package fi.epicbot.toster.logger

internal class DefaultLogger : ShellLogger {
    private val commands = mutableListOf<CommandRecord>()

    override fun logCommand(command: String) {
        commands.add(CommandRecord(System.currentTimeMillis(), command))
    }

    override fun getAllCommands(timestampEnabled: Boolean): String {
        return commands.joinToString(separator = "\n") { it.toString(timestampEnabled) }
    }
}

private class CommandRecord(
    val timestamp: Long,
    val command: String,
) {
    fun toString(timestampEnabled: Boolean) = if (timestampEnabled) {
        "$timestamp\t$command"
    } else {
        command
    }
}
