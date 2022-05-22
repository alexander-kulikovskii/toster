package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.MultiApk

@TosterDslMarker
class MultiApkContext {
    internal val multiApk = MultiApk()

    fun apk(init: ApkContext.() -> Unit) {
        val context = ApkContext().apply(init)
        val apk = context.apk
        multiApk.add(apk)
    }
}
