package fi.epicbot.toster.model

data class Permissions(
    val granted: List<String> = emptyList(),
    val revoked: List<String> = emptyList(),
)
