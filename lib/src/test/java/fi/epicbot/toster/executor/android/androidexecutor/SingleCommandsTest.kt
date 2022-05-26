package fi.epicbot.toster.executor.android.androidexecutor

import fi.epicbot.toster.Then
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.BufferDimension
import fi.epicbot.toster.model.BufferSize
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.ScreenSize
import fi.epicbot.toster.model.TrimMemoryLevel
import fi.epicbot.toster.model.title
import io.kotest.core.spec.style.BehaviorSpec

private class CommonActionTest(
    val action: Action,
    val expectedTitle: String,
    val expectedCommand: String,
)

private val COMMON_ACTIONS = listOf(
    CommonActionTest(Action.Click(x = 3, y = 2), "Click to (3;2)", "input tap 3 2"),
    CommonActionTest(Action.CloseApp, "Close app", "am force-stop $PACKAGE_NAME"),
    CommonActionTest(
        Action.SetDemoModeEnable,
        "Set demo mode enable",
        "settings put global sysui_demo_allowed 1"
    ),
    CommonActionTest(Action.HideDemoMode, "Hide demo mode", "$SYSTEM_UI_COMMAND exit"),
    CommonActionTest(
        Action.SetFontScale(FontScale.DEFAULT),
        "Set font scale <Default>",
        "settings put system font_scale 1.0"
    ),
    CommonActionTest(
        Action.SetFontScale(FontScale.SMALL),
        "Set font scale <Small>",
        "settings put system font_scale 0.85"
    ),
    CommonActionTest(
        Action.SetFontScale(FontScale.LARGE),
        "Set font scale <Large>",
        "settings put system font_scale 1.15"
    ),
    CommonActionTest(
        Action.SetFontScale(FontScale.LARGEST),
        "Set font scale <Largest>",
        "settings put system font_scale 1.3"
    ),
    CommonActionTest(Action.DeleteApk, "Delete apk", "pm uninstall $PACKAGE_NAME"),
//    CommonActionTestObject(Action.InstallApk("/test/app.apk"), "Install apk", "install -g /test/app.apk"),
    CommonActionTest(
        Action.LongClick(x = 6, y = 100, clickDelayMillis = 200),
        "Long click to (6;100) for 200 ms",
        "input touchscreen swipe 6 100 6 100 200"
    ),
    CommonActionTest(
        Action.GrantPermission(permission = "PERMISSION"),
        "Grant permission <PERMISSION>",
        "pm grant $PACKAGE_NAME PERMISSION"
    ),
    CommonActionTest(
        Action.RevokePermission(permission = "PERMISSION"),
        "Revoke permission <PERMISSION>",
        "pm revoke $PACKAGE_NAME PERMISSION"
    ),
    CommonActionTest(Action.CloseKeyboard, "Close keyboard", "input keyevent 111"),
    CommonActionTest(Action.OpenHomeScreen, "Open home screen", "input keyevent 3"),
    CommonActionTest(Action.ClickBack, "Click back", "input keyevent 4"),
    CommonActionTest(Action.SendKeyEvent("42"), "Send key event <42>", "input keyevent 42"),
    CommonActionTest(
        Action.ResetGfxInfo,
        "Reset gfxinfo",
        "dumpsys gfxinfo $PACKAGE_NAME --reset"
    ),
    CommonActionTest(
        Action.OpenAppAgain,
        "Open app again",
        "monkey -p $PACKAGE_NAME -c android.intent.category.LAUNCHER 1"
    ),
    CommonActionTest(
        Action.TrimMemory(TrimMemoryLevel.RUNNING_CRITICAL),
        "Send trim memory <RUNNING_CRITICAL>",
        "am send-trim-memory $PACKAGE_NAME RUNNING_CRITICAL"
    ),
    CommonActionTest(
        Action.SetScreenDensity(Density.TVDPI),
        "Set screen density <213>",
        "wm density 213"
    ),
    CommonActionTest(
        Action.SetScreenDensity(Density.CUSTOM(42)),
        "Set screen density <42>",
        "wm density 42"
    ),
    CommonActionTest(
        Action.SetScreenSize(ScreenSize(width = 24, height = 42)),
        "Set screen size <24x42>",
        "wm size 24x42"
    ),
    CommonActionTest(
        Action.ResetScreenDensity,
        "Reset screen density",
        "wm density reset"
    ),
    CommonActionTest(
        Action.ResetScreenSize,
        "Reset screen size",
        "wm size reset"
    ),
    CommonActionTest(
        Action.ClearLogcat,
        "Clear logcat",
        "logcat -c"
    ),
    CommonActionTest(
        Action.SetLogcatBufferSize(bufferSize = BufferSize(42, dimension = BufferDimension.KILOBYTES)),
        "Set logcat buffer size 42K",
        "logcat -G 42K"
    ),
    CommonActionTest(
        Action.SetLogcatBufferSize(bufferSize = BufferSize(2, dimension = BufferDimension.MEGABYTES)),
        "Set logcat buffer size 2M",
        "logcat -G 2M"
    ),
)

class SingleCommandsTest : BehaviorSpec({
    Given("AndroidExecutor") {
        COMMON_ACTIONS.forEach { commonActionTestObject ->
            val facade = MockedFacade()
            val androidExecutor = provideAndroidExecutor(facade).apply {
                imagePrefix = IMAGE_PREFIX
            }
            When("Execute action ${commonActionTestObject.action.title()}") {
                val res = androidExecutor.execute(commonActionTestObject.action)
                Then(
                    "Name should be ${commonActionTestObject.expectedTitle}",
                    res.name,
                    commonActionTestObject.expectedTitle
                )
                Verify("check shell") {
                    facade.adbShell(commonActionTestObject.expectedCommand)
                }
            }
        }
    }
})
