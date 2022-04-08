package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.BufferDimension
import fi.epicbot.toster.model.BufferSize
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.Devices
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Overdraw
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.ScreenSize
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

    fun shellLogger(init: ShellLoggerContext.() -> Unit) {
        val context = ShellLoggerContext().apply(init)
        val shellLoggerConfig = context.shellLogger
        config.shellLoggerConfig = shellLoggerConfig
    }

    fun fontScaleForAll(scale: FontScale) {
        config.fontScale = scale
    }

    fun runShellsBeforeAllScreens(vararg shell: String) {
        config.shellsBeforeAllScreens = shell
    }

    fun runShellsAfterAllScreens(vararg shell: String) {
        config.shellsAfterAllScreens = shell
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

    fun setHorizontalSwipeOffset(offsetPx: Int, offsetFactor: Double) {
        config.horizontalSwipeOffset = SwipeOffset.HorizontalSwipeOffset(offsetPx, offsetFactor)
    }

    fun setVerticalSwipeOffset(offsetPx: Int, offsetFactor: Double) {
        config.verticalSwipeOffset = SwipeOffset.VerticalSwipeOffset(offsetPx, offsetFactor)
    }

    /**
     * If you want to disable demo mode for tests use this function.
     */
    fun disableDemoMode() {
        config.useDemoMode = false
    }

    fun disableFailFast() {
        config.failFast = false
    }

    fun restartAdbServiceBeforeEachDevice() {
        config.restartAdbServiceBeforeEachDevice = true
    }

    fun setScreenDensity(density: Density) {
        config.globalScreenDensity = density
    }

    fun setScreenSize(width: Int, height: Int) {
        config.globalScreenSize = ScreenSize(width = width, height = height)
    }

    fun setLogcatBufferSize(size: Int, bufferDimension: BufferDimension) {
        config.globalLogcatBufferSize = BufferSize(size, bufferDimension)
    }
}
