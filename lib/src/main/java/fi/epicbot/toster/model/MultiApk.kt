package fi.epicbot.toster.model

import fi.epicbot.toster.TosterDslMarker

@TosterDslMarker
class MultiApk {

    internal val apks: MutableList<Apk> = mutableListOf()

    fun add(apk: Apk) {
        apks.add(apk)
    }
}
