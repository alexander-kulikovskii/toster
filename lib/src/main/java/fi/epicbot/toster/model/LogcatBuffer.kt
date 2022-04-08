package fi.epicbot.toster.model

sealed class Buffer(val bufferName: String) {
    // View the buffer that contains radio/telephony related messages.
    object RADIO : Buffer("radio")

    // View the interpreted binary system event buffer messages.
    object EVENTS : Buffer("events")

    // View the main log buffer (default) does not contain system and crash log messages.
    object MAIN : Buffer("main")

    // View the system log buffer (default).
    object SYSTEM : Buffer("system")

    // View the crash log buffer (default).
    object CRASH : Buffer("crash")

    // View all buffers.
    object ALL : Buffer("all")

    // Reports main, system, and crash buffers.
    object DEFAULT : Buffer("default")
}

sealed class BufferDimension(val value: String) {
    object KILOBYTES : BufferDimension("K")
    object MEGABYTES : BufferDimension("M")
}

class BufferSize(val size: Int, val dimension: BufferDimension) {
    override fun toString(): String = "$size${dimension.value}"
}
