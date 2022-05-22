package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Apk

@TosterDslMarker
class ApkContext {

    internal val apk = Apk()

    fun runShellsBefore(vararg shell: String) {
        apk.shellsBefore = shell
    }

    fun url(value: String) {
        apk.url = value
    }

    fun prefix(value: String) {
        apk.prefix = value
    }
}
