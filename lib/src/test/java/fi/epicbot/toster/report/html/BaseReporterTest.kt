package fi.epicbot.toster.report.html

import fi.epicbot.toster.Verify
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.Device
import fi.epicbot.toster.report.model.ReportAppInfo
import fi.epicbot.toster.report.model.ReportBuild
import fi.epicbot.toster.report.model.ReportCollage
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportOutput
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.report.model.Screenshot
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk

internal abstract class BaseReporterTest(
    private val reporter: BaseHtmlReporter,
    private val path: String,
    private val testData: Map<ReportOutput, String>,
) : BehaviorSpec({
    testData.forEach { (report, expectedAnswer) ->
        Given("Html reporter ${reporter.javaClass.canonicalName}") {
            val shellExecutor: ShellExecutor = mockk(relaxed = true)
            val shellLogger: ShellLogger = mockk(relaxed = true)
            When("make report") {
                reporter.makeReport(report, shellExecutor, shellLogger)
                Verify("html should be $expectedAnswer") {
                    shellExecutor.makeFile(
                        path,
                        "index.html",
                        expectedAnswer
                    )
                }
            }
        }
    }
})

private val appInfo = ReportAppInfo("app", 12345L)

internal val DEFAULT_REPORT_OUTPUT = ReportOutput(
    appInfo,
    mutableListOf(
        ReportBuild(
            name = "test",
            devices = mutableListOf(
                ReportDevice(
                    device = Device(type = "test_type", name = "test_name"),
                    reportScreens = listOf(
                        ReportScreen(
                            name = "test_before",
                        ),
                        ReportScreen(
                            name = "test_screnn",
                            common = mutableListOf(
                                Common(
                                    index = 1,
                                    name = "common",
                                    startTime = 1234L,
                                    endTime = 2345L,
                                )
                            ),
                            screenshots = mutableListOf(
                                Screenshot(
                                    index = 2,
                                    name = "common",
                                    startTime = 1234L,
                                    endTime = 2345L,
                                    prefix = "prefix",
                                    pathUrl = "123",
                                    localUrl = "234",
                                )
                            )
                        ),
                        ReportScreen(
                            name = "test_after",
                        ),
                    ),
                    collage = ReportCollage()
                )
            )
        )
    )
)
