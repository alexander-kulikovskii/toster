package fi.epicbot.toster.model

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.CpuUsage
import fi.epicbot.toster.report.model.GfxInfo
import fi.epicbot.toster.report.model.Memory
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.report.model.Screenshot
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

sealed class Action {
    object ClearAppData : Action()
    object ClearLogcat : Action()
    object ClearText : Action()
    class Click(val x: Int, val y: Int) : Action()
    object ClickBack : Action()
    object CloseApp : Action()
    object CloseAppsInTray : Action()
    object CloseKeyboard : Action()
    class CreateDir(val dir: String) : Action()
    class Delay(val delayMillis: Long) : Action()
    object DeleteApk : Action()
    object GetDensity : Action()
    class GrantPermission(val permission: String) : Action()
    object HideDemoMode : Action()
    object HideGpuOverdraw : Action()
    class InstallApk(val apkPath: String) : Action()
    class LongClick(val x: Int, val y: Int, val clickDelayMillis: Long) : Action()
    object OpenAppAgain : Action()
    object OpenHomeScreen : Action()
    class OpenScreen(val screen: Screen, val params: String) : Action()
    object ResetScreenDensity : Action()
    object ResetScreenSize : Action()
    object ResetGfxInfo : Action()
    object RestartAdbService : Action()
    class RevokePermission(val permission: String) : Action()
    class SendKeyEvent(val keyEvent: String) : Action()
    object SetDemoModeEnable : Action()
    class SetFontScale(val fontScale: FontScale) : Action()
    class SetLogcatBufferSize(val bufferSize: BufferSize) : Action()
    class SetScreenDensity(val density: Density) : Action()
    class SetScreenSize(val screenSize: ScreenSize) : Action()
    class Shell(val shell: String, val name: String) : Action()
    class ShellAfterAllScreens(val shell: String) : Action()
    class ShellAfterScreen(val shell: String) : Action()
    class ShellBeforeAllScreens(val shell: String) : Action()
    class ShellBeforeScreen(val shell: String) : Action()
    class ShowDemoMode(val time: String) : Action()
    object ShowGpuOverdraw : Action()
    class Swipe(val swipeMove: SwipeMove) : Action()
    object TakeCpuUsage : Action()
    object TakeGfxInfo : Action()
    object TakeMemoryAllocation : Action()
    class TakeMemoryHeap(val index: Int) : Action()
    class TakeLogcat(val buffer: Buffer) : Action()
    class TakeScreenshot(val name: String) : Action()
    class TrimMemory(val trimMemoryLevel: TrimMemoryLevel) : Action()
    class TypeText(val text: String) : Action()
    @Suppress("LongParameterList")
    class Zoom(
        val centerX: Int,
        val centerY: Int,
        val fromX: Int,
        val fromY: Int,
        val toX: Int,
        val toY: Int,
        val zoomDelayMillis: Long,
        val delayMillis: Long,
    ) : Action()
    class Rotate(val rotation: Rotation) : Action()
    object TurnOffAutoRotation : Action()
}

@Suppress("ComplexMethod")
internal fun Action.title(): String {
    return when (this) {
        Action.ClearAppData -> "Clear app data"
        Action.ClearLogcat -> "Clear logcat"
        Action.ClearText -> TODO()
        is Action.Click -> "Click to ($x;$y)"
        Action.ClickBack -> "Click back"
        Action.CloseApp -> "Close app"
        Action.CloseAppsInTray -> "Close apps in tray"
        Action.CloseKeyboard -> "Close keyboard"
        is Action.CreateDir -> "Create dir"
        is Action.Delay -> "Delay for <$delayMillis> ms"
        Action.DeleteApk -> "Delete apk"
        Action.GetDensity -> TODO()
        is Action.GrantPermission -> "Grant permission <$permission>"
        Action.HideDemoMode -> "Hide demo mode"
        Action.HideGpuOverdraw -> "Hide gpu overdraw"
        is Action.InstallApk -> "Install apk"
        is Action.LongClick -> "Long click to ($x;$y) for $clickDelayMillis ms"
        Action.OpenAppAgain -> "Open app again"
        Action.OpenHomeScreen -> "Open home screen"
        is Action.OpenScreen -> "Start activity" + if (params.isBlank()) "" else " with $params"
        is Action.ResetGfxInfo -> "Reset gfxinfo"
        is Action.ResetScreenDensity -> "Reset screen density"
        is Action.ResetScreenSize -> "Reset screen size"
        Action.RestartAdbService -> "Restart adb service"
        is Action.RevokePermission -> "Revoke permission <$permission>"
        is Action.SendKeyEvent -> "Send key event <$keyEvent>"
        Action.SetDemoModeEnable -> "Set demo mode enable"
        is Action.SetFontScale -> "Set font scale <$fontScale>"
        is Action.SetLogcatBufferSize -> "Set logcat buffer size $bufferSize"
        is Action.SetScreenDensity -> "Set screen density <${density.dpi}>"
        is Action.SetScreenSize -> "Set screen size <${screenSize.width}x${screenSize.height}>"
        is Action.Shell -> if (name.isBlank()) "Run shell" else "Run $name"
        is Action.ShellAfterAllScreens -> "Run shell after all screens"
        is Action.ShellAfterScreen -> "Run shell after screen"
        is Action.ShellBeforeAllScreens -> "Run shell before all screens"
        is Action.ShellBeforeScreen -> "Run shell before screen"
        is Action.ShowDemoMode -> "Show demo mode with time $time"
        Action.ShowGpuOverdraw -> "Show gpu overdraw"
        is Action.Swipe -> swipeMove.toString()
        is Action.TakeCpuUsage -> "Take cpu usage"
        is Action.TakeGfxInfo -> "Take gfxinfo"
        Action.TakeMemoryAllocation -> "Take memory allocation"
        is Action.TakeMemoryHeap -> "Take memory heap <$index>"
        is Action.TakeLogcat -> "Take logcat ${buffer.bufferName}"
        is Action.TakeScreenshot -> "Take screenshot"
        is Action.TrimMemory -> "Send trim memory <${trimMemoryLevel.level}>"
        is Action.TypeText -> "Type text <$text>"
        is Action.Zoom -> "Zoom from ($centerX;$centerY) to ($toX;$toY)"
        is Action.Rotate -> "Rotate to ${rotation.value}"
        is Action.TurnOffAutoRotation -> "Turn off auto-rotation"
    }
}

internal suspend fun DescribeSpecContainerScope.runAction(
    action: Action,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    executeCondition: Boolean = true,
) {
    if (executeCondition) {
        it(action.title()) {
            when (val reportAction = actionExecutor.execute(action)) {
                is Common -> reportScreen.common.add(reportAction)
                is Memory -> reportScreen.memory.add(reportAction)
                is GfxInfo -> reportScreen.gfxInfo.add(reportAction)
                is CpuUsage -> reportScreen.cpuUsage.add(reportAction)
                is Screenshot -> reportScreen.screenshots.add(
                    reportAction.copy(pathUrl = "${reportScreen.name.safeForPath()}/${reportAction.pathUrl}")
                )
            }
        }
    }
}
