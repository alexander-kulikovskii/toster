package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.Devices
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Overdraw
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.SwipeOffset

@Suppress("TooManyFunctions")
@TosterDslMarker
class ConfigContext {
    internal val config = Config()

    fun applicationName(value: String) {
        config.applicationName = value
    }

    fun applicationPackageName(value: String) {
        config.applicationPackageName = value
    }

    fun permissions(init: PermissionAppContext.() -> Unit) {
        val context = PermissionAppContext().apply(init)
        val permissions = context.granted
        config.permissions = Permissions(granted = permissions)
    }

    fun emulatorPath(value: String) {
        config.emulatorPath = value
    }

    fun apkUrl(value: String) {
        config.apkUrl = value
    }

    fun checkOverdraw(value: Overdraw) {
        config.checkOverdraw = value
    }

    fun report(init: ReportContext.() -> Unit) {
        val context = ReportContext().apply(init)
        val report = context.report
        config.reportConfig = report
    }

    fun fontScaleForAll(scale: FontScale) {
        config.fontScale = scale
    }

    fun runShellBeforeAllScreens(shell: String) {
        config.shellBeforeAllScreens = shell
    }

    fun runShellAfterAllScreens(shell: String) {
        config.shellAfterAllScreens = shell
    }

    fun clearDataBeforeEachRun() {
        config.clearDataBeforeEachRun = true
    }

    fun devices(init: DeviceContext.() -> Unit) {
        val context = DeviceContext().apply(init)
        config.devices = Devices(context.emulators, context.phones)
    }

    fun collage(init: CollageContext.() -> Unit) {
        val context = CollageContext().apply(init)
        val collage = context.collage
        config.collage = collage
    }

    fun testTimeout(timeoutMillis: Long) {
        config.testTimeoutMillis = timeoutMillis
    }

    fun doNotDeleteAndInstallApk() {
        config.deleteAndInstallApk = false
    }

    fun setSwipeOffset(offsetPx: Int, offsetFactor: Double) {
        config.swipeOffset = SwipeOffset(offsetPx, offsetFactor)
    }
}
