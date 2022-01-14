package fi.epicbot.toster.model

@Suppress("LongParameterList")
class Screen(
    var name: String = "",
    var url: String = "",
    var shortUrl: String = "",
    var delayAfterOpenMillis: Long = 2000L,
    var fontScale: FontScale = FontScale.DEFAULT,
    val activityParams: MutableList<ActivityParam> = mutableListOf(),
    val actions: MutableList<Action> = mutableListOf(),
    var clearDataBeforeRun: Boolean = false,
    var shellBefore: String = "",
    var shellAfter: String = "",
    var permissions: Permissions = Permissions(),
    var screenshotAsLastAction: Boolean = true,
)
