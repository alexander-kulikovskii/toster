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

internal class MemoryUsageHtmlReporter : BaseHtmlReporter() {

    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger,
    ) {
        reportOutput.builds.first().devices.forEach { device ->
            generateDeviceChartBuilder(shellExecutor, device)
            generateDeviceMemoryPage(shellExecutor, reportOutput.appInfo.appName, device)
            generateDeviceMemoryData(shellExecutor, reportOutput, device)
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
                    data: data$index
                });
            """.trimIndent()
        }

        shellExecutor.makeFileForChart(
            device.memoryDir(),
            CHART_BUILDER_NAME,
            chartBuilder
        )
    }

    private fun generateDeviceMemoryData(
        shellExecutor: ShellExecutor,
        reportOutput: ReportOutput,
        device: ReportDevice,
    ) {
        val chartData = device.userScreens().mapIndexed { index, reportScreen ->

            val firstMemoryUsageEndTime = reportScreen.memory.firstOrNull()?.endTime ?: 0L
            val indexStr = reportScreen.memory.map {
                (it.endTime - firstMemoryUsageEndTime) / MILLIS_IN_SECOND
            }.joinToString(separator = ", ")

            """
                var labelName = "Memory"
                var labels$index = [$indexStr]
                var dataSets$index = [
                ${getDataSet(index, reportOutput)}
                ]
            """.trimIndent()
        }.joinToString(separator = "\n")

        shellExecutor.makeFileForChart(
            device.memoryDir(),
            "memory_data.js",
            chartData
        )
    }

    private fun getDataSet(screenIndex: Int, reportOutput: ReportOutput): String {
        var res = ""
        reportOutput.builds.forEachIndexed { index, build ->
            val buildName = build.name.safeForPath()
            build.devices.forEach { device ->
                val reportScreen = device.userScreens()[screenIndex]
                val dataDalvik = reportScreen.memory.joinToString(
                    separator = ", ",
                    transform = { (it.measurements["Dalvik Heap"]?.memory ?: 0).toString() }
                )
                val dataNative = reportScreen.memory.joinToString(
                    separator = ", ",
                    transform = { (it.measurements["Native Heap"]?.memory ?: 0).toString() }
                )
                res += """
                {
                    label: "Dalvik memory ($buildName)",
                    data: [$dataDalvik],
                    fill: $FILL_CHART,
                    borderColor: "${getColorByIndex(index * 2)}",
                    backgroundColor: "${getColorByIndex(index * 2, transparent = true)}",
                    tension: 0.3
                },
                {
                    label: "Native memory ($buildName)",
                    data: [$dataNative],
                    fill: $FILL_CHART,
                    borderColor: "${getColorByIndex(index * 2 + 1)}",
                    backgroundColor: "${getColorByIndex(index * 2 + 1, transparent = true)}",
                    tension: 0.3
                },
                
                """.trimIndent()
            }
        }
        return res
    }

    private fun generateDeviceMemoryPage(
        shellExecutor: ShellExecutor,
        appName: String,
        device: ReportDevice
    ) {
        val memoryBody = getTemplate(MEMORY_TEMPLATE_NAME)
            .replace(APP_NAME_PLACEHOLDER, appName)
            .replace(DEVICE_NAME_PLACEHOLDER, device.device.name)
            .replace(GENERATED_WITH_PLACEHOLDER, getGenerateWithHtml())
            .replace(METRICS_HOLDER_VERSION, getAllCharts(device))

        shellExecutor.makeFileForChart(
            device.memoryDir(),
            "index.html",
            memoryBody
        )
    }

    private fun getAllCharts(device: ReportDevice): String {
        return device.userScreens().mapIndexed { index, screen ->
            createChart(index, screen)
        }.joinToString(separator = "\n")
    }

    private fun ReportDevice.memoryDir() = "${device.name.safeForPath()}/memory"

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
