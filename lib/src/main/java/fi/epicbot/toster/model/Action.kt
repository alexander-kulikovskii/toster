package fi.epicbot.toster.model

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.extension.saveForPath
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.Memory
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.report.model.Screenshot
import io.kotest.core.spec.style.scopes.DescribeSpecContainerContext

sealed class Action {
    object ClearAppData : Action()
    object ClearText : Action()
    data class Click(val x: Int, val y: Int) : Action()
    object ClickBack : Action()
    object CloseApp : Action()
    object CloseKeyboard : Action()
    data class CreateDir(val dir: String) : Action()
    data class Delay(val delayMillis: Long) : Action()
    object DeleteApk : Action()
    object GetDensity : Action()
    data class GrantPermission(val permission: String) : Action()
    object HideDemoMode : Action()
    object HideGpuOverdraw : Action()
    data class InstallApk(val apkPath: String) : Action()
    object OpenHomeScreen : Action()
    data class OpenScreen(val screen: Screen, val params: String) : Action()
    data class RevokePermission(val permission: String) : Action()
    data class SendKeyEvent(val keyEvent: String) : Action()
    object SetDemoModeEnable : Action()
    data class SetFontScale(val fontScale: FontScale) : Action()
    data class Shell(val shell: String, val name: String) : Action()
    data class ShellAfterAllScreens(val shell: String) : Action()
    data class ShellAfterScreen(val shell: String) : Action()
    data class ShellBeforeAllScreens(val shell: String) : Action()
    data class ShellBeforeScreen(val shell: String) : Action()
    object ShowDemoMode : Action()
    object ShowGpuOverdraw : Action()
    data class Swipe(val swipeMove: SwipeMove) : Action()
    object TakeMemoryAllocation : Action()
    data class TakeMemoryHeap(val index: Int) : Action()
    data class TakeScreenshot(val name: String) : Action()
    data class TypeText(val text: String) : Action()
}

@Suppress("ComplexMethod")
internal fun Action.title(): String {
    return when (this) {
        Action.ClearAppData -> "Clear app data"
        Action.ClearText -> TODO()
        is Action.Click -> "Click to ($x;$y)"
        Action.ClickBack -> TODO()
        Action.CloseApp -> "Close app"
        Action.CloseKeyboard -> "Close keyboard"
        is Action.CreateDir -> "Create dir"
        is Action.Delay -> "Delay for <$delayMillis> ms"
        Action.DeleteApk -> "Delete apk"
        Action.GetDensity -> TODO()
        is Action.GrantPermission -> "Grant permission <$permission>"
        Action.HideDemoMode -> "Hide demo mode"
        Action.HideGpuOverdraw -> "Hide gpu overdraw"
        is Action.InstallApk -> "Install apk"
        Action.OpenHomeScreen -> "Open home screen"
        is Action.OpenScreen -> "Start activity" + if (params.isBlank()) "" else " with $params"
        is Action.RevokePermission -> "Revoke permission <$permission>"
        is Action.SendKeyEvent -> "Send key event <$keyEvent>"
        Action.SetDemoModeEnable -> "Set demo mode enable"
        is Action.SetFontScale -> "Set font scale <$fontScale>"
        is Action.Shell -> if (name.isBlank()) "Run shell" else "Run $name"
        is Action.ShellAfterAllScreens -> "Run shell after all screens"
        is Action.ShellAfterScreen -> "Run shell after screen"
        is Action.ShellBeforeAllScreens -> "Run shell before all screens"
        is Action.ShellBeforeScreen -> "Run shell before screen"
        Action.ShowDemoMode -> "Show demo mode"
        Action.ShowGpuOverdraw -> "Show gpu overdraw"
        is Action.Swipe -> swipeMove.toString()
        Action.TakeMemoryAllocation -> "Take memory allocation"
        is Action.TakeMemoryHeap -> "Take memory heap <$index>"
        is Action.TakeScreenshot -> "Take screenshot"
        is Action.TypeText -> "Type text <$text>"
    }
}

internal suspend fun DescribeSpecContainerContext.runAction(
    action: Action,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    imagePrefix: String = "",
    executeCondition: Boolean = true,
) {
    if (executeCondition) {
        it(action.title()) {
            when (val reportAction = actionExecutor.execute(action, imagePrefix)) {
                is Common -> reportScreen.common.add(reportAction)
                is Memory -> reportScreen.memory.add(reportAction)
                is Screenshot -> reportScreen.screenshots.add(
                    reportAction.copy(pathUrl = "${reportScreen.name.saveForPath()}/${reportAction.pathUrl}")
                )
            }
        }
    }
}
