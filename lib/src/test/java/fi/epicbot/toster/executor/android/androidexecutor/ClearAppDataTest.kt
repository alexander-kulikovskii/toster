package fi.epicbot.toster.executor.android.androidexecutor

import fi.epicbot.toster.Then
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private const val CLEAR_APP_TITLE = "Clear app data"

private val PACKAGE_OUTPUT_MAP = mapOf(
    "package:$PACKAGE_NAME" to true,
    "" to false,
)

class ClearAppDataTest : BehaviorSpec({

    PACKAGE_OUTPUT_MAP.forEach { (packageOutput, clearShouldBeCalled) ->
        Given("AndroidExecutor") {
            val facade = MockedFacade()
            every {
                facade.shell("adb shell pm list packages | grep $PACKAGE_NAME")
            }.returns(packageOutput)
            val androidExecutor = provideAndroidExecutor(facade).apply {
                imagePrefix = IMAGE_PREFIX
            }

            When("Execute action TakeGfxInfo") {
                val res = androidExecutor.execute(Action.ClearAppData)
                Then("Name should be $CLEAR_APP_TITLE", res.name, CLEAR_APP_TITLE)
                Verify("check packages list shell") {
                    facade.shell("adb shell pm list packages | grep $PACKAGE_NAME")
                }
                if (clearShouldBeCalled) {
                    Verify("check clear shell") {
                        facade.adbShell("pm clear -t $PACKAGE_NAME")
                    }
                }
            }
        }
    }
})
