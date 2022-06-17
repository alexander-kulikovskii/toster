package fi.epicbot.toster

import fi.epicbot.toster.checker.ApkChecker
import fi.epicbot.toster.checker.ConfigChecker
import fi.epicbot.toster.checker.ScreensChecker
import fi.epicbot.toster.context.ConfigContext
import fi.epicbot.toster.context.ScreensContext
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.executor.android.AndroidExecutor
import fi.epicbot.toster.executor.android.EmulatorExecutor
import fi.epicbot.toster.extension.runScreens
import fi.epicbot.toster.extension.runShellsForApk
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.logger.DefaultLogger
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.makeReport
import fi.epicbot.toster.model.projectDir
import fi.epicbot.toster.parser.ParserProvider
import fi.epicbot.toster.report.DefaultReporter
import fi.epicbot.toster.report.formatter.JsonFormatter
import fi.epicbot.toster.report.html.HtmlReporterFacade
import fi.epicbot.toster.report.model.ReportBuild
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.time.DefaultTimeProvider
import io.kotest.core.spec.style.DescribeSpec

object Screens {
    operator fun invoke(init: ScreensContext.() -> Unit): List<Screen> {
        val screens = ScreensContext().apply(init).screens
        val screensChecker = ScreensChecker(screens)
        screensChecker.check()
        return screens
    }
}

object Config {
    operator fun invoke(init: ConfigContext.() -> Unit): Config {
        val config = ConfigContext().apply(init).config
        val configChecker = ConfigChecker(config)
        configChecker.check()
        val apkChecker = ApkChecker(config.multiApk.apks)
        apkChecker.check()
        return config
    }
}

@Suppress("UnnecessaryAbstractClass")
abstract class TosterTest(config: Config, screens: List<Screen>) : DescribeSpec({

    timeout = config.testTimeoutMillis

    failfast = config.failFast

    describe(config.applicationName) {

        val timeProvider = DefaultTimeProvider()
        val startTestTime = timeProvider.getTimeMillis()
        val shellLogger = DefaultLogger(timeProvider)
        val reportBuilds = mutableListOf<ReportBuild>()
        val parserProvider = ParserProvider()

        config.multiApk.apks.forEachIndexed { index, apk ->
            describe(apk.prefix) {
                val reportDevices = mutableListOf<ReportDevice>()
                val shellExecutor =
                    ShellExecutor(
                        config.projectDir(),
                        apk.prefix.safeForPath(),
                        shellLogger,
                        index == 0,
                    )
                shellExecutor.runShellsForApk(timeProvider = timeProvider, apk)

                config.devices.emulators.forEach { emulator ->
                    val actionExecutor = EmulatorExecutor(
                        serialName = emulator.name,
                        config = config,
                        startDelayMillis = emulator.startDelayMillis,
                        shellExecutor = shellExecutor,
                        parserProvider = parserProvider,
                        timeProvider = timeProvider,
                    )
                    actionExecutor.runScreens(config, apk, screens, reportDevices)
                }
                config.devices.phones.forEach { phone ->
                    val actionExecutor = AndroidExecutor(
                        serialName = phone.uuid,
                        config = config,
                        shellExecutor = shellExecutor,
                        parserProvider = parserProvider,
                        timeProvider = timeProvider,
                    )
                    actionExecutor.runScreens(config, apk, screens, reportDevices)
                }
                reportBuilds.add(
                    ReportBuild(
                        name = apk.prefix.safeForPath(),
                        devices = reportDevices,
                    )
                )
            }
        }

        makeReport(
            config,
            reportBuilds,
            testTime = timeProvider.getTimeMillis() - startTestTime,
            shellLogger,
        )
    }
})

internal fun makeReport(
    config: Config,
    reportBuilds: MutableList<ReportBuild>,
    testTime: Long,
    shellLogger: ShellLogger,
) {
    val defaultReporter = DefaultReporter(
        JsonFormatter(prettyPrintJson = true),
        config.shellLoggerConfig,
    )
    val htmlReporterFacade = HtmlReporterFacade()
    val shellExecutor =
        ShellExecutor(
            config.projectDir(),
            "",
            shellLogger,
            false
        )
    config.makeReport(
        reportBuilds,
        testTime,
        defaultReporter,
        htmlReporterFacade,
        shellExecutor,
        shellLogger,
    )
}
