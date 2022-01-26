package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.ShellLoggerConfig

@TosterDslMarker
class ShellLoggerContext {
    internal val shellLogger = ShellLoggerConfig()

    fun disable() {
        shellLogger.enable = false
    }

    fun disableTimestamp() {
        shellLogger.enableTimestamp = false
    }
}
