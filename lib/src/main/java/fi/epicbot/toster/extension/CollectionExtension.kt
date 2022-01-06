package fi.epicbot.toster.extension

internal fun Collection<String>.throwExceptionIfOneBlank(message: String) {
    val blankCount = count { it.isBlank() }
    if (blankCount > 0) {
        throw IllegalArgumentException(message)
    }
}
