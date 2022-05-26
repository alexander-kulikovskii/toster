package fi.epicbot.toster.executor.android.androidexecutor

import fi.epicbot.toster.Then
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Move
import fi.epicbot.toster.model.SwipeMove
import fi.epicbot.toster.model.SwipeOffset
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private class SwipeTestObject(
    val action: Action.Swipe,
    val name: String,
    val deviceSize: String,
    val expectedMove: String,
)

private val SWIPE_ACTIONS = listOf(
    SwipeTestObject(
        Action.Swipe(SwipeMove.BottomToTop),
        name = "Swipe from bottom to top",
        deviceSize = "1000x2000",
        expectedMove = "500 1840 500 160",
    ),
    SwipeTestObject(
        Action.Swipe(SwipeMove.TopToBottom),
        name = "Swipe from top to bottom",
        deviceSize = "100x100",
        expectedMove = "50 8 50 92",
    ),
    SwipeTestObject(
        Action.Swipe(SwipeMove.LeftToRight),
        name = "Swipe from left to right",
        deviceSize = "100x200",
        expectedMove = "5 100 95 100",
    ),
    SwipeTestObject(
        Action.Swipe(SwipeMove.RightToLeft),
        name = "Swipe from right to left",
        deviceSize = "1000x1",
        expectedMove = "950 0 50 0",
    ),
    SwipeTestObject(
        Action.Swipe(SwipeMove.Custom(Move(1, 2, 100, 200))),
        name = "Swipe from (1, 2) to (100, 200)",
        deviceSize = "100x200",
        expectedMove = "1 2 100 200",
    ),
)

class SwipeTest : BehaviorSpec({

    Given("AndroidExecutor") {
        SWIPE_ACTIONS.forEach {
            val facade = MockedFacade()
            every {
                facade.adbShell("wm size")
            }.returns("Physical size: ${it.deviceSize}")
            every {
                facade.config.verticalSwipeOffset
            }.returns(
                SwipeOffset.VerticalSwipeOffset(
                    offsetPx = 220,
                    offsetFactor = 0.08,
                )
            )
            every {
                facade.config.horizontalSwipeOffset
            }.returns(
                SwipeOffset.HorizontalSwipeOffset(
                    offsetPx = 160,
                    offsetFactor = 0.05,
                )
            )
            val androidExecutor = provideAndroidExecutor(facade).apply {
                imagePrefix = IMAGE_PREFIX
            }

            When("Execute action TakeGfxInfo") {
                val res = androidExecutor.execute(it.action)
                Then("Name should be ${it.name}", res.name, it.name)
                if (it.action.swipeMove !is SwipeMove.Custom) {
                    Verify("check shell wm") {
                        facade.adbShell("wm size")
                    }
                }
                Verify("check shell") {
                    facade.adbShell("input touchscreen swipe ${it.expectedMove}")
                }
            }
        }
    }
})
