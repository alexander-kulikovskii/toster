package fi.epicbot.toster.time

internal interface TimeProvider {

    fun getTimeMillis(): Long

    fun addOffset(offset: Long)
}
