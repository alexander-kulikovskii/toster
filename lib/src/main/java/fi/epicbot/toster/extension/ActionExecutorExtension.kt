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

context(DescribeSpecContainerScope)
internal suspend fun ActionExecutor.runBeforeScreens(
    config: Config,
    apk: Apk,
): ReportScreen {
    val beforeScreen = ReportScreen(name = "Before")

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
    config.globalScreenDensity.apply(this, beforeScreen)

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

    return beforeScreen
}

context(DescribeSpecContainerScope)
internal suspend fun ActionExecutor.runAfterScreens(
    config: Config,
): ReportScreen {
    val afterScreen = ReportScreen("After")

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
    resetScreenSizeAndDensity(config, afterScreen)
    finishEnvironment()

    return afterScreen
}

context(DescribeSpecContainerScope)
internal suspend fun ActionExecutor.runScreens(
    config: Config,
    apk: Apk,
    screens: List<Screen>,
    reportDevices: MutableList<ReportDevice>,
) {
    val actionExecutor = this
    describe(this.executor().toString()) {

        val reportScreens: MutableList<ReportScreen> = mutableListOf()
        val beforeScreenReport = runBeforeScreens(config, apk)

        screens.forEach { screen ->
            val reportScreen = ReportScreen(name = screen.name)
            runScreen(config, screen, reportScreen, "normal")
            if (config.checkOverdraw.check) {
                Action.ShowGpuOverdraw.runAction(actionExecutor, reportScreen, "overdraw")
                runScreen(config, screen, reportScreen, "overdraw")
                Action.HideGpuOverdraw.runAction(actionExecutor, reportScreen, "overdraw")
                // TODO run tests for getting overdraw info
            }
            // TODO compare screenshots
            reportScreens.add(reportScreen)
        }
        val afterScreenReport = runAfterScreens(config)

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

context(DescribeSpecContainerScope)
@Suppress("LongMethod")
internal suspend fun ActionExecutor.runScreen(
    config: Config,
    screen: Screen,
    reportScreen: ReportScreen,
    imagePrefix: String,
) {
    val actionExecutor = this
    describe("Screen: ${screen.name}; $imagePrefix") {

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

        setScreenSizeAndDensity(screen, reportScreen, imagePrefix)

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

        resetScreenSizeAndDensity(config, screen, reportScreen, imagePrefix)

        screen.shellsAfter.forEach { shellAfter ->
            Action.ShellAfterScreen(shellAfter).runAction(
                actionExecutor,
                reportScreen,
                imagePrefix,
                shellAfter.isNotBlank(),
            )
        }
    }
}

context(DescribeSpecContainerScope)
internal suspend fun ActionExecutor.resetScreenSizeAndDensity(
    config: Config,
    afterScreen: ReportScreen,
) {
    config.globalScreenSize.reset(this, afterScreen)
    config.globalScreenDensity.reset(this, afterScreen)
}

context(DescribeSpecContainerScope)
internal suspend fun ActionExecutor.setScreenSizeAndDensity(
    screen: Screen,
    reportScreen: ReportScreen,
    imagePrefix: String,
) {
    screen.screenDensity.apply(
        this,
        reportScreen,
        imagePrefix,
    )
    screen.screenSize.apply(
        this,
        reportScreen,
        imagePrefix,
    )
}

context(DescribeSpecContainerScope)
internal suspend fun ActionExecutor.resetScreenSizeAndDensity(
    config: Config,
    screen: Screen,
    reportScreen: ReportScreen,
    imagePrefix: String,
) {
    screen.screenSize.reset(this, reportScreen, imagePrefix)
    screen.screenDensity.reset(this, reportScreen, imagePrefix)

    config.globalScreenDensity.apply(
        this,
        reportScreen,
        imagePrefix,
        screen.screenDensity != null,
    )
    config.globalScreenSize.apply(
        this,
        reportScreen,
        imagePrefix,
        screen.screenSize != null,
    )
}
