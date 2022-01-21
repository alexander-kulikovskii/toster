package fi.epicbot.toster.logger

interface ShellLogger {
    fun logCommand(command: String)

    fun getAllCommands(): String
}
