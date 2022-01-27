package fi.epicbot.toster.time

internal class DefaultTimeProvider : TimeProvider {
    override fun getTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun addOffset(offset: Long) {
        // Do nothing
    }
}
