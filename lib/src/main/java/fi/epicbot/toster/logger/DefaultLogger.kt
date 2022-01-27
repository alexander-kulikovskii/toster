package fi.epicbot.toster.logger

import fi.epicbot.toster.time.TimeProvider

internal class DefaultLogger(private val timeProvider: TimeProvider) : ShellLogger {
    private val commands = mutableListOf<CommandRecord>()

    override fun logCommand(command: String) {
        commands.add(CommandRecord(timeProvider.getTimeMillis(), command))
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
