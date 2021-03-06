package fi.epicbot.toster.extension

internal fun Collection<String>.throwExceptionIfOneBlank(message: String) {
    val blankCount = filter { it.isBlank() }.count() // replace count{} as workaround for pitest
    if (blankCount > 0) {
        throw IllegalArgumentException(message)
    }
}

internal fun Collection<String>.throwExceptionIfAllBlank(message: String) {
    throwExceptionIfAll(message) { it.isBlank() }
}

internal fun Collection<String>.throwExceptionIfAllNotBlank(message: String) {
    throwExceptionIfAll(message) { it.isNotBlank() }
}

internal fun Collection<String>.throwExceptionIfAllElementsNotUnique(messagePrefix: String) {
    val duplicates = groupingBy { it }.eachCount().filter { it.value > 1 }
    if (duplicates.isNotEmpty()) {
        val duplicatesMessage = duplicates.map { (k, v) -> "<$k> - $v times" }.joinToString("; ")
        val exceptionMessage = "$messagePrefix: $duplicatesMessage"
        throw IllegalArgumentException(exceptionMessage)
    }
}

private fun <T> Collection<T>.throwExceptionIfAll(
    message: String,
    predicate: (T) -> Boolean,
) {
    if (this.all { predicate(it) }) {
        throw IllegalArgumentException(message)
    }
}
