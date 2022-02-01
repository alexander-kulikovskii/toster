package fi.epicbot.toster.model

@Suppress("ClassNaming")
sealed class TrimMemoryLevel(val level: String) {
    object BACKGROUND : TrimMemoryLevel("BACKGROUND")
    object COMPLETE : TrimMemoryLevel("COMPLETE")
    object MODERATE : TrimMemoryLevel("MODERATE")
    object RUNNING_CRITICAL : TrimMemoryLevel("RUNNING_CRITICAL")
    object RUNNING_LOW : TrimMemoryLevel("RUNNING_LOW")
    object RUNNING_MODERATE : TrimMemoryLevel("RUNNING_MODERATE")
    object UI_HIDDEN : TrimMemoryLevel("UI_HIDDEN")
}
