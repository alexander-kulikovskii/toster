package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.extension.saveForPath
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportOutput
import fi.epicbot.toster.report.model.Screenshot
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.img
import kotlinx.html.stream.createHTML

internal class CollageHtmlReporter : BaseHtmlReporter() {

    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger,
    ) {

        reportOutput.devices.forEach { device ->
            generateDeviceCollagePage(shellExecutor, reportOutput.appInfo.appName, device)
        }
    }

    private fun generateDeviceCollagePage(
        shellExecutor: ShellExecutor,
        appName: String,
        device: ReportDevice
    ) {
        var collageContent = ""
        device.userScreens().forEach { screen ->
            screen.screenshots.forEach { screenshot ->
                collageContent += generateScreenshotHtml(screenshot)
            }
        }

        val collageBody = getTemplate(COLLAGE_TEMPLATE)
            .replace(APP_NAME_PLACEHOLDER, appName)
            .replace(DEVICE_NAME_PLACEHOLDER, device.device.name)
            .replace(GENERATED_WITH_PLACEHOLDER, getGenerateWithHtml())
            .replace(COLLAGE_HOLDER_VERSION, collageContent)

        shellExecutor.makeFileForChart(
            device.collageDir(),
            "index.html",
            collageBody
        )
    }

    private fun ReportDevice.userScreens() = reportScreens.dropLast(1).drop(1)

    private fun ReportDevice.collageDir() = "${device.name.saveForPath()}/collage"

    private fun generateScreenshotHtml(screenshot: Screenshot) = createHTML().div {
        h3 {
            text("${screenshot.name} ${screenshot.index}")
        }
        img {
            src = "../../../${screenshot.localUrl}"
            height = DEFAULT_HEIGHT
        }
    }

    private companion object {
        private const val DEFAULT_HEIGHT = "500"
    }
}
