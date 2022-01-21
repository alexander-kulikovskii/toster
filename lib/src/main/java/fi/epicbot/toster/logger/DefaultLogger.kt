package fi.epicbot.toster.logger

internal class DefaultLogger : ShellLogger {
    private val commands = mutableListOf<String>()

    override fun logCommand(command: String) {
        commands.add(command)
    }

    override fun getAllCommands(): String {
        return commands.joinToString("\n")
    }
}
