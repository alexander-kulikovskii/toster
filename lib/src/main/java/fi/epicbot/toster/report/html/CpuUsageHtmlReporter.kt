package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.extension.saveForPath
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

        reportOutput.devices.forEach { device ->
            generateDeviceChartBuilder(shellExecutor, device)
            generateDeviceCpuData(shellExecutor, device)
            generateDeviceCpuPage(shellExecutor, reportOutput.appInfo.appName, device)
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
            OUTPUT_CPU_USAGE_BUILDER_NAME,
            chartBuilder
        )
    }

    private fun generateDeviceCpuData(
        shellExecutor: ShellExecutor,
        device: ReportDevice
    ) {
        val chartData = device.userScreens().mapIndexed { index, reportScreen ->
            val data = reportScreen.cpuUsage.joinToString(
                separator = ", ",
                transform = { it.measurement.user.toString() }
            )

            val firstCpuUsageEndTime = reportScreen.cpuUsage.firstOrNull()?.endTime ?: 0L
            val indexStr = reportScreen.cpuUsage.map {
                (it.endTime - firstCpuUsageEndTime) / MILLIS_IN_SECOND
            }.joinToString(separator = ", ")

            """
                var labelName = "CPU"
                var labels$index = [$indexStr]
                var dataSets$index = [
                {
                    label: "Cpu usage",
                    data: [$data],
                    fill: true,
                    borderColor: "rgb(73, 128, 135)",
                    backgroundColor: "rgba(73, 128, 135, 0.8)",
                    tension: 0.3
                },
                ]
            """.trimIndent()
        }.joinToString(separator = "\n")

        shellExecutor.makeFileForChart(
            device.cpuDir(),
            "cpu_data.js",
            chartData
        )
    }

    private fun generateDeviceCpuPage(
        shellExecutor: ShellExecutor,
        appName: String,
        device: ReportDevice
    ) {
        val cpuBody = getTemplate(CPU_TEMPLATE)
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

    private fun ReportDevice.userScreens() = reportScreens.dropLast(1).drop(1)

    private fun ReportDevice.cpuDir() = "${device.name.saveForPath()}/cpu"

    private fun createChart(index: Int, screen: ReportScreen) = createHTML().div {
        h2 {
            text(screen.name)
        }
        canvas {
            id = "chart$index"
            height = "80"
        }
    }

    private companion object {
        private const val MILLIS_IN_SECOND = 1000.0
    }
}
