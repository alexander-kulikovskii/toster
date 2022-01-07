package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker

@TosterDslMarker
class PermissionAppContext {
    internal val granted: MutableList<String> = mutableListOf()

    fun grant(name: String) {
        granted.add(name)
    }
}
