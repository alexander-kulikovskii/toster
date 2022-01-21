package fi.epicbot.toster.model

import kotlin.math.max

sealed class SwipeOffset(
    val offsetPx: Int,
    val offsetFactor: Double,
) {
    class HorizontalSwipeOffset(
        offsetPx: Int,
        offsetFactor: Double,
    ) : SwipeOffset(offsetPx, offsetFactor)

    class VerticalSwipeOffset(
        offsetPx: Int,
        offsetFactor: Double,
    ) : SwipeOffset(offsetPx, offsetFactor)
}

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

internal fun SwipeMove.toMove(
    horizontalSwipeOffset: SwipeOffset,
    verticalSwipeOffset: SwipeOffset,
    width: Int,
    height: Int
): Move {
    return when (this) {
        SwipeMove.BottomToTop -> Move(
            width / 2,
            height - provideOffset(verticalSwipeOffset, height),
            width / 2,
            provideOffset(verticalSwipeOffset, height),
        )
        SwipeMove.LeftToRight -> Move(
            provideOffset(horizontalSwipeOffset, width),
            height / 2,
            width - provideOffset(horizontalSwipeOffset, width),
            height / 2,
        )
        SwipeMove.RightToLeft -> Move(
            width - provideOffset(horizontalSwipeOffset, width),
            height / 2,
            provideOffset(horizontalSwipeOffset, width),
            height / 2,
        )
        SwipeMove.TopToBottom -> Move(
            width / 2,
            provideOffset(verticalSwipeOffset, height),
            width / 2,
            height - provideOffset(verticalSwipeOffset, height),
        )
        else -> throw UnsupportedOperationException("Unsupported swipe type $this")
    }
}

private fun provideOffset(swipeOffset: SwipeOffset, totalValue: Int): Int =
    max((totalValue * swipeOffset.offsetFactor).toInt(), swipeOffset.offsetPx)
