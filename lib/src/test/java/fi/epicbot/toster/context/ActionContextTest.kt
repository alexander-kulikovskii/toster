package fi.epicbot.toster.context

import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Buffer
import fi.epicbot.toster.model.Move
import fi.epicbot.toster.model.SwipeMove
import fi.epicbot.toster.model.TrimMemoryLevel
import fi.epicbot.toster.model.title
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll

private class ActionContextData(
    val name: String,
    val action: ActionContext.() -> Unit,
    val expectedList: List<Action>,
)

private val actionList = listOf(
    ActionContextData(
        "Click with delay",
        {
            click(4, 4, 3)
        },
        listOf(
            Action.Click(x = 4, y = 4),
            Action.Delay(3),
        )
    ),
    ActionContextData(
        "Click without delay",
        {
            click(4, 4)
        },
        listOf(
            Action.Click(x = 4, y = 4),
            Action.Delay(0),
        )
    ),
    ActionContextData(
        "long click with delay",
        {
            longClick(4, 4, 3, 4)
        },
        listOf(
            Action.LongClick(x = 4, y = 4, clickDelayMillis = 3),
            Action.Delay(4),
        )
    ),
    ActionContextData(
        "long click without delay",
        {
            longClick(4, 4, 3)
        },
        listOf(
            Action.LongClick(x = 4, y = 4, clickDelayMillis = 3),
            Action.Delay(0),
        )
    ),
    ActionContextData(
        "long click with default time",
        {
            longClick(4, 4)
        },
        listOf(
            Action.LongClick(x = 4, y = 4, clickDelayMillis = 2000L),
            Action.Delay(0),
        )
    ),
    ActionContextData(
        "delay",
        {
            delay(100)
        },
        listOf(
            Action.Delay(100),
        )
    ),
    ActionContextData(
        "take screenshot",
        {
            takeScreenshot("name")
        },
        listOf(
            Action.TakeScreenshot("name"),
        )
    ),
    ActionContextData(
        "take screenshot with default name",
        {
            takeScreenshot()
        },
        listOf(
            Action.TakeScreenshot(""),
        )
    ),
    ActionContextData(
        "swipe with move",
        {
            swipe(value = Move(xFrom = 2, yFrom = 4, xTo = 20, yTo = 28), delayMillis = 4)
        },
        listOf(
            Action.Swipe(SwipeMove.Custom(Move(xFrom = 2, yFrom = 4, xTo = 20, yTo = 28))),
            Action.Delay(4)
        )
    ),
    ActionContextData(
        "swipe with move with default delay",
        {
            swipe(value = Move(xFrom = 2, yFrom = 4, xTo = 20, yTo = 28))
        },
        listOf(
            Action.Swipe(SwipeMove.Custom(Move(xFrom = 2, yFrom = 4, xTo = 20, yTo = 28))),
            Action.Delay(0)
        )
    ),
    ActionContextData(
        "swipe with SwipeMove",
        {
            swipe(value = SwipeMove.BottomToTop, delayMillis = 23)
        },
        listOf(
            Action.Swipe(SwipeMove.BottomToTop),
            Action.Delay(23),
        )
    ),
    ActionContextData(
        "swipe with SwipeMove with default delay",
        {
            swipe(value = SwipeMove.BottomToTop)
        },
        listOf(
            Action.Swipe(SwipeMove.BottomToTop),
            Action.Delay(0),
        )
    ),
    ActionContextData(
        "take memory allocation",
        {
            takeMemoryAllocation()
        },
        listOf(
            Action.TakeMemoryAllocation,
        )
    ),
    ActionContextData(
        "resetGfxInfo",
        {
            resetGfxInfo()
        },
        listOf(
            Action.ResetGfxInfo,
        )
    ),
    ActionContextData(
        "takeGfxInfo",
        {
            takeGfxInfo()
        },
        listOf(
            Action.TakeGfxInfo,
        )
    ),
    ActionContextData(
        "runShell",
        {
            runShell(shell = "command", name = "name")
        },
        listOf(
            Action.Shell("command", "name"),
        )
    ),
    ActionContextData(
        "runShell with default name",
        {
            runShell(shell = "command")
        },
        listOf(
            Action.Shell("command", ""),
        )
    ),
    ActionContextData(
        "openHomeScreen",
        {
            openHomeScreen(42)
        },
        listOf(
            Action.OpenHomeScreen,
            Action.Delay(42),
        )
    ),
    ActionContextData(
        "openHomeScreen with default time",
        {
            openHomeScreen()
        },
        listOf(
            Action.OpenHomeScreen,
            Action.Delay(0),
        )
    ),
    ActionContextData(
        "openAppAgain",
        {
            openAppAgain(42)
        },
        listOf(
            Action.OpenAppAgain,
            Action.Delay(42),
        )
    ),
    ActionContextData(
        "openAppAgain with default time",
        {
            openAppAgain()
        },
        listOf(
            Action.OpenAppAgain,
            Action.Delay(0),
        )
    ),
    ActionContextData(
        "trimMemory",
        {
            trimMemory(TrimMemoryLevel.BACKGROUND)
        },
        listOf(
            Action.TrimMemory(TrimMemoryLevel.BACKGROUND),
        )
    ),
    ActionContextData(
        "takeCpuUsage",
        {
            takeCpuUsage()
        },
        listOf(
            Action.TakeCpuUsage,
        )
    ),
    ActionContextData(
        "clearLogcat",
        {
            clearLogcat()
        },
        listOf(
            Action.ClearLogcat,
        )
    ),
    ActionContextData(
        "takeLogcat custom",
        {
            takeLogcat(Buffer.MAIN)
        },
        listOf(
            Action.TakeLogcat(Buffer.MAIN),
        )
    ),
    ActionContextData(
        "takeLogcat default",
        {
            takeLogcat()
        },
        listOf(
            Action.TakeLogcat(Buffer.DEFAULT),
        )
    ),
)

internal class ActionContextTest : BehaviorSpec({
    actionList.forEach { actionContextData ->
        Given("check ${actionContextData.name}") {
            val actionContext = ActionContext()
            When("Invoke action") {
                actionContextData.action.invoke(actionContext)
                Then("Action list should be ${actionContextData.expectedList.joinToString { it.title() }}") {
                    val actual = actionContext.actions.map { it.title() }
                    val expected = actionContextData.expectedList.map { it.title() }
                    actual shouldContainAll expected
                }
            }
        }
    }
})
