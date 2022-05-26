package fi.epicbot.toster

import fi.epicbot.toster.checker.ApkChecker
import fi.epicbot.toster.checker.ConfigChecker
import fi.epicbot.toster.checker.ScreensChecker
import fi.epicbot.toster.context.ConfigContext
import fi.epicbot.toster.context.ScreensContext
import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.executor.android.AndroidExecutor
import fi.epicbot.toster.executor.android.EmulatorExecutor
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.logger.DefaultLogger
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Apk
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.makeReport
import fi.epicbot.toster.model.runAction
import fi.epicbot.toster.model.runShellAction
import fi.epicbot.toster.model.toStringParams
import fi.epicbot.toster.parser.ParserProvider
import fi.epicbot.toster.report.DefaultReporter
import fi.epicbot.toster.report.formatter.JsonFormatter
import fi.epicbot.toster.report.html.HtmlReporterFacade
import fi.epicbot.toster.report.model.ReportBuild
import fi.epicbot.toster.report.model.ReportCollage
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.time.DefaultTimeProvider
import fi.epicbot.toster.time.TimeProvider
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

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
        val apkChecker = ApkChecker(config.multiApk!!.apks)
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
                        "/build/toster/${config.applicationName.safeForPath()}",
                        apk.prefix,
                        shellLogger,
                        index == 0,
                    )
                runShellsForApk(shellExecutor = shellExecutor, timeProvider = timeProvider, apk)

                config.devices.emulators.forEach { emulator ->
                    val actionExecutor = EmulatorExecutor(
                        serialName = emulator.name,
                        config = config,
                        startDelayMillis = emulator.startDelayMillis,
                        shellExecutor = shellExecutor,
                        parserProvider = parserProvider,
                        timeProvider = timeProvider,
                    )
                    runScreens(actionExecutor, config, apk, screens, reportDevices)
                }
                config.devices.phones.forEach { phone ->
                    val actionExecutor = AndroidExecutor(
                        serialName = phone.uuid,
                        config = config,
                        shellExecutor = shellExecutor,
                        parserProvider = parserProvider,
                        timeProvider = timeProvider,
                    )
                    runScreens(actionExecutor, config, apk, screens, reportDevices)
                }
                reportBuilds.add(
                    ReportBuild(
                        name = apk.prefix,
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

private fun makeReport(
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
            "/build/toster/${config.applicationName.safeForPath()}",
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

private suspend fun DescribeSpecContainerScope.runShellsForApk(
    shellExecutor: ShellExecutor,
    timeProvider: TimeProvider,
    apk: Apk,
): ReportScreen {
    val apkReport = ReportScreen(name = "Before")
    apk.shellsBefore.forEach { shell ->
        shell.runShellAction(
            timeProvider,
            shellExecutor,
            apkReport,
            executeCondition = shell.isNotBlank(),
        )
    }
    return apkReport
}

private suspend fun DescribeSpecContainerScope.runBeforeScreens(
    actionExecutor: ActionExecutor,
    config: Config,
    apk: Apk,
): ReportScreen {
    val beforeScreen = ReportScreen(name = "Before")
    actionExecutor.run {
        Action.RestartAdbService.runAction(
            this,
            beforeScreen,
            executeCondition = config.restartAdbServiceBeforeEachDevice,
        )
        prepareEnvironment()
        config.globalLogcatBufferSize?.let { logcatBufferSize ->
            Action.SetLogcatBufferSize(logcatBufferSize).runAction(this, beforeScreen)
        }
        config.shellsBeforeAllScreens.forEach { shellBeforeAllScreens ->
            Action.ShellBeforeAllScreens(shellBeforeAllScreens).runAction(
                this,
                beforeScreen,
                executeCondition = shellBeforeAllScreens.isNotBlank(),
            )
        }
        config.globalScreenDensity?.let { screenDensity ->
            Action.SetScreenDensity(screenDensity).runAction(this, beforeScreen)
        }
        config.globalScreenSize?.let { screenSize ->
            Action.SetScreenSize(screenSize).runAction(this, beforeScreen)
        }

        if (config.deleteAndInstallApk) {
            listOf(
                Action.ClearAppData,
                Action.DeleteApk,
                Action.InstallApk(apk.url)
            ).forEach { action ->
                action.runAction(this, beforeScreen)
            }
        }

        Action.HideDemoMode.runAction(this, beforeScreen)

        if (config.useDemoMode) {
            Action.SetDemoModeEnable.runAction(this, beforeScreen)
            Action.ShowDemoMode(config.demoModeTime).runAction(this, beforeScreen)
        }

        Action.HideGpuOverdraw.runAction(this, beforeScreen)
    }
    return beforeScreen
}

internal suspend fun DescribeSpecContainerScope.resetScreenSizeAndDensity(
    actionExecutor: ActionExecutor,
    config: Config,
    afterScreen: ReportScreen,
) {
    Action.ResetScreenSize.runAction(
        actionExecutor,
        afterScreen,
        executeCondition = config.globalScreenSize != null
    )
    Action.ResetScreenDensity.runAction(
        actionExecutor,
        afterScreen,
        executeCondition = config.globalScreenDensity != null
    )
}

internal suspend fun DescribeSpecContainerScope.setScreenSizeAndDensity(
    actionExecutor: ActionExecutor,
    screen: Screen,
    reportScreen: ReportScreen,
    imagePrefix: String,
) {
    screen.screenDensity?.let { screenDensity ->
        Action.SetScreenDensity(screenDensity).runAction(
            actionExecutor,
            reportScreen,
            imagePrefix,
        )
    }

    screen.screenSize?.let { screenSize ->
        Action.SetScreenSize(screenSize).runAction(
            actionExecutor,
            reportScreen,
            imagePrefix,
        )
    }
}

internal suspend fun DescribeSpecContainerScope.resetScreenSizeAndDensity(
    actionExecutor: ActionExecutor,
    config: Config,
    screen: Screen,
    reportScreen: ReportScreen,
    imagePrefix: String,
) {
    Action.ResetScreenSize.runAction(
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.screenSize != null
    )
    Action.ResetScreenDensity.runAction(
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.screenDensity != null
    )
    config.globalScreenDensity?.let { screenDensity ->
        Action.SetScreenDensity(screenDensity).runAction(
            actionExecutor,
            reportScreen,
            imagePrefix,
            screen.screenDensity != null,
        )
    }
    config.globalScreenSize?.let { screenSize ->
        Action.SetScreenSize(screenSize).runAction(
            actionExecutor,
            reportScreen,
            imagePrefix,
            screen.screenSize != null,
        )
    }
}

private suspend fun DescribeSpecContainerScope.runAfterScreens(
    actionExecutor: ActionExecutor,
    config: Config,
): ReportScreen {
    val afterScreen = ReportScreen("After")
    actionExecutor.run {
        if (config.useDemoMode) {
            Action.HideDemoMode.runAction(this, afterScreen)
        }
        config.shellsAfterAllScreens.forEach { shellAfterAllScreens ->
            Action.ShellAfterAllScreens(shellAfterAllScreens).runAction(
                this,
                afterScreen,
                executeCondition = shellAfterAllScreens.isNotBlank(),
            )
        }
        resetScreenSizeAndDensity(actionExecutor, config, afterScreen)
        finishEnvironment()
    }
    return afterScreen
}

private suspend fun DescribeSpecContainerScope.runScreens(
    actionExecutor: ActionExecutor,
    config: Config,
    apk: Apk,
    screens: List<Screen>,
    reportDevices: MutableList<ReportDevice>,
) = describe(actionExecutor.executor().toString()) {

    val reportScreens: MutableList<ReportScreen> = mutableListOf()
    val beforeScreenReport = runBeforeScreens(actionExecutor, config, apk)

    screens.forEach { screen ->
        val reportScreen = ReportScreen(name = screen.name)
        runScreen(config, actionExecutor, screen, reportScreen, "normal")
        if (config.checkOverdraw.check) {
            Action.ShowGpuOverdraw.runAction(actionExecutor, reportScreen, "overdraw")
            runScreen(config, actionExecutor, screen, reportScreen, "overdraw")
            Action.HideGpuOverdraw.runAction(actionExecutor, reportScreen, "overdraw")
            // TODO run tests for getting overdraw info
        }
        // TODO compare screenshots
        reportScreens.add(reportScreen)
    }
    val afterScreenReport = runAfterScreens(actionExecutor, config)

    reportDevices.add(
        ReportDevice(
            device = actionExecutor.executor(),
            reportScreens = listOf(beforeScreenReport) + reportScreens + listOf(afterScreenReport),
            collage = ReportCollage(),
        )
    )
}

@Suppress("LongMethod")
private suspend fun DescribeSpecContainerScope.runScreen(
    config: Config,
    actionExecutor: ActionExecutor,
    screen: Screen,
    reportScreen: ReportScreen,
    imagePrefix: String,
) = describe("Screen: ${screen.name}; $imagePrefix") {

    screen.shellsBefore.forEach { shellBefore ->
        Action.ShellBeforeScreen(shellBefore).runAction(
            actionExecutor,
            reportScreen,
            imagePrefix,
            shellBefore.isNotBlank(),
        )
    }
    Action.ClearLogcat.runAction(
        actionExecutor,
        reportScreen,
        imagePrefix,
        executeCondition = screen.clearLogcatBefore
    )

    setScreenSizeAndDensity(actionExecutor, screen, reportScreen, imagePrefix)

    if (config.clearDataBeforeEachRun || screen.clearDataBeforeRun) {
        Action.ClearAppData.runAction(actionExecutor, reportScreen, imagePrefix)
    }

    (config.permissions.granted + screen.permissions.granted).forEach {
        Action.GrantPermission(it).runAction(actionExecutor, reportScreen, imagePrefix)
    }
    screen.permissions.revoked.forEach {
        Action.RevokePermission(it).runAction(actionExecutor, reportScreen, imagePrefix)
    }

    val fontScale = if (screen.fontScale != null && screen.fontScale != FontScale.DEFAULT) {
        screen.fontScale
    } else {
        config.fontScale
    }
    fontScale?.let { fontScale ->
        Action.SetFontScale(fontScale).runAction(actionExecutor, reportScreen, imagePrefix)
    }

    Action.CloseAppsInTray.runAction(
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.closeAppsInTrayBeforeStart,
    )
    Action.ResetGfxInfo.runAction(
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.resetGfxInfoBeforeStart,
    )

    val activityParamsAsString = screen.activityParams.toStringParams()
    Action.OpenScreen(screen, activityParamsAsString).runAction(
        actionExecutor,
        reportScreen,
        imagePrefix,
    )

    screen.actions.forEach { action ->
        action.runAction(actionExecutor, reportScreen, imagePrefix)
    }

    if (screen.screenshotAsLastAction && screen.actions.count { it is Action.TakeScreenshot } == 0) {
        Action.TakeScreenshot("").runAction(actionExecutor, reportScreen, imagePrefix)
    }

    fontScale?.let {
        Action.SetFontScale(FontScale.DEFAULT).runAction(actionExecutor, reportScreen, imagePrefix)
    }

    Action.CloseApp.runAction(actionExecutor, reportScreen, imagePrefix)

    resetScreenSizeAndDensity(actionExecutor, config, screen, reportScreen, imagePrefix)

    screen.shellsAfter.forEach { shellAfter ->
        Action.ShellAfterScreen(shellAfter).runAction(
            actionExecutor,
            reportScreen,
            imagePrefix,
            shellAfter.isNotBlank(),
        )
    }
}
