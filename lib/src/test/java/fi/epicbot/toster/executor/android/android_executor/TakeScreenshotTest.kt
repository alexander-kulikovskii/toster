package fi.epicbot.toster.executor.android.android_executor

import fi.epicbot.toster.Then
import fi.epicbot.toster.ThenInstanseOf
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.report.model.Screenshot
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private const val TAKE_SCREENSHOT_TITLE = "Take screenshot"
private const val DEVICE_SCREENSHOT_PATH = "/sdcard/toster_screenshot_image.png"
private val SCREENSHOT_NAME_MAP = mapOf("" to "0", "common name" to "common_name")

class AndroidExecutorTest : BehaviorSpec({

    Given("AndroidExecutor") {
        SCREENSHOT_NAME_MAP.forEach { (name_title, name_value) ->
            val facade = MockedFacade()
            every { facade.timeProvider.getTimeMillis() }.returnsMany(1L, 42L)
            val androidExecutor = provideAndroidExecutor(facade)

            When("Execute action TakeScreenshot with <$name_title> name") {
                val res = androidExecutor.execute(Action.TakeScreenshot(name_title), IMAGE_PREFIX)
                Then("Name should be $TAKE_SCREENSHOT_TITLE", res.name, TAKE_SCREENSHOT_TITLE)
                Verify("check make dir") {
                    facade.shellExecutor.makeDirForScreen("$IMAGE_PREFIX/")
                }
                Verify("check shell") {
                    facade.adbShell("/system/bin/screencap -p $DEVICE_SCREENSHOT_PATH")
                }
                Verify("check pull", exactly = 1) {
                    facade.adb("pull $DEVICE_SCREENSHOT_PATH $IMAGE_PREFIX/$name_value.png")
                }
                Then("Index should be 0", res.index, 0)
                Then("startTime should be 1", res.startTime, 1L)
                Then("endTime should be 42", res.endTime, 42L)
                ThenInstanseOf<Screenshot>("ReportAction should be Screenshot", res)
                (res as Screenshot).let { screenshot ->
                    Then("Image prefix should be $IMAGE_PREFIX", screenshot.prefix, IMAGE_PREFIX)
                    Then("check pathUrl", screenshot.pathUrl, "$IMAGE_PREFIX/$name_value.png")
                }
            }
        }
    }
})
