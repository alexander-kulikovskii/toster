package fi.epicbot.toster.model

class Apk(
    var url: String = "",
    var shellsBefore: Array<out String> = emptyArray(),
    var prefix: String = "default",
)

internal val EMPTY_APK = Apk()
