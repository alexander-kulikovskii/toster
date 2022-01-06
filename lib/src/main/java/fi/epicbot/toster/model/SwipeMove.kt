package fi.epicbot.toster.model

private const val DEFAULT_SWIPE_OFFSET_PX = 20

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
            height - DEFAULT_SWIPE_OFFSET_PX,
            width / 2,
            DEFAULT_SWIPE_OFFSET_PX,
        )
        SwipeMove.LeftToRight -> Move(
            DEFAULT_SWIPE_OFFSET_PX,
            height / 2,
            width - DEFAULT_SWIPE_OFFSET_PX,
            height / 2,
        )
        SwipeMove.RightToLeft -> Move(
            width - DEFAULT_SWIPE_OFFSET_PX,
            height / 2,
            DEFAULT_SWIPE_OFFSET_PX,
            height / 2,
        )
        SwipeMove.TopToBottom -> Move(
            width / 2,
            DEFAULT_SWIPE_OFFSET_PX,
            width / 2,
            height - DEFAULT_SWIPE_OFFSET_PX,
        )
        else -> throw UnsupportedOperationException("Unsupported swipe type $this")
    }
}
