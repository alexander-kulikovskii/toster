package fi.epicbot.toster.report

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.model.ShellLoggerConfig
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
        val mockedShellLoggerConfig = mockk<ShellLoggerConfig>(relaxed = true)
        every { mockedShellLoggerConfig.enable } returns true
        every { mockedShellLoggerConfig.enableTimestamp } returns true
        val defaultReporter = DefaultReporter(mockedReporter, mockedShellLoggerConfig)
        val mockedShellExecutor = mockk<ShellExecutor>(relaxed = true)
        val mockedShellLogger = mockk<ShellLogger>(relaxed = true)
        When("make report") {

            defaultReporter.makeReport(reportOutput, mockedShellExecutor, mockedShellLogger)
            Then("formatter should be called") {
                verify {
                    mockedReporter.format(reportOutput)
                }
            }
            Then("shell executor should be called") {
                verify {
                    mockedShellExecutor.runShellCommand(LOGGER_OUTPUT, false)
                }
                verify {
                    mockedShellExecutor.runShellCommand(COMMAND_OUTPUT, false)
                }
            }
            Then("shell logger should be called") {
                verify {
                    mockedShellLogger.getAllCommands(true)
                }
            }
        }
    }

    Given("Default reporter with huge report") {
        val mockedReporter = mockk<ReportFormatter>(relaxed = true)
        every { mockedReporter.format(reportOutput) } returns REPORT_OUTPUT.repeat(300)
        val mockedShellLoggerConfig = mockk<ShellLoggerConfig>(relaxed = true)
        every { mockedShellLoggerConfig.enable } returns true
        every { mockedShellLoggerConfig.enableTimestamp } returns true
        val defaultReporter = DefaultReporter(mockedReporter, mockedShellLoggerConfig)
        val mockedShellExecutor = mockk<ShellExecutor>(relaxed = true)
        val mockedShellLogger = mockk<ShellLogger>(relaxed = true)
        When("make report") {

            defaultReporter.makeReport(reportOutput, mockedShellExecutor, mockedShellLogger)
            Then("formatter should be called") {
                verify {
                    mockedReporter.format(reportOutput)
                }
            }
            Then("shell executor should be called") {
                verify {
                    mockedShellExecutor.runShellCommand(LOGGER_OUTPUT, false)
                }
                verify(exactly = 4) {
                    mockedShellExecutor.runShellCommand(any(), false)
                }
            }
            Then("shell logger should be called") {
                verify {
                    mockedShellLogger.getAllCommands(true)
                }
            }
        }
    }
})

private val reportOutput = ReportOutput(
    ReportAppInfo(appName = "Test", testTime = 0L),
    builds = mutableListOf(),
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

private val COMMAND_OUTPUT = "echo '$REPORT_OUTPUT' >> report.json"
private const val LOGGER_OUTPUT = "echo '' > log.txt"
