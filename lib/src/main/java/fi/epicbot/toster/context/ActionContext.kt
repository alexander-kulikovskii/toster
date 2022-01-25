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

    /**
     * Make long click action
     *
     * @param x Coordinate X, from 0 to width.
     * @param y Coordinate Y, from 0 to height.
     * @param clickDelayMillis How long this action should be. By default 2000 millis.
     * @param delayMillis Delay after action. By default 0 millis.
     */
    fun longClick(x: Int, y: Int, clickDelayMillis: Long = 2000L, delayMillis: Long = 0) {
        actions.add(Action.LongClick(x = x, y = y, clickDelayMillis = clickDelayMillis))
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

    fun resetGfxInfo() {
        actions.add(Action.ResetGfxInfo)
    }

    fun takeGfxInfo() {
        actions.add(Action.TakeGfxInfo)
    }

    fun runShell(shell: String, name: String = "") {
        actions.add(Action.Shell(shell, name))
    }

    fun openHomeScreen(delayMillis: Long = 0) {
        actions.add(Action.OpenHomeScreen)
        delay(delayMillis)
    }

    fun openAppAgain(delayMillis: Long = 0) {
        actions.add(Action.OpenAppAgain)
        delay(delayMillis)
    }
}
