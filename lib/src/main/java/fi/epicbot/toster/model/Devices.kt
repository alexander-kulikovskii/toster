package fi.epicbot.toster.model

data class Devices(
    val emulators: List<Emulator> = emptyList(),
    val phones: List<Phone> = emptyList(),
)
