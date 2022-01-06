package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker

@TosterDslMarker
class PermissionActivityContext {
    internal val granted: MutableList<String> = mutableListOf()
    internal val revoked: MutableList<String> = mutableListOf()

    fun grand(name: String) {
        granted.add(name)
    }

    fun revoke(name: String) {
        revoked.add(name)
    }
}
