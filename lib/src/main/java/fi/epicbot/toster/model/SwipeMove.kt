package fi.epicbot.toster.model

import kotlin.math.max

private const val DEFAULT_SWIPE_OFFSET_PX = 220
private const val MIN_OFFSET_FACTOR = 0.08

data class Move(
    val xFrom: Int,
    val yFrom: Int,
    val xTo: Int,
    val yTo: Int,
)

sealed class SwipeMove {
    object LeftToRight : SwipeMove()
    object RightToLeft : SwipeMove()
    object TopToBottom : SwipeMove()
    object BottomToTop : SwipeMove()
    data class Custom(val move: Move) : SwipeMove()

    override fun toString(): String {
        return when (this) {
            BottomToTop -> "Swipe from bottom to top"
            is Custom -> "Swipe from (${move.xFrom}, ${move.yFrom}) to (${move.xTo}, ${move.yTo})"
            LeftToRight -> "Swipe from left to right"
            RightToLeft -> "Swipe from right to left"
            TopToBottom -> "Swipe from top to bottom"
        }
    }

    /**         X
     *     + - - - - - ->
     *     |
     *     |
     *   Y |
     *     |
     *    \ |
     */
}

internal fun SwipeMove.toMove(width: Int, height: Int): Move {
    return when (this) {
        SwipeMove.BottomToTop -> Move(
            width / 2,
            height - provideOffset(height),
            width / 2,
            provideOffset(height),
        )
        SwipeMove.LeftToRight -> Move(
            provideOffset(width),
            height / 2,
            width - provideOffset(width),
            height / 2,
        )
        SwipeMove.RightToLeft -> Move(
            width - provideOffset(width),
            height / 2,
            provideOffset(width),
            height / 2,
        )
        SwipeMove.TopToBottom -> Move(
            width / 2,
            provideOffset(height),
            width / 2,
            height - provideOffset(height),
        )
        else -> throw UnsupportedOperationException("Unsupported swipe type $this")
    }
}

private fun provideOffset(value: Int): Int =
    max((value * MIN_OFFSET_FACTOR).toInt(), DEFAULT_SWIPE_OFFSET_PX)
