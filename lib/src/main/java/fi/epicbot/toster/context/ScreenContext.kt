package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.Screen

@TosterDslMarker
@Suppress("TooManyFunctions")
class ScreenContext {

    internal val screen = Screen()

    fun name(value: String) {
        screen.name = value
    }

    fun url(value: String) {
        screen.url = value
    }

    fun delayAfterOpenMillis(value: Long) {
        screen.delayAfterOpenMillis = value
    }

    fun activityParams(init: ActivityParamContext.() -> Unit) {
        val context = ActivityParamContext().apply(init)
        screen.activityParams.addAll(context.activityParams)
    }

    fun permissions(init: PermissionActivityContext.() -> Unit) {
        val context = PermissionActivityContext().apply(init)
        screen.permissions = Permissions(granted = context.granted, revoked = context.revoked)
    }

    fun actions(init: ActionContext.() -> Unit) {
        val context = ActionContext().apply(init)
        screen.actions.addAll(context.actions)
    }

    fun runShellBefore(shell: String) {
        screen.shellBefore = shell
    }

    fun runShellAfter(shell: String) {
        screen.shellAfter = shell
    }

    fun clearDataBeforeRun() {
        screen.clearDataBeforeRun = true
    }

    fun fontScale(scale: FontScale) {
        screen.fontScale = scale
    }

    fun disableScreenshotAsLastAction() {
        screen.screenshotAsLastAction = false
    }
}
