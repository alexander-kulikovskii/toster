package fi.epicbot.toster

import fi.epicbot.toster.checker.ConfigChecker
import fi.epicbot.toster.checker.ScreensChecker
import fi.epicbot.toster.context.ConfigContext
import fi.epicbot.toster.context.ScreensContext
import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.executor.android.AndroidExecutor
import fi.epicbot.toster.executor.android.EmulatorExecutor
import fi.epicbot.toster.extension.saveForPath
import fi.epicbot.toster.logger.DefaultLogger
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.makeReport
import fi.epicbot.toster.model.runAction
import fi.epicbot.toster.model.toStringParams
import fi.epicbot.toster.parser.CpuUsageParser
import fi.epicbot.toster.parser.DumpSysParser
import fi.epicbot.toster.parser.GfxInfoParser
import fi.epicbot.toster.report.DefaultReporter
import fi.epicbot.toster.report.formatter.JsonFormatter
import fi.epicbot.toster.report.html.HtmlReporterFacade
import fi.epicbot.toster.report.model.ReportCollage
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.time.DefaultTimeProvider
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerContext

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
        return config
    }
}

abstract class TosterTest(config: Config, screens: List<Screen>) : DescribeSpec({

    timeout = config.testTimeoutMillis

    failfast = config.failFast

    describe(config.applicationName) {
        val timeProvider = DefaultTimeProvider()
        val startTestTime = timeProvider.getTimeMillis()
        val reportDevices = mutableListOf<ReportDevice>()
        val shellLogger = DefaultLogger(timeProvider)
        val shellExecutor =
            ShellExecutor("/build/toster/${config.applicationName.saveForPath()}", shellLogger)
        val dumpSysParser = DumpSysParser()
        val gfxInfoParser = GfxInfoParser()
        val cpuUsageParser = CpuUsageParser()

        config.devices.emulators.forEach { emulator ->
            val actionExecutor = EmulatorExecutor(
                serialName = emulator.name,
                config = config,
                startDelayMillis = emulator.startDelayMillis,
                shellExecutor = shellExecutor,
                dumpSysParser = dumpSysParser,
                gfxInfoParser = gfxInfoParser,
                cpuUsageParser = cpuUsageParser,
                timeProvider = timeProvider,
            )
            runScreens(actionExecutor, config, screens, reportDevices)
        }
        config.devices.phones.forEach { phone ->
            val actionExecutor = AndroidExecutor(
                serialName = phone.uuid,
                config = config,
                shellExecutor = shellExecutor,
                dumpSysParser = dumpSysParser,
                gfxInfoParser = gfxInfoParser,
                cpuUsageParser = cpuUsageParser,
                timeProvider = timeProvider,
            )
            runScreens(actionExecutor, config, screens, reportDevices)
        }

        val endTestTime = timeProvider.getTimeMillis()

        val defaultReporter = DefaultReporter(
            JsonFormatter(prettyPrintJson = true),
            config.shellLoggerConfig,
        )
        val htmlReporterFacade = HtmlReporterFacade()
        config.makeReport(
            reportDevices,
            endTestTime - startTestTime,
            defaultReporter,
            htmlReporterFacade,
            shellExecutor,
            shellLogger,
        )
    }
})

private suspend fun DescribeSpecContainerContext.runBeforeScreens(
    actionExecutor: ActionExecutor,
    config: Config,
): ReportScreen {
    val beforeScreen = ReportScreen(name = "Before")
    actionExecutor.run {
        runAction(
            Action.RestartAdbService,
            this,
            beforeScreen,
            executeCondition = config.restartAdbServiceBeforeEachDevice,
        )
        prepareEnvironment()
        config.globalLogcatBufferSize?.let { logcatBufferSize ->
            runAction(
                Action.SetLogcatBufferSize(logcatBufferSize), this, beforeScreen
            )
        }
        config.shellsBeforeAllScreens.forEach { shellBeforeAllScreens ->
            runAction(
                Action.ShellBeforeAllScreens(shellBeforeAllScreens),
                this,
                beforeScreen,
                executeCondition = shellBeforeAllScreens.isNotBlank(),
            )
        }
        config.globalScreenDensity?.let { screenDensity ->
            runAction(
                Action.SetScreenDensity(screenDensity), this, beforeScreen
            )
        }
        config.globalScreenSize?.let { screenSize ->
            runAction(
                Action.SetScreenSize(screenSize), this, beforeScreen
            )
        }

        if (config.deleteAndInstallApk) {
            runAction(Action.ClearAppData, this, beforeScreen)
            runAction(Action.DeleteApk, this, beforeScreen)
            runAction(Action.InstallApk(config.apkUrl), this, beforeScreen)
        }

        runAction(Action.HideDemoMode, this, beforeScreen)

        if (config.useDemoMode) {
            runAction(Action.SetDemoModeEnable, this, beforeScreen)
            runAction(Action.ShowDemoMode, this, beforeScreen)
        }

        runAction(Action.HideGpuOverdraw, this, beforeScreen)
    }
    return beforeScreen
}

private suspend fun DescribeSpecContainerContext.runAfterScreens(
    actionExecutor: ActionExecutor,
    config: Config,
): ReportScreen {
    val afterScreen = ReportScreen("After")
    actionExecutor.run {
        if (config.useDemoMode) {
            runAction(Action.HideDemoMode, this, afterScreen)
        }
        config.shellsAfterAllScreens.forEach { shellAfterAllScreens ->
            runAction(
                Action.ShellAfterAllScreens(shellAfterAllScreens),
                this,
                afterScreen,
                executeCondition = shellAfterAllScreens.isNotBlank(),
            )
        }
        runAction(
            Action.ResetScreenSize,
            this,
            afterScreen,
            executeCondition = config.globalScreenSize != null
        )
        runAction(
            Action.ResetScreenDensity,
            this,
            afterScreen,
            executeCondition = config.globalScreenDensity != null
        )
        finishEnvironment()
    }
    return afterScreen
}

private suspend fun DescribeSpecContainerContext.runScreens(
    actionExecutor: ActionExecutor,
    config: Config,
    screens: List<Screen>,
    reportDevices: MutableList<ReportDevice>,
) = describe(actionExecutor.executor().toString()) {

    val reportScreens: MutableList<ReportScreen> = mutableListOf()
    val beforeScreenReport = runBeforeScreens(actionExecutor, config)

    screens.forEach { screen ->
        val reportScreen = ReportScreen(name = screen.name)
        runScreen(config, actionExecutor, screen, reportScreen, "normal")
        if (config.checkOverdraw.check) {
            runAction(Action.ShowGpuOverdraw, actionExecutor, reportScreen, "overdraw")
            runScreen(config, actionExecutor, screen, reportScreen, "overdraw")
            runAction(Action.HideGpuOverdraw, actionExecutor, reportScreen, "overdraw")
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

@Suppress("LongMethod", "ComplexMethod")
private suspend fun DescribeSpecContainerContext.runScreen(
    config: Config,
    actionExecutor: ActionExecutor,
    screen: Screen,
    reportScreen: ReportScreen,
    imagePrefix: String,
) = describe("Screen: ${screen.name}; $imagePrefix") {

    screen.shellsBefore.forEach { shellBefore ->
        runAction(
            Action.ShellBeforeScreen(shellBefore),
            actionExecutor,
            reportScreen,
            imagePrefix,
            shellBefore.isNotBlank(),
        )
    }
    runAction(
        Action.ClearLogcat,
        actionExecutor,
        reportScreen,
        imagePrefix,
        executeCondition = screen.clearLogcatBefore
    )

    screen.screenDensity?.let { screenDensity ->
        runAction(
            Action.SetScreenDensity(screenDensity),
            actionExecutor,
            reportScreen,
            imagePrefix,
        )
    }

    screen.screenSize?.let { screenSize ->
        runAction(
            Action.SetScreenSize(screenSize),
            actionExecutor,
            reportScreen,
            imagePrefix,
        )
    }

    if (config.clearDataBeforeEachRun || screen.clearDataBeforeRun) {
        runAction(Action.ClearAppData, actionExecutor, reportScreen, imagePrefix)
    }

    (config.permissions.granted + screen.permissions.granted).forEach {
        runAction(Action.GrantPermission(it), actionExecutor, reportScreen, imagePrefix)
    }
    screen.permissions.revoked.forEach {
        runAction(Action.RevokePermission(it), actionExecutor, reportScreen, imagePrefix)
    }

    val fontScale = if (screen.fontScale != FontScale.DEFAULT) {
        screen.fontScale
    } else {
        config.fontScale
    }
    runAction(Action.SetFontScale(fontScale), actionExecutor, reportScreen, imagePrefix)
    runAction(
        Action.CloseAppsInTray,
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.closeAppsInTrayBeforeStart,
    )
    runAction(
        Action.ResetGfxInfo,
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.resetGfxInfoBeforeStart,
    )

    val activityParamsAsString = screen.activityParams.toStringParams()
    runAction(
        Action.OpenScreen(screen, activityParamsAsString),
        actionExecutor,
        reportScreen,
        imagePrefix,
    )

    screen.actions.forEach { action ->
        runAction(action, actionExecutor, reportScreen, imagePrefix)
    }

    if (screen.screenshotAsLastAction && screen.actions.count { it is Action.TakeScreenshot } == 0) {
        runAction(Action.TakeScreenshot(""), actionExecutor, reportScreen, imagePrefix)
    }

    runAction(Action.SetFontScale(FontScale.DEFAULT), actionExecutor, reportScreen, imagePrefix)

    runAction(Action.CloseApp, actionExecutor, reportScreen, imagePrefix)

    runAction(
        Action.ResetScreenSize,
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.screenSize != null
    )

    runAction(
        Action.ResetScreenDensity,
        actionExecutor,
        reportScreen,
        imagePrefix,
        screen.screenDensity != null
    )

    config.globalScreenDensity?.let { screenDensity ->
        runAction(
            Action.SetScreenDensity(screenDensity),
            actionExecutor,
            reportScreen,
            imagePrefix,
            screen.screenDensity != null,
        )
    }

    config.globalScreenSize?.let { screenSize ->
        runAction(
            Action.SetScreenSize(screenSize),
            actionExecutor,
            reportScreen,
            imagePrefix,
            screen.screenSize != null,
        )
    }

    screen.shellsAfter.forEach { shellAfter ->
        runAction(
            Action.ShellAfterScreen(shellAfter),
            actionExecutor,
            reportScreen,
            imagePrefix,
            shellAfter.isNotBlank(),
        )
    }
}
