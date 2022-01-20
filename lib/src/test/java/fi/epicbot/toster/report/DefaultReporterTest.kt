package fi.epicbot.toster.report

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.report.formatter.ReportFormatter
import fi.epicbot.toster.report.model.ReportAppInfo
import fi.epicbot.toster.report.model.ReportOutput
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DefaultReporterTest : BehaviorSpec({
    Given("Default reporter") {
        val mockedReporter = mockk<ReportFormatter>(relaxed = true)
        every { mockedReporter.format(reportOutput) } returns REPORT_OUTPUT
        val defaultReporter = DefaultReporter(mockedReporter)
        val mockedShellExecutor = mockk<ShellExecutor>(relaxed = true)
        When("make report") {

            defaultReporter.makeReport(reportOutput, mockedShellExecutor)
            Then("formatter should be called") {
                verify {
                    mockedReporter.format(reportOutput)
                }
            }
            Then("shell executor should be called") {
                verify {
                    mockedShellExecutor.runShellCommand(COMMAND_OUTPUT, false)
                }
            }
        }
    }
})

private val reportOutput = ReportOutput(
    ReportAppInfo(appName = "Test", testTime = 0L),
    devices = mutableListOf()
)

private val REPORT_OUTPUT = """
{
    "appInfo": {
        "appName": "Test",
        "testTime": 0
    },
    "devices": [
    ]
}
""".trimIndent()

private val COMMAND_OUTPUT = "echo '$REPORT_OUTPUT' > report.json"
