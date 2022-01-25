package fi.epicbot.toster.model

class Permissions(
    val granted: List<String> = emptyList(),
    val revoked: List<String> = emptyList(),
)
