package fi.epicbot.toster.report

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.model.ReportOutput

interface Reporter {

    fun makeReport(reportOutput: ReportOutput, shellExecutor: ShellExecutor, shellLogger: ShellLogger)
}
