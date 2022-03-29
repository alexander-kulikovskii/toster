package fi.epicbot.toster.model

@Suppress("MagicNumber")
sealed class Density(open val dpi: Int) {
    object LDPI : Density(120)
    object MDPI : Density(160)
    object HDPI : Density(240)
    object XHDPI : Density(320)
    object XXHDPI : Density(480)
    object XXXHDPI : Density(640)
    object TVDPI : Density(213)
    class CUSTOM(override val dpi: Int) : Density(dpi)
}
