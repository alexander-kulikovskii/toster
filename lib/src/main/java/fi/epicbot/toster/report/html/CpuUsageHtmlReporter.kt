package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportOutput
import fi.epicbot.toster.report.model.ReportScreen
import kotlinx.html.canvas
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.stream.createHTML

internal class CpuUsageHtmlReporter : BaseHtmlReporter() {

    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger,
    ) {
        reportOutput.builds.first().devices.forEach { device ->
            generateDeviceChartBuilder(shellExecutor, device)
            generateDeviceCpuPage(shellExecutor, reportOutput.appInfo.appName, device)
            generateDeviceCpuData(shellExecutor, reportOutput, device)
        }
    }

    private fun generateDeviceChartBuilder(shellExecutor: ShellExecutor, device: ReportDevice) {
        var chartBuilder = ""
        device.userScreens().forEachIndexed { index, _ ->
            chartBuilder += """
                const ctx$index = document.getElementById('chart$index');
                                
                const data$index = {
                    labels: labels$index,
                    datasets: dataSets$index,
                };
                                
                const chart$index = new Chart(ctx$index, {
                    type: 'line',
                    data: data$index,
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                suggestedMin: 0,
                                suggestedMax: 100,
                            }
                        }
                    }
                });
            """.trimIndent()
        }

        shellExecutor.makeFileForChart(
            device.cpuDir(),
            CHART_BUILDER_NAME,
            chartBuilder
        )
    }

    private fun generateDeviceCpuData(
        shellExecutor: ShellExecutor,
        reportOutput: ReportOutput,
        device: ReportDevice,
    ) {
        val chartData = device.userScreens().mapIndexed { index, reportScreen ->
            val firstCpuUsageEndTime = reportScreen.cpuUsage.firstOrNull()?.endTime ?: 0L
            val indexStr = reportScreen.cpuUsage.map {
                (it.endTime - firstCpuUsageEndTime) / MILLIS_IN_SECOND
            }.joinToString(separator = ", ")
            """
                var labelName = "CPU"
                var labels$index = [$indexStr]
                var dataSets$index = [
                ${getDataSet(index, reportOutput)}
                ]
            """.trimIndent()
        }.joinToString(separator = "\n")

        shellExecutor.makeFileForChart(
            device.cpuDir(),
            "cpu_data.js",
            chartData
        )
    }

    private fun getDataSet(screenIndex: Int, reportOutput: ReportOutput): String {
        var res = ""
        reportOutput.builds.forEachIndexed { index, build ->
            val buildName = build.name.safeForPath()
            build.devices.forEach { device ->
                val data = device.userScreens()[screenIndex].cpuUsage.joinToString(
                    separator = ", ",
                    transform = { it.measurement.user.toString() }
                )
                res += """
                {
                    label: "$buildName",
                    data: [$data],
                    fill: $FILL_CHART,
                    borderColor: "${getColorByIndex(index)}",
                    backgroundColor: "${getColorByIndex(index, transparent = true)}",
                    tension: 0.3
                },
                
                """.trimIndent()
            }
        }
        return res
    }

    private fun generateDeviceCpuPage(
        shellExecutor: ShellExecutor,
        appName: String,
        device: ReportDevice
    ) {
        val cpuBody = getTemplate(CPU_TEMPLATE_NAME)
            .replace(APP_NAME_PLACEHOLDER, appName)
            .replace(DEVICE_NAME_PLACEHOLDER, device.device.name)
            .replace(GENERATED_WITH_PLACEHOLDER, getGenerateWithHtml())
            .replace(METRICS_HOLDER_VERSION, getAllCharts(device))

        shellExecutor.makeFileForChart(
            device.cpuDir(),
            "index.html",
            cpuBody
        )
    }

    private fun getAllCharts(device: ReportDevice): String {
        return device.userScreens().mapIndexed { index, screen ->
            createChart(index, screen)
        }.joinToString(separator = "\n")
    }

    private fun ReportDevice.cpuDir() = "${device.name.safeForPath()}/cpu"

    private fun createChart(index: Int, screen: ReportScreen) = createHTML().div {
        h2 {
            text(screen.name)
        }
        canvas {
            id = "chart$index"
            height = CHART_HEIGHT
        }
    }
}
