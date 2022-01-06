package fi.epicbot.toster.extension

internal fun CharSequence.throwExceptionIfBlank(message: String = "") {
    if (this.isBlank()) {
        throw IllegalArgumentException(message)
    }
}

internal fun CharSequence.throwExceptionIfNotEmptyButBlank(message: String = "") {
    if (this.isNotEmpty() && this.isBlank()) {
        throw IllegalArgumentException(message)
    }
}

internal fun String.findRow(name: String): List<String> {
    return split(name)[1].split(" ").filter {
        it.isNotEmpty()
    }
}

internal fun String.saveForPath(): String {
    return this.trim().replace("[.,;!@#$%^&*()+=>< ]".toRegex(), "_")
}
