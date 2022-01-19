package fi.epicbot.toster.report

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.report.formatter.ReportFormatter
import fi.epicbot.toster.report.model.ReportOutput

internal class DefaultReporter(private val formatter: ReportFormatter) : Reporter {

    override fun makeReport(reportOutput: ReportOutput, shellExecutor: ShellExecutor) {
        shellExecutor.runShellCommand("echo '${formatter.format(reportOutput)}' > $OUTPUT_FILE_NAME")
    }

    private companion object {
        private const val OUTPUT_FILE_NAME = "report.json"
    }
}
