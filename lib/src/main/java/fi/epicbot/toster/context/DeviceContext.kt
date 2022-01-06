package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Emulator
import fi.epicbot.toster.model.Phone

@TosterDslMarker
class DeviceContext {
    internal val emulators: MutableList<Emulator> = mutableListOf()
    internal val phones: MutableList<Phone> = mutableListOf()

    fun emulator(name: String) {
        emulators.add(Emulator(name))
    }

    fun phone(uuid: String) {
        phones.add(Phone(uuid))
    }

    fun emulators(vararg names: String) {
        names.forEach {
            emulator(it)
        }
    }

    fun phones(vararg uuids: String) {
        uuids.forEach {
            phone(it)
        }
    }
}
