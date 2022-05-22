package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.model.ReportBuild
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportOutput
import fi.epicbot.toster.report.model.Screenshot
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.h4
import kotlinx.html.img
import kotlinx.html.stream.createHTML

internal class CollageHtmlReporter : BaseHtmlReporter() {

    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger,
    ) {

        reportOutput.builds.forEach { build ->
            build.devices.forEach { device ->
                generateDeviceCollagePage(
                    shellExecutor,
                    reportOutput.appInfo.appName,
                    device,
                    reportOutput
                )
            }
        }
    }

    private fun generateDeviceCollagePage(
        shellExecutor: ShellExecutor,
        appName: String,
        device: ReportDevice,
        reportOutput: ReportOutput,
    ) {
        var collageContent = ""

        device.userScreens().forEach { screen ->
            screen.screenshots.forEach { screenshot ->
                reportOutput.builds.forEach { build ->
                    collageContent += generateScreenshotHtml(build, screenshot)
                }
            }
        }

        val collageBody = getTemplate(COLLAGE_TEMPLATE_NAME)
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

    private fun ReportDevice.collageDir() = "${device.name.safeForPath()}/collage"

    private fun generateScreenshotHtml(build: ReportBuild, screenshot: Screenshot) =
        createHTML().div {
            h3 {
                text("${screenshot.name} ${screenshot.index}")
            }
            h4 {
                text(build.name)
            }
            img {
                src = "../../../${build.name.safeForPath()}/${screenshot.localUrl}"
                height = DEFAULT_SCREENSHOT_HEIGHT
            }
        }
}
