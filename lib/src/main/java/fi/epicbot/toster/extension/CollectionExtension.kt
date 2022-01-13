package fi.epicbot.toster.extension

internal fun Collection<String>.throwExceptionIfOneBlank(message: String) {
    val blankCount = count { it.isBlank() }
    if (blankCount > 0) {
        throw IllegalArgumentException(message)
    }
}

internal fun Collection<String>.throwExceptionIfAllElementsNotUnique(messagePrefix: String) {
    val duplicates = groupingBy { it }.eachCount().filter { it.value > 1 }
    if (duplicates.isNotEmpty()) {
        val duplicatesMessage = duplicates.map { (k, v) -> "<$k> - $v times" }.joinToString("; ")
        val exceptionMessage = "$messagePrefix: $duplicatesMessage"
        throw IllegalArgumentException(exceptionMessage)
    }
}
