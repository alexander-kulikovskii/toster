package fi.epicbot.toster.report

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.report.model.ReportOutput
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class DefaultReporter : Reporter {

    override fun makeReport(reportOutput: ReportOutput, shellExecutor: ShellExecutor) {
        val format = Json { prettyPrint = true }
        val stringJson = "'${format.encodeToString(reportOutput)}'"

        shellExecutor.runShellCommand("echo $stringJson > $OUTPUT_FILE_NAME")
    }

    private companion object {
        private const val OUTPUT_FILE_NAME = "report.json"
    }
}
