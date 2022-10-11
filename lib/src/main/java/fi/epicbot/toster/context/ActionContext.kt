package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Buffer
import fi.epicbot.toster.model.Move
import fi.epicbot.toster.model.SwipeMove
import fi.epicbot.toster.model.TrimMemoryLevel

@TosterDslMarker
@Suppress("TooManyFunctions")
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

    fun trimMemory(trimMemoryLevel: TrimMemoryLevel) {
        actions.add(Action.TrimMemory(trimMemoryLevel))
    }

    fun takeCpuUsage() {
        actions.add(Action.TakeCpuUsage)
    }

    fun clearLogcat() {
        actions.add(Action.ClearLogcat)
    }

    fun takeLogcat(buffer: Buffer = Buffer.DEFAULT) {
        actions.add(Action.TakeLogcat(buffer))
    }

    /**
     * Closest action to make zoom in. First "finger" will be set to [centerX]; [centerY],
     * second one will make swipe from [centerX]; [centerY] to [moveToX]; [moveToY]
     * For example 500,500; 1000,1000
     *
     * @param zoomDelayMillis zoom action duration in milliseconds
     */
    @Suppress("LongParameterList")
    fun zoomIn(
        centerX: Int,
        centerY: Int,
        moveToX: Int,
        moveToY: Int,
        zoomDelayMillis: Long = 1000L,
        delayMillis: Long = 0,
    ) {
        actions.add(
            Action.Zoom(
                centerX = centerX,
                centerY = centerY,
                fromX = centerX,
                fromY = centerY,
                toX = moveToX,
                toY = moveToY,
                zoomDelayMillis = zoomDelayMillis,
                delayMillis = delayMillis
            )
        )
        delay(delayMillis)
    }

    /**
     * Closest action to make zoom out. First "finger" will be set to [centerX]; [centerY],
     * second one will make swipe from [centerX]; [centerY] to [moveToX]; [moveToY]
     * For example 1000,1000; 500,500
     *
     * @param zoomDelayMillis zoom action duration in milliseconds
     */
    @Suppress("LongParameterList")
    fun zoomOut(
        centerX: Int,
        centerY: Int,
        moveToX: Int,
        moveToY: Int,
        zoomDelayMillis: Long = 1000L,
        delayMillis: Long = 0,
    ) {
        actions.add(
            Action.Zoom(
                centerX = centerX,
                centerY = centerY,
                fromX = moveToX,
                fromY = moveToY,
                toX = centerX,
                toY = centerY,
                zoomDelayMillis = zoomDelayMillis,
                delayMillis = delayMillis
            )
        )
        delay(delayMillis)
    }
}
