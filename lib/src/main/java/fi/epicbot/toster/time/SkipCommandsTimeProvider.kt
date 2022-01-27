package fi.epicbot.toster.time

internal class SkipCommandsTimeProvider(
    private val startTimeMillis: Long = System.currentTimeMillis()
) : TimeProvider {
    private var currentTimeMillis = startTimeMillis

    override fun getTimeMillis(): Long {
        currentTimeMillis += TIME_DELTA_MILLIS
        return currentTimeMillis
    }

    override fun addOffset(offset: Long) {
        currentTimeMillis += offset
    }

    private companion object {
        private const val TIME_DELTA_MILLIS = 10L
    }
}
