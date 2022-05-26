package fi.epicbot.toster.executor.android

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.SwipeMove
import fi.epicbot.toster.model.title
import fi.epicbot.toster.model.toMove
import fi.epicbot.toster.parser.ParserProvider
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.CpuUsage
import fi.epicbot.toster.report.model.Device
import fi.epicbot.toster.report.model.GfxInfo
import fi.epicbot.toster.report.model.Memory
import fi.epicbot.toster.report.model.ReportAction
import fi.epicbot.toster.report.model.Screenshot
import fi.epicbot.toster.time.TimeProvider
import kotlin.math.max

@Suppress("TooManyFunctions")
internal open class AndroidExecutor(
    private val serialName: String,
    private val config: Config,
    private val shellExecutor: ShellExecutor,
    private val parserProvider: ParserProvider,
    private val timeProvider: TimeProvider,
) : ActionExecutor {

    private val apkPackage = config.applicationPackageName
    private var actionIndex = 0L

    private var prefix: String = ""

    override var imagePrefix: String
        get() = prefix
        set(value) {
            prefix = value
        }

    override fun executor() = Device(type = "Phone", name = serialName)

    override suspend fun prepareEnvironment() {
        // Do nothing
    }

    override suspend fun finishEnvironment() {
        // Do nothing
    }

    @Suppress("ComplexMethod", "LongMethod", "ReturnCount")
    override suspend fun execute(action: Action): ReportAction {
        if (action is Action.TakeMemoryAllocation) {
            return takeMemoryAllocation(action)
        }
        if (action is Action.TakeCpuUsage) {
            return takeCpuUsage(action)
        }
        if (action is Action.TakeScreenshot) {
            return takeScreenshot(action, imagePrefix)
        }
        if (action is Action.TakeGfxInfo) {
            return takeGfxInfo(action)
        }
        if (action is Action.TakeLogcat) {
            return takeLogcat(action)
        }

        val startTime = timeProvider.getTimeMillis()
        when (action) {
            Action.ClearAppData -> {
                val exists = "adb shell pm list packages | grep $apkPackage".shell()
                if (exists == "package:$apkPackage") {
                    "pm clear -t $apkPackage".adbShell()
                }
            }
            is Action.Click -> "input tap ${action.x} ${action.y}".adbShell()
            Action.CloseApp -> "am force-stop $apkPackage".adbShell()
            Action.SetDemoModeEnable -> "settings put global sysui_demo_allowed 1".adbShell()
            is Action.ShowDemoMode -> showDemoMode(action)
            Action.HideDemoMode -> "$SYSTEM_UI_COMMAND exit".adbShell()
            is Action.OpenScreen -> {
                shellExecutor.setScreenDirAndMakeIt(serialName.safeForPath() + "/" + action.screen.name)
                val screenUrl = if (action.screen.url.isNotBlank()) action.screen.url else
                    "${config.applicationPackageName}.${action.screen.shortUrl}"
                "am start -n $apkPackage/$screenUrl${action.params}".adbShell()
                shellExecutor.delay(action.screen.delayAfterOpenMillis)
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
            Action.DeleteApk -> "pm uninstall $apkPackage".adbShell()
            is Action.InstallApk -> "adb install -g ${action.apkPath}".shell()
            is Action.LongClick ->
                "input touchscreen swipe ${action.x} ${action.y} ${action.x} ${action.y} ${action.clickDelayMillis}"
                    .adbShell()
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
            is Action.Delay -> shellExecutor.delay(max(0L, action.delayMillis))
            is Action.Shell -> action.shell.shell()
            is Action.ShellAfterAllScreens -> action.shell.shell()
            is Action.ShellAfterScreen -> action.shell.shell()
            is Action.ShellBeforeAllScreens -> action.shell.shell()
            is Action.ShellBeforeScreen -> action.shell.shell()
            is Action.Swipe -> swipe(action.swipeMove)
            Action.ResetGfxInfo -> "dumpsys gfxinfo $apkPackage --reset".adbShell()
            Action.OpenAppAgain -> "monkey -p $apkPackage -c android.intent.category.LAUNCHER 1".adbShell()
            Action.CloseAppsInTray -> closeAppsInTray()
            is Action.TrimMemory -> "am send-trim-memory $apkPackage ${action.trimMemoryLevel.level}".adbShell()
            Action.RestartAdbService -> {
                "kill-server".adb()
                "start-server".adb()
            }
            is Action.SetScreenSize -> "wm size ${action.screenSize.width}x${action.screenSize.height}".adbShell()
            Action.ResetScreenSize -> "wm size reset".adbShell()
            is Action.SetScreenDensity -> "wm density ${action.density.dpi}".adbShell()
            Action.ResetScreenDensity -> "wm density reset".adbShell()
            Action.ClearLogcat -> "logcat -c".adbShell()
            is Action.SetLogcatBufferSize -> "logcat -G ${action.bufferSize}".adbShell()
            else -> throw UnsupportedOperationException("Unsupported type of action $action")
        }
        val endTime = timeProvider.getTimeMillis()

        return Common(actionIndex++, action.title(), startTime, endTime)
    }

    private fun swipe(swipeMove: SwipeMove) {
        val move = if (swipeMove is SwipeMove.Custom) {
            swipeMove.move
        } else {
            // TODO make measurement only once
            val rawData = "wm size".adbShell()
            val rawSize = rawData.replace("Physical size: ", "").split("x")
            swipeMove.toMove(
                horizontalSwipeOffset = config.horizontalSwipeOffset,
                verticalSwipeOffset = config.verticalSwipeOffset,
                width = rawSize[0].toInt(),
                height = rawSize[1].toInt()
            )
        }
        "input touchscreen swipe ${move.xFrom} ${move.yFrom} ${move.xTo} ${move.yTo}".adbShell()
    }

    private fun getPid(): String {
        val psInfo = "ps | grep $apkPackage".adbShell()
        return psInfo.split("\\s+".toRegex())[1]
    }

    private fun getCoreNumber(): Int {
        return try {
            max(1, "nproc".adbShell().toInt())
        } catch (_: Exception) {
            1
        }
    }

    private fun takeCpuUsage(action: Action.TakeCpuUsage): CpuUsage {
        val startTime = timeProvider.getTimeMillis()

        val pid = getPid()
        val coreNumber = getCoreNumber() // TODO make call only once
        val rawCpuInfo = "top -p $pid -d 0.1 -n $SAMPLE_NUMBER".adbShell()

        val measurement = parserProvider.cpuUsageParser.parse(
            rawData = rawCpuInfo,
            sampleNumber = SAMPLE_NUMBER,
            coreNumber = coreNumber
        )

        val endTime = timeProvider.getTimeMillis()
        return CpuUsage(
            index = actionIndex++,
            name = action.title(),
            measurement = measurement,
            startTime = startTime,
            endTime = endTime,
        )
    }

    private fun takeMemoryAllocation(action: Action.TakeMemoryAllocation): Memory {
        val startTime = timeProvider.getTimeMillis()

        val rawMemInfo = "dumpsys meminfo $apkPackage -d".adbShell()
        val measurements = parserProvider.dumpSysParser.parse(rawMemInfo)

        val endTime = timeProvider.getTimeMillis()
        return Memory(
            index = actionIndex++,
            name = action.title(),
            measurements = measurements,
            startTime = startTime,
            endTime = endTime,
        )
    }

    private fun takeGfxInfo(action: Action.TakeGfxInfo): GfxInfo {
        val startTime = timeProvider.getTimeMillis()

        val rawMemInfo = "dumpsys gfxinfo $apkPackage".adbShell()
        val measurements = parserProvider.gfxInfoParser.parse(rawMemInfo)

        val endTime = timeProvider.getTimeMillis()
        return GfxInfo(
            index = actionIndex++,
            name = action.title(),
            measurements = measurements,
            startTime = startTime,
            endTime = endTime,
        )
    }

    private fun takeScreenshot(action: Action.TakeScreenshot, imagePrefix: String): Screenshot {
        val startTime = timeProvider.getTimeMillis()

        shellExecutor.makeDirForScreen("$imagePrefix/")
        "/system/bin/screencap -p $DEVICE_SCREENSHOT_PATH".adbShell()
        val index = actionIndex++
        val screenshotFileName =
            if (action.name.safeForPath().isNotBlank()) action.name.safeForPath() else index
        "pull $DEVICE_SCREENSHOT_PATH $imagePrefix/$screenshotFileName.png".adb()

        val endTime = timeProvider.getTimeMillis()
        return Screenshot(
            index = index,
            name = action.title(),
            startTime = startTime,
            endTime = endTime,
            prefix = imagePrefix,
            pathUrl = "$imagePrefix/$screenshotFileName.png"
        )
    }

    private fun takeLogcat(action: Action.TakeLogcat): Common {
        val startTime = timeProvider.getTimeMillis()

        val index = actionIndex++
        val screenshotFileName = "${serialName.safeForPath()}/logcat_$index.txt"
        "adb logcat -b ${action.buffer.bufferName} -d > $screenshotFileName".shellForScreen()

        val endTime = timeProvider.getTimeMillis()
        return Common(
            index = index,
            name = action.title(),
            startTime = startTime,
            endTime = endTime,
        )
    }

    private fun closeAppsInTray() {
        val appList =
            "dumpsys window a | grep \"/\" | cut -d \"{\" -f2 | cut -d \"/\" -f1 | cut -d \" \" -f2".adbShell()
        appList.split("\n").forEach { app ->
            "am force-stop $app".adbShell()
        }
    }

    private fun showDemoMode(action: Action.ShowDemoMode) {
        // Set the clock
        "$SYSTEM_UI_COMMAND clock -e hhmm ${action.time}".adbShell()
        // Set the wifi level to max
        "$SYSTEM_UI_COMMAND network -e wifi show -e level 4".adbShell()
        // Show the silent volume icon
        "$SYSTEM_UI_COMMAND status -e volume vibrate".adbShell()
        // Full battery
        "$SYSTEM_UI_COMMAND battery -e level 100 -e plugged false".adbShell()
        // Hide the notification icons
        "$SYSTEM_UI_COMMAND notifications -e visible false".adbShell()
    }

    private fun String.makeDir(): String = shellExecutor.makeDir(this)

    private fun String.shell(): String = shellExecutor.runShellCommand(this, fromRootFolder = true)

    private fun String.shellForScreen(): String = shellExecutor.runShellCommand(this, fromRootFolder = false)

    private fun String.adb(): String = shellExecutor.runCommandForScreen("adb", this)

    private fun String.adbShell(): String = "shell $this".adb()

    private companion object {
        private const val SAMPLE_NUMBER = 5
        private const val DEVICE_SCREENSHOT_PATH = "/sdcard/toster_screenshot_image.png"
        private const val SYSTEM_UI_COMMAND = "am broadcast -a com.android.systemui.demo -e command"
    }
}
