package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.ScreenSize

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

    fun shortUrl(value: String) {
        screen.shortUrl = value
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

    fun runShellsBefore(vararg shell: String) {
        screen.shellsBefore = shell
    }

    fun runShellsAfter(vararg shell: String) {
        screen.shellsAfter = shell
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

    fun resetGfxInfoBeforeStart() {
        screen.resetGfxInfoBeforeStart = true
    }

    fun closeAppsInTrayBeforeStart() {
        screen.closeAppsInTrayBeforeStart = true
    }

    fun setScreenDensity(density: Density) {
        screen.screenDensity = density
    }

    fun setScreenSize(width: Int, height: Int) {
        screen.screenSize = ScreenSize(width = width, height = height)
    }
}
