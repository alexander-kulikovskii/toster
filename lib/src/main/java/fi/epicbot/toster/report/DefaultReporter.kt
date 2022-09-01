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
        val formattedReportOutput = formatter.format(reportOutput)
        writeReportOutputToFile(formattedReportOutput, shellExecutor)
    }

    private fun writeReportOutputToFile(reportOutput: String, shellExecutor: ShellExecutor) {
        val chunks = reportOutput
            .split(NEW_LINE_DIVIDER)
            .chunked(MAX_LINE_PER_CHUNK)
            .map { it.joinToString(NEW_LINE_DIVIDER) }
        for (chunk in chunks) {
            shellExecutor.runShellCommand("echo '$chunk' >> $OUTPUT_FILE_NAME")
        }
    }

    private companion object {
        private const val OUTPUT_FILE_NAME = "report.json"
        private const val OUTPUT_LOG_NAME = "log.txt"
        private const val MAX_LINE_PER_CHUNK = 1000
        private const val NEW_LINE_DIVIDER = "\n"
    }
}
