package fi.epicbot.toster.report.html

import fi.epicbot.toster.Verify
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.html.BaseHtmlReporter.Companion.LIB_VERSION
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk

internal class CollageHtmlReporterTest : BehaviorSpec({
    testData.forEach { (report, expectedAnswer) ->
        Given("Collage Html Reporter") {
            val reporter = CollageHtmlReporter()
            val shellExecutor: ShellExecutor = mockk(relaxed = true)
            val shellLogger: ShellLogger = mockk(relaxed = true)
            When("make report") {
                reporter.makeReport(report, shellExecutor, shellLogger)
                Verify("html should be $expectedAnswer") {
                    shellExecutor.makeFile(
                        "chart/test_name/collage",
                        "index.html",
                        expectedAnswer
                    )
                }
            }
        }
    }
})

private val COLLAGE_REPORT = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../../styles.css">
</head>

<body>

<h1><a href="../../index.html">app</a> / <a href="../index.html">test_name</a> / Collage</h1>

<div class="grid-container">
<div>
  <h3>common 2</h3>
  <h4>test</h4>
<img src="../../../test/234" height="500"></div>

</div>

<div>generated with <a href="https://github.com/alexander-kulikovskii/toster">toster version $LIB_VERSION</a></div>

</body>
</html>
""".trimIndent()

private val testData = mapOf(
    DEFAULT_REPORT_OUTPUT to COLLAGE_REPORT
)
