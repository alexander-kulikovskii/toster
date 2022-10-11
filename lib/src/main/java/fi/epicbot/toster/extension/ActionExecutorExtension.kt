package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Apk
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.runAction
import fi.epicbot.toster.model.toStringParams
import fi.epicbot.toster.report.model.ReportCollage
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportScreen
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

internal suspend fun DescribeSpecContainerScope.runBeforeScreens(
    actionExecutor: ActionExecutor,
    config: Config,
    apkUrl: String,
): ReportScreen {
    val beforeScreen = ReportScreen(name = "Before")

    runAction(
        Action.RestartAdbService,
        actionExecutor,
        beforeScreen,
        executeCondition = config.restartAdbServiceBeforeEachDevice,
    )
    actionExecutor.prepareEnvironment()
    config.blockBeforeAllScreens.invoke()
    config.globalLogcatBufferSize?.let { logcatBufferSize ->
        runAction(Action.SetLogcatBufferSize(logcatBufferSize), actionExecutor, beforeScreen)
    }
    config.shellsBeforeAllScreens.forEach { shellBeforeAllScreens ->
        runAction(
            Action.ShellBeforeAllScreens(shellBeforeAllScreens),
            actionExecutor,
            beforeScreen,
            executeCondition = shellBeforeAllScreens.isNotBlank(),
        )
    }
    apply(config.globalScreenDensity, actionExecutor, beforeScreen)

    config.globalScreenSize?.let { screenSize ->
        runAction(Action.SetScreenSize(screenSize), actionExecutor, beforeScreen)
    }

    if (config.deleteAndInstallApk) {
        listOf(
            Action.ClearAppData,
            Action.DeleteApk,
            Action.InstallApk(apkUrl)
        ).forEach { action ->
            runAction(action, actionExecutor, beforeScreen)
        }
    }

    runAction(Action.HideDemoMode, actionExecutor, beforeScreen)

    if (config.useDemoMode) {
        runAction(Action.SetDemoModeEnable, actionExecutor, beforeScreen)
        runAction(Action.ShowDemoMode(config.demoModeTime), actionExecutor, beforeScreen)
    }

    runAction(Action.HideGpuOverdraw, actionExecutor, beforeScreen)

    return beforeScreen
}

internal suspend fun DescribeSpecContainerScope.runAfterScreens(
    actionExecutor: ActionExecutor,
    config: Config,
): ReportScreen {
    val afterScreen = ReportScreen("After")

    if (config.useDemoMode) {
        runAction(Action.HideDemoMode, actionExecutor, afterScreen)
    }
    config.shellsAfterAllScreens.forEach { shellAfterAllScreens ->
        runAction(
            Action.ShellAfterAllScreens(shellAfterAllScreens),
            actionExecutor,
            afterScreen,
            executeCondition = shellAfterAllScreens.isNotBlank(),
        )
    }
    resetScreenSizeAndDensity(actionExecutor, config, afterScreen)
    config.blockAfterAllScreens.invoke()
    actionExecutor.finishEnvironment()

    return afterScreen
}

internal suspend fun DescribeSpecContainerScope.runScreens(
    actionExecutor: ActionExecutor,
    config: Config,
    apk: Apk,
    screens: List<Screen>,
    reportDevices: MutableList<ReportDevice>,
) {
    describe(actionExecutor.executor().toString()) {

        val reportScreens: MutableList<ReportScreen> = mutableListOf()
        val beforeScreenReport = runBeforeScreens(actionExecutor, config, apk.url)

        screens.forEach { screen ->
            val reportScreen = ReportScreen(name = screen.name)
            actionExecutor.imagePrefix = "normal"
            runScreen(actionExecutor, config, screen, reportScreen)
            if (config.checkOverdraw.check) {
                actionExecutor.imagePrefix = "overdraw"
                runAction(Action.ShowGpuOverdraw, actionExecutor, reportScreen)
                runScreen(actionExecutor, config, screen, reportScreen)
                runAction(Action.HideGpuOverdraw, actionExecutor, reportScreen)
                // TODO run tests for getting overdraw info
            }
            // TODO compare screenshots
            reportScreens.add(reportScreen)
        }
        val afterScreenReport = runAfterScreens(actionExecutor, config)

        reportDevices.add(
            ReportDevice(
                device = actionExecutor.executor(),
                reportScreens = listOf(beforeScreenReport) + reportScreens + listOf(
                    afterScreenReport
                ),
                collage = ReportCollage(),
            )
        )
    }
}

@Suppress("ComplexMethod", "LongMethod")
internal suspend fun DescribeSpecContainerScope.runScreen(
    actionExecutor: ActionExecutor,
    config: Config,
    screen: Screen,
    reportScreen: ReportScreen,
) {
    describe("Screen: ${screen.name}; ${actionExecutor.imagePrefix}") {

        screen.blockBefore.invoke()
        screen.shellsBefore.forEach { shellBefore ->
            runAction(
                Action.ShellBeforeScreen(shellBefore),
                actionExecutor,
                reportScreen,
                shellBefore.isNotBlank(),
            )
        }
        runAction(
            Action.ClearLogcat,
            actionExecutor,
            reportScreen,
            executeCondition = screen.clearLogcatBefore
        )

        setScreenSizeAndDensity(actionExecutor, screen, reportScreen)

        if (config.clearDataBeforeEachRun || screen.clearDataBeforeRun) {
            runAction(Action.ClearAppData, actionExecutor, reportScreen)
        }

        (config.permissions.granted + screen.permissions.granted).forEach {
            runAction(Action.GrantPermission(it), actionExecutor, reportScreen)
        }
        screen.permissions.revoked.forEach {
            runAction(Action.RevokePermission(it), actionExecutor, reportScreen)
        }

        val fontScale = if (screen.fontScale != null && screen.fontScale != FontScale.DEFAULT) {
            screen.fontScale
        } else {
            config.fontScale
        }
        fontScale?.let { fontScale ->
            runAction(Action.SetFontScale(fontScale), actionExecutor, reportScreen)
        }

        runAction(
            Action.CloseAppsInTray,
            actionExecutor,
            reportScreen,
            screen.closeAppsInTrayBeforeStart,
        )
        runAction(
            Action.ResetGfxInfo,
            actionExecutor,
            reportScreen,
            screen.resetGfxInfoBeforeStart,
        )

        val activityParamsAsString = screen.activityParams.toStringParams()
        runAction(
            Action.OpenScreen(screen, activityParamsAsString),
            actionExecutor,
            reportScreen,
        )
        if (screen.actions.filterIsInstance<Action.Rotate>().isNotEmpty()) {
            runAction(Action.TurnOffAutoRotation, actionExecutor, reportScreen)
        }

        screen.actions.forEach { action ->
            runAction(action, actionExecutor, reportScreen)
        }

        if (screen.screenshotAsLastAction && screen.actions.count { it is Action.TakeScreenshot } == 0) {
            runAction(Action.TakeScreenshot(""), actionExecutor, reportScreen)
        }

        fontScale?.let {
            runAction(Action.SetFontScale(FontScale.DEFAULT), actionExecutor, reportScreen)
        }

        runAction(Action.CloseApp, actionExecutor, reportScreen)

        resetScreenSizeAndDensity(actionExecutor, config, screen, reportScreen)

        screen.shellsAfter.forEach { shellAfter ->
            runAction(
                Action.ShellAfterScreen(shellAfter),
                actionExecutor,
                reportScreen,
                shellAfter.isNotBlank(),
            )
        }
        screen.blockAfter.invoke()
    }
}

internal suspend fun DescribeSpecContainerScope.resetScreenSizeAndDensity(
    actionExecutor: ActionExecutor,
    config: Config,
    afterScreen: ReportScreen,
) {
    reset(config.globalScreenSize, actionExecutor, afterScreen)
    reset(config.globalScreenDensity, actionExecutor, afterScreen)
}

internal suspend fun DescribeSpecContainerScope.setScreenSizeAndDensity(
    actionExecutor: ActionExecutor,
    screen: Screen,
    reportScreen: ReportScreen,
) {
    apply(
        screen.screenDensity,
        actionExecutor,
        reportScreen,
    )
    apply(
        screen.screenSize,
        actionExecutor,
        reportScreen,
    )
}

internal suspend fun DescribeSpecContainerScope.resetScreenSizeAndDensity(
    actionExecutor: ActionExecutor,
    config: Config,
    screen: Screen,
    reportScreen: ReportScreen,
) {
    reset(screen.screenSize, actionExecutor, reportScreen)
    reset(screen.screenDensity, actionExecutor, reportScreen)

    apply(
        config.globalScreenDensity,
        actionExecutor,
        reportScreen,
        screen.screenDensity != null,
    )
    apply(
        config.globalScreenSize,
        actionExecutor,
        reportScreen,
        screen.screenSize != null,
    )
}
