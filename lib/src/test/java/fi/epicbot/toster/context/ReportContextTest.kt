package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.model.ReportConfig
import fi.epicbot.toster.report.Reporter
import fi.epicbot.toster.report.model.ReportOutput
import io.kotest.core.spec.style.BehaviorSpec

private class ReportContextData(
    val name: String,
    val action: ReportContext.() -> Unit,
    val expectedReport: ReportConfig,
)

private val REPORTER = object : Reporter {
    override fun makeReport(
        reportOutput: ReportOutput,
        shellExecutor: ShellExecutor,
        shellLogger: ShellLogger
    ) {
        // do nothing
    }
}

private val reportContextList = listOf(
    ReportContextData(
        name = "Default report",
        action = {},
        expectedReport = ReportConfig(enable = true, customReporters = mutableListOf())
    ),
    ReportContextData(
        name = "Disable report",
        action = {
            disable()
        },
        expectedReport = ReportConfig(enable = false, customReporters = mutableListOf())
    ),
    ReportContextData(
        name = "Add custom reporter",
        action = {
            addCustomReporter(REPORTER)
        },
        expectedReport = ReportConfig(enable = true, customReporters = mutableListOf(REPORTER))
    ),
)

internal class ReportContextTest : BehaviorSpec({
    reportContextList.forEach { reportData ->
        Given("check ${reportData.name}") {
            val reportContext = ReportContext()
            When("Invoke action") {
                reportData.action.invoke(reportContext)
                val actualReport = reportContext.report
                val expectedReport = reportData.expectedReport

                Then("Check enable", expectedReport.enable, actualReport.enable)
                Then("Check custom reporters list", expectedReport.customReporters, actualReport.customReporters)
            }
        }
    }
})
