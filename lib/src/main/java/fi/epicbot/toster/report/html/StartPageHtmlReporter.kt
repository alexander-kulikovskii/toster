package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportOutput
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.li
import kotlinx.html.stream.createHTML
import kotlinx.html.ul

internal class StartPageHtmlReporter : BaseHtmlReporter() {

    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger,
    ) {
        generateMainPage(shellExecutor, reportOutput)
        copyStyleFile(shellExecutor)
        copyChartFile(shellExecutor)
        reportOutput.devices.forEach { device ->
            generateDevicePage(shellExecutor, reportOutput.appInfo.appName, device)
        }
    }

    private fun copyChartFile(shellExecutor: ShellExecutor) {
        val chartFile = getTemplate(CHART_TEMPLATE)
        shellExecutor.makeFileForChart(fileName = CHART_TEMPLATE, content = chartFile)
    }

    private fun copyStyleFile(shellExecutor: ShellExecutor) {
        val styleFile = getTemplate(STYLE_TEMPLATE)
        shellExecutor.makeFileForChart(fileName = STYLE_TEMPLATE, content = styleFile)
    }

    private fun generateMainPage(shellExecutor: ShellExecutor, reportOutput: ReportOutput) {
        val htmlBody = getTemplate(MAIN_TEMPLATE)
            .replace(APP_NAME_PLACEHOLDER, reportOutput.appInfo.appName)
            .replace(GENERATED_WITH_PLACEHOLDER, getGenerateWithHtml())
            .replace(DEVICES_PLACEHOLDER, createDeviceListHtml(reportOutput.devices))
        shellExecutor.makeFileForChart(fileName = "index.html", content = htmlBody)
    }

    private fun generateDevicePage(
        shellExecutor: ShellExecutor,
        appName: String,
        device: ReportDevice
    ) {
        val htmlBody = getTemplate(DEVICE_TEMPLATE)
            .replace(APP_NAME_PLACEHOLDER, appName)
            .replace(DEVICE_NAME_PLACEHOLDER, device.device.name)
            .replace(GENERATED_WITH_PLACEHOLDER, getGenerateWithHtml())
        shellExecutor.makeFileForChart(device.device.name, "index.html", htmlBody)
    }

    private fun createDeviceListHtml(devices: MutableList<ReportDevice>) = createHTML().div {
        ul {
            devices.forEach {
                li {
                    a(href = "${it.device.name}/index.html") {
                        text(it.device.name)
                    }
                }
            }
        }
    }
}
