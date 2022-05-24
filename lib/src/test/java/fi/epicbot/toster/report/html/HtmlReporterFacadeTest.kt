package fi.epicbot.toster.report.html

import fi.epicbot.toster.Verify
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk

internal class HtmlReporterFacadeTest : BehaviorSpec({
    testData.forEach { (report, files) ->
        Given("Collage Html Reporter") {
            val reporter = HtmlReporterFacade()
            val shellExecutor: ShellExecutor = mockk(relaxed = true)
            val shellLogger: ShellLogger = mockk(relaxed = true)
            When("make report") {
                reporter.makeReport(report, shellExecutor, shellLogger)
                files.forEach { reportFile ->
                    Verify(reportFile.description) {
                        shellExecutor.makeFile(
                            path = reportFile.path,
                            fileName = reportFile.fileName,
                            content = reportFile.content,
                        )
                    }
                }
            }
        }
    }
})

private val testData = mapOf(
    DEFAULT_REPORT_OUTPUT to listOf(
        // Start page html reporter
        ReportFile(
            description = "Start page",
            path = "chart/",
            fileName = "index.html",
            content = START_PAGE,
        ),
        ReportFile(
            description = "Styles css",
            path = "chart/",
            fileName = "styles.css",
            content = STYLES_FILE,
        ),
        ReportFile(
            description = "Devices page",
            path = "chart/test_name",
            fileName = "index.html",
            content = DEVICE_PAGE,
        ),
        // Cpu html reporter
        ReportFile(
            description = "Cpu page",
            path = "chart/test_name/cpu",
            fileName = "index.html",
            content = CPU_REPORT,
        ),
        ReportFile(
            description = "Cpu chart builder",
            path = "chart/test_name/cpu",
            fileName = "chart_builder.js",
            content = CPU_BUILDER_JS,
        ),
        ReportFile(
            description = "Cpu chart data",
            path = "chart/test_name/cpu",
            fileName = "cpu_data.js",
            content = CPU_DATA_JS,
        ),
        // Memory usage html reporter
        ReportFile(
            description = "Memory usage page",
            path = "chart/test_name/memory",
            fileName = "index.html",
            content = MEMORY_REPORT,
        ),
        ReportFile(
            description = "Memory chart builder",
            path = "chart/test_name/memory",
            fileName = "chart_builder.js",
            content = MEMORY_BUILDER_JS,
        ),
        ReportFile(
            description = "Memory chart data",
            path = "chart/test_name/memory",
            fileName = "memory_data.js",
            content = MEMORY_DATA_JS,
        ),
        // Collage html reporter
        ReportFile(
            description = "Collage page",
            path = "chart/test_name/collage",
            fileName = "index.html",
            content = COLLAGE_REPORT,
        ),
    ),
)

private class ReportFile(
    val description: String,
    val path: String,
    val fileName: String,
    val content: String,
)
