package fi.epicbot.toster.model

@Suppress("ClassNaming")
sealed class Rotation(val value: Int) {
    object PORTRAIT : Rotation(value = 0)
    object LANDSCAPE : Rotation(value = 1)
    object PORTRAIT_REVERSED : Rotation(value = 2)
    object LANDSCAPE_REVERSED : Rotation(value = 3)
}
