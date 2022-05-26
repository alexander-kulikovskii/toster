package fi.epicbot.toster.executor.android.androidexecutor

import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.Then
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.title
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Ordering

private class OpenActivityActionTest(
    val action: Action,
    val activityName: String,
    val expectedTitle: String,
    val expectedCommand: String,
)

private val OPEN_ACTIVITY_ACTIONS = listOf(

    OpenActivityActionTest(
        Action.OpenScreen(
            Screen(name = "test screen", url = "fi.test.MainActivity"),
            params = " --es key value"
        ),
        activityName = "test screen",
        "Start activity with  --es key value",
        "am start -n $PACKAGE_NAME/fi.test.MainActivity --es key value"
    ),
    OpenActivityActionTest(
        Action.OpenScreen(
            Screen(name = "second", shortUrl = "MainActivity"),
            params = " --ef key value"
        ),
        activityName = "second",
        "Start activity with  --ef key value",
        "am start -n $PACKAGE_NAME/$PACKAGE_NAME.MainActivity --ef key value"
    ),
)

class OpenActivityTest : BehaviorSpec({
    Given("AndroidExecutor") {

        OPEN_ACTIVITY_ACTIONS.forEach { activityOpenActionTest ->
            val facade = MockedFacade()
            val androidExecutor = provideAndroidExecutor(facade)
            When("Execute action ${activityOpenActionTest.action.title()}") {
                val res = androidExecutor.execute(activityOpenActionTest.action, IMAGE_PREFIX)
                Then(
                    "Name should be ${activityOpenActionTest.expectedTitle}",
                    res.name,
                    activityOpenActionTest.expectedTitle
                )
                CoVerify("check shell", ordering = Ordering.SEQUENCE) {
                    facade.shellExecutor.setScreenDirAndMakeIt("$SERIAL_NAME/${activityOpenActionTest.activityName}")
                    facade.adbShell(activityOpenActionTest.expectedCommand)
                    facade.shellExecutor.delay(2000L)
                }
            }
        }
    }
})
