package fi.epicbot.toster.executor.android

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.extension.saveForPath
import fi.epicbot.toster.memory.DumpSysParser
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.SwipeMove
import fi.epicbot.toster.model.title
import fi.epicbot.toster.model.toMove
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.Memory
import fi.epicbot.toster.report.model.ReportAction
import fi.epicbot.toster.report.model.Screenshot
import kotlinx.coroutines.delay
import kotlin.math.max

@Suppress("TooManyFunctions")
internal open class AndroidExecutor(
    private val serialName: String,
    private val config: Config,
    private val shellExecutor: ShellExecutor,
    private val dumpSysParser: DumpSysParser,
) : ActionExecutor {

    private val apkPackage = config.applicationPackageName
    private var actionIndex = 0L

    override fun executorName(): String = "Phone <$serialName>"

    override suspend fun prepareEnvironment() {
        // Do nothing
    }

    override suspend fun finishEnvironment() {
        // Do nothing
    }

    @Suppress("ComplexMethod", "ReturnCount")
    override suspend fun execute(action: Action, imagePrefix: String): ReportAction {
        if (action is Action.TakeMemoryAllocation) {
            return takeMemoryAllocation(action)
        }
        if (action is Action.TakeScreenshot) {
            return takeScreenshot(action, imagePrefix)
        }

        val startTime = System.currentTimeMillis()
        when (action) {
            Action.ClearAppData -> "pm clear -t $apkPackage".adbShell()
            is Action.Click -> "input tap ${action.x} ${action.y}".adbShell()
            Action.CloseApp -> "am force-stop $apkPackage".adbShell()
            Action.SetDemoModeEnable -> "settings put global sysui_demo_allowed 1".adbShell()
            Action.ShowDemoMode -> showDemoMode()
            Action.HideDemoMode -> "am broadcast -a com.android.systemui.demo -e command exit".adbShell()
            is Action.OpenScreen -> {
                shellExecutor.setScreenDirAndMakeIt(action.screen.name)
                val screenUrl = if (action.screen.url.isNotBlank()) action.screen.url else
                    "${config.applicationPackageName}.${action.screen.shortUrl}"
                "am start -n $apkPackage/$screenUrl${action.params}".adbShell()
                delay(action.screen.delayAfterOpenMillis)
            }
            Action.ShowGpuOverdraw -> {
                "setprop debug.hwui.overdraw show".adbShell()
                "setprop debug.hwui.show_overdraw true".adbShell()
                "service call activity 1599295570".adbShell()
            }
            Action.HideGpuOverdraw -> {
                "setprop debug.hwui.overdraw false".adbShell()
                "setprop debug.hwui.show_overdraw false".adbShell()
                "service call activity 1599295570".adbShell()
            }
            is Action.SetFontScale -> "settings put system font_scale ${action.fontScale.size}".adbShell()
            Action.DeleteApk -> "pm uninstall -k $apkPackage".adbShell()
            is Action.InstallApk -> "adb install -g ${action.apkPath}".shell()
            is Action.CreateDir -> action.dir.makeDir()
            is Action.GrantPermission -> "pm grant $apkPackage ${action.permission}".adbShell()
            is Action.RevokePermission -> "pm revoke $apkPackage ${action.permission}".adbShell()
            Action.CloseKeyboard -> "input keyevent 111".adbShell()
            Action.OpenHomeScreen -> "input keyevent 3".adbShell()
            Action.ClickBack -> "input keyevent 4".adbShell()
            is Action.SendKeyEvent -> "input keyevent ${action.keyEvent}".adbShell()
            Action.ClearText -> TODO()
            Action.GetDensity -> TODO()
            is Action.TypeText -> TODO()
            is Action.Delay -> delay(max(0L, action.delayMillis))
            is Action.Shell -> action.shell.shell()
            is Action.ShellAfterAllScreens -> action.shell.shell()
            is Action.ShellAfterScreen -> action.shell.shell()
            is Action.ShellBeforeAllScreens -> action.shell.shell()
            is Action.ShellBeforeScreen -> action.shell.shell()
            is Action.Swipe -> swipe(action.swipeMove)
            else -> throw UnsupportedOperationException("Unsupported type of action $action")
        }
        val endTime = System.currentTimeMillis()

        return Common(actionIndex++, action.title(), startTime, endTime)
    }

    private fun swipe(swipeMove: SwipeMove) {
        val move = if (swipeMove is SwipeMove.Custom) {
            swipeMove.move
        } else {
            // TODO make measurement only once
            val rawData = "wm size".adbShell()
            val rawSize = rawData.replace("Physical size: ", "").split("x")
            swipeMove.toMove(width = rawSize[0].toInt(), height = rawSize[1].toInt())
        }
        "input touchscreen swipe ${move.xFrom} ${move.yFrom} ${move.xTo} ${move.yTo}".adbShell()
    }

    private fun takeMemoryAllocation(action: Action.TakeMemoryAllocation): Memory {
        val startTime = System.currentTimeMillis()

        val rawMemInfo = "dumpsys meminfo $apkPackage -d".adbShell()
        val measurements = dumpSysParser.parse(rawMemInfo)

        val endTime = System.currentTimeMillis()
        return Memory(
            index = actionIndex++,
            name = action.title(),
            measurements = measurements,
            startTime = startTime,
            endTime = endTime,
        )
    }

    private fun takeScreenshot(action: Action.TakeScreenshot, imagePrefix: String): Screenshot {
        val startTime = System.currentTimeMillis()

        shellExecutor.makeDirForScreen("$imagePrefix/")
        "/system/bin/screencap -p $DEVICE_SCREENSHOT_PATH".adbShell()
        val index = actionIndex++
        val screenshotFileName =
            if (action.name.saveForPath().isNotBlank()) action.name.saveForPath() else index
        "pull $DEVICE_SCREENSHOT_PATH $imagePrefix/$screenshotFileName.png".adb()

        val endTime = System.currentTimeMillis()
        return Screenshot(
            index = index,
            name = action.title(),
            startTime = startTime,
            endTime = endTime,
            prefix = imagePrefix,
            pathUrl = "$imagePrefix/$screenshotFileName.png"
        )
    }

    private fun showDemoMode() {
        // Set the clock to 12:00
        "am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200".adbShell()
        // Set the wifi level to max
        "am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4".adbShell()
        // Show the silent volume icon
        "am broadcast -a com.android.systemui.demo -e command status -e volume vibrate".adbShell()
        // Full battery
        "am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false".adbShell()
        // Hide the notification icons
        "am broadcast -a com.android.systemui.demo -e command notifications -e visible false".adbShell()
    }

    private fun String.makeDir(): String = shellExecutor.makeDir(this)

    private fun String.shell(): String = shellExecutor.runShellCommand(this, fromRootFolder = true)

    private fun String.adb(): String = shellExecutor.runCommandForScreen("adb", this)

    private fun String.adbShell(): String = "shell $this".adb()

    private companion object {
        private const val DEVICE_SCREENSHOT_PATH = "/sdcard/toster_screenshot_image.png"
    }
}
