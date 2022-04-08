package fi.epicbot.toster.executor.android.android_executor

import fi.epicbot.toster.Then
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.title
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Ordering

private class MultilineActionTest(
    val action: Action,
    val expectedTitle: String,
    val expectedCommands: List<String>,
    val shellCommand: Boolean = true,
)

private val MULTILINE_ACTIONS = listOf(
    MultilineActionTest(
        Action.ShowGpuOverdraw,
        "Show gpu overdraw",
        listOf(
            "setprop debug.hwui.overdraw show",
            "setprop debug.hwui.show_overdraw true",
            "service call activity 1599295570"
        )
    ),
    MultilineActionTest(
        Action.HideGpuOverdraw,
        "Hide gpu overdraw",
        listOf(
            "setprop debug.hwui.overdraw false",
            "setprop debug.hwui.show_overdraw false",
            "service call activity 1599295570"
        )
    ),
    MultilineActionTest(
        Action.HideGpuOverdraw,
        "Hide gpu overdraw",
        listOf(
            "setprop debug.hwui.overdraw false",
            "setprop debug.hwui.show_overdraw false",
            "service call activity 1599295570"
        )
    ),
    MultilineActionTest(
        Action.ShowDemoMode("1300"),
        "Show demo mode with time 1300",
        listOf(
            "$SYSTEM_UI_COMMAND clock -e hhmm 1300",
            "$SYSTEM_UI_COMMAND network -e wifi show -e level 4",
            "$SYSTEM_UI_COMMAND status -e volume vibrate",
            "$SYSTEM_UI_COMMAND battery -e level 100 -e plugged false",
            "$SYSTEM_UI_COMMAND notifications -e visible false",
        )
    ),
    MultilineActionTest(
        Action.RestartAdbService,
        "Restart adb service",
        listOf(
            "kill-server",
            "start-server",
        ),
        shellCommand = false,
    )
)

class MultilineCommandsTest : BehaviorSpec({
    Given("AndroidExecutor") {

        MULTILINE_ACTIONS.forEach { multilineActionTest ->
            val facade = MockedFacade()
            val androidExecutor = provideAndroidExecutor(facade)
            When("Execute action ${multilineActionTest.action.title()}") {
                val res = androidExecutor.execute(multilineActionTest.action, IMAGE_PREFIX)
                Then(
                    "Name should be ${multilineActionTest.expectedTitle}",
                    res.name,
                    multilineActionTest.expectedTitle
                )
                Verify("check shell ", ordering = Ordering.SEQUENCE) {
                    multilineActionTest.expectedCommands.forEachIndexed { index, command ->
                        if (multilineActionTest.shellCommand) {
                            facade.adbShell(command)
                        } else {
                            facade.adb(command)
                        }
                    }
                }
            }
        }
    }
})
