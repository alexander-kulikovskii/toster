package fi.epicbot.toster.model

class Devices(
    val emulators: List<Emulator> = emptyList(),
    val phones: List<Phone> = emptyList(),
)
