package fi.epicbot.toster.report

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.model.ShellLoggerConfig
import fi.epicbot.toster.report.formatter.ReportFormatter
import fi.epicbot.toster.report.model.ReportOutput

internal class DefaultReporter(
    private val formatter: ReportFormatter,
    private val shellLoggerConfig: ShellLoggerConfig,
) : Reporter {

    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger,
    ) {
        if (shellLoggerConfig.enable) {
            shellExecutor.runShellCommand(
                "echo '${shellLogger.getAllCommands(shellLoggerConfig.enableTimestamp)}' > $OUTPUT_LOG_NAME"
            )
        }
        shellExecutor.runShellCommand("echo '${formatter.format(reportOutput)}' > $OUTPUT_FILE_NAME")
    }

    private companion object {
        private const val OUTPUT_FILE_NAME = "report.json"
        private const val OUTPUT_LOG_NAME = "log.txt"
    }
}
