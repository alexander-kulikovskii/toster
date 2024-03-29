package fi.epicbot.toster.model

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.Reporter
import fi.epicbot.toster.report.model.ReportAppInfo
import fi.epicbot.toster.report.model.ReportBuild
import fi.epicbot.toster.report.model.ReportOutput

@Suppress("LongParameterList", "MagicNumber")
class Config(
    var applicationName: String = "",
    var applicationPackageName: String = "",
    var multiApk: MultiApk = MultiApk(),
    var emulatorPath: String = "",
    var fontScale: FontScale? = null,
    var checkOverdraw: Overdraw = Overdraw(),
    var reportConfig: ReportConfig = ReportConfig(),
    var shellLoggerConfig: ShellLoggerConfig = ShellLoggerConfig(),
    var collage: Collage = Collage(),
    var clearDataBeforeEachRun: Boolean = false,
    var devices: Devices = Devices(),
    var shellsBeforeAllScreens: Array<out String> = emptyArray(),
    var blockBeforeAllScreens: () -> Unit = {},
    var shellsAfterAllScreens: Array<out String> = emptyArray(),
    var blockAfterAllScreens: () -> Unit = {},
    var permissions: Permissions = Permissions(),
    var testTimeoutMillis: Long = 600 * 1000L,
    var deleteAndInstallApk: Boolean = true,
    var horizontalSwipeOffset: SwipeOffset.HorizontalSwipeOffset = SwipeOffset.HorizontalSwipeOffset(
        offsetPx = 160,
        offsetFactor = 0.05
    ),
    var verticalSwipeOffset: SwipeOffset.VerticalSwipeOffset = SwipeOffset.VerticalSwipeOffset(
        offsetPx = 220,
        offsetFactor = 0.08
    ),
    var useDemoMode: Boolean = true,
    var failFast: Boolean = true,
    var restartAdbServiceBeforeEachDevice: Boolean = false,
    var globalScreenDensity: Density? = null,
    var globalScreenSize: ScreenSize? = null,
    var globalLogcatBufferSize: BufferSize? = null,
    var demoModeTime: String = "1300",

)

class Overdraw(
    val check: Boolean = false,
    val threshold: Double = 0.0,
)

@Suppress("LongParameterList")
internal fun Config.makeReport(
    reportBuilds: MutableList<ReportBuild>,
    testTime: Long,
    defaultReporter: Reporter,
    htmlReporterFacade: Reporter,
    shellExecutor: ShellExecutor,
    shellLogger: ShellLogger,
) {
    if (reportConfig.enable.not()) {
        return
    }
    reportBuilds.forEach { reportBuild ->
        reportBuild.devices.forEach { reportDevice ->
            val deviceName = reportDevice.device.name.safeForPath()
            reportDevice.reportScreens.forEach { reportScreen ->
                val tmp = reportScreen.screenshots.map { screenshot ->
                    screenshot.copy(
                        pathUrl = "${shellExecutor.workingDir}/$deviceName/${screenshot.pathUrl}",
                        localUrl = "$deviceName/${screenshot.pathUrl}"
                    )
                }
                reportScreen.screenshots.clear()
                reportScreen.screenshots.addAll(tmp)
            }
        }
    }
    val reportOutput = ReportOutput(
        ReportAppInfo(applicationName, testTime),
        reportBuilds,
    )
    (listOf(htmlReporterFacade, defaultReporter) + reportConfig.customReporters).forEach { reporter ->
        reporter.makeReport(reportOutput, shellExecutor, shellLogger)
    }
}

internal fun Config.projectDir() = "/build/toster/${applicationName.safeForPath()}"
