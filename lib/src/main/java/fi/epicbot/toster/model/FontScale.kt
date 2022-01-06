package fi.epicbot.toster.model

sealed class FontScale(val size: Double) {
    object LARGEST : FontScale(size = 1.3)
    object LARGE : FontScale(size = 1.15)
    object DEFAULT : FontScale(size = 1.0)
    object SMALL : FontScale(size = 0.85)

    override fun toString(): String {
        return when (this) {
            LARGE -> "Large"
            DEFAULT -> "Default"
            LARGEST -> "Largest"
            SMALL -> "Small"
        }
    }
}
