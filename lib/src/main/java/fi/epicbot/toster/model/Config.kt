package fi.epicbot.toster.model

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.Reporter
import fi.epicbot.toster.report.model.ReportAppInfo
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportOutput

@Suppress("LongParameterList", "MagicNumber")
class Config(
    var applicationName: String = "",
    var applicationPackageName: String = "",
    var apkUrl: String = "",
    var emulatorPath: String = "",
    var fontScale: FontScale = FontScale.DEFAULT,
    var checkOverdraw: Overdraw = Overdraw(),
    var reportConfig: ReportConfig = ReportConfig(),
    var collage: Collage = Collage(),
    var clearDataBeforeEachRun: Boolean = false,
    var devices: Devices = Devices(),
    var shellBeforeAllScreens: String = "",
    var shellAfterAllScreens: String = "",
    var permissions: Permissions = Permissions(),
    var testTimeoutMillis: Long = 600 * 1000L,
    var deleteAndInstallApk: Boolean = true,
    var swipeOffset: SwipeOffset = SwipeOffset(offsetPx = 220, offsetFactor = 0.08),
    var useDemoMode: Boolean = true
)

data class Overdraw(
    val check: Boolean = false,
    val threshold: Double = 0.0,
)

internal fun Config.makeReport(
    reportDevices: MutableList<ReportDevice>,
    testTime: Long,
    defaultReporter: Reporter,
    shellExecutor: ShellExecutor,
    shellLogger: ShellLogger,
) {
    if (reportConfig.enable.not()) {
        return
    }
    reportDevices.forEach { reportDevice ->
        reportDevice.reportScreens.forEach { reportScreen ->
            val tmp = reportScreen.screenshots.map { screenshot ->
                screenshot.copy(pathUrl = "${shellExecutor.workingDir}/${screenshot.pathUrl}")
            }
            reportScreen.screenshots.clear()
            reportScreen.screenshots.addAll(tmp)
        }
    }
    val reportOutput = ReportOutput(
        ReportAppInfo(applicationName, testTime),
        reportDevices,
    )
    (listOf(defaultReporter) + reportConfig.customReporters).forEach { reporter ->
        reporter.makeReport(reportOutput, shellExecutor, shellLogger)
    }
}
