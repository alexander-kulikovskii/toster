package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Move
import fi.epicbot.toster.model.SwipeMove

@TosterDslMarker
class ActionContext {
    internal val actions: MutableList<Action> = mutableListOf()

    fun click(x: Int, y: Int, delayMillis: Long = 0) {
        actions.add(Action.Click(x = x, y = y))
        delay(delayMillis)
    }

    fun longClick(x: Int, y: Int, clickDelayMs: Long = 200L, delayMillis: Long = 0) {
        actions.add(Action.LongClick(x = x, y = y, clickDelayMs = clickDelayMs))
        delay(delayMillis)
    }

    fun delay(delayMillis: Long) {
        actions.add(Action.Delay(delayMillis))
    }

    fun takeScreenshot(name: String = "") {
        actions.add(Action.TakeScreenshot(name))
    }

    fun swipe(value: Move, delayMillis: Long = 0) {
        swipe(SwipeMove.Custom(value), delayMillis)
    }

    fun swipe(value: SwipeMove, delayMillis: Long = 0) {
        actions.add(Action.Swipe(value))
        delay(delayMillis)
    }

    fun takeMemoryAllocation() {
        actions.add(Action.TakeMemoryAllocation)
    }

    fun runShell(shell: String, name: String = "") {
        actions.add(Action.Shell(shell, name))
    }
}
