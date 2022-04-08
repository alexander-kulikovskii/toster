package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.Reporter
import fi.epicbot.toster.report.model.ReportOutput

internal class HtmlReporterFacade : Reporter {

    private val htmlReporters = listOf(
        StartPageHtmlReporter(),
        CpuUsageHtmlReporter(),
        MemoryUsageHtmlReporter(),
        CollageHtmlReporter(),
    )

    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger
    ) {
        htmlReporters.forEach {
            it.makeReport(reportOutput, shellExecutor, shellLogger)
        }
    }
}
