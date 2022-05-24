package fi.epicbot.toster.report.html

import fi.epicbot.toster.Verify
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.logger.ShellLogger
import fi.epicbot.toster.report.html.BaseHtmlReporter.Companion.CHART_VERSION
import fi.epicbot.toster.report.html.BaseHtmlReporter.Companion.LIB_VERSION
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk

internal class MemoryUsageHtmlReporterTest : BehaviorSpec({
    testData.forEach { (report, expectedAnswer) ->
        Given("Memory Usage Reporter") {
            val reporter = MemoryUsageHtmlReporter()
            val shellExecutor: ShellExecutor = mockk(relaxed = true)
            val shellLogger: ShellLogger = mockk(relaxed = true)
            When("make report") {
                reporter.makeReport(report, shellExecutor, shellLogger)
                Verify("chat builder should be") {
                    shellExecutor.makeFile(
                        "chart/test_name/memory",
                        "chart_builder.js",
                        expectedAnswer["builder"]!!
                    )
                }
                Verify("html should be $expectedAnswer") {
                    shellExecutor.makeFile(
                        "chart/test_name/memory",
                        "index.html",
                        expectedAnswer["html"]!!
                    )
                }
                Verify("cpu data should be") {
                    shellExecutor.makeFile(
                        "chart/test_name/memory",
                        "memory_data.js",
                        expectedAnswer["data"]!!
                    )
                }
            }
        }
    }
})

private val MEMORY_REPORT = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../../styles.css">
    <script src="memory_data.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/$CHART_VERSION/chart.js"></script>
</head>

<body>

<h1><a href="../../index.html">app</a> / <a href="../index.html">test_name</a> / Memory</h1>

<div>
  <h2>test_screnn</h2>
  <canvas id="chart0" height="80"></canvas>
</div>


<div>generated with <a href="https://github.com/alexander-kulikovskii/toster">toster version $LIB_VERSION</a></div>

<script type="text/javascript" src="chart_builder.js"></script>
</body>
</html>
""".trimIndent()

private val BUILDER_JS = """
const ctx0 = document.getElementById('chart0');
const data0 = {
    labels: labels0,
    datasets: dataSets0,
};
const chart0 = new Chart(ctx0, {
    type: 'line',
    data: data0
});
""".trimIndent()

private val DATA_JS = """
var labelName = "Memory"
var labels0 = []
var dataSets0 = [
{
    label: "Dalvik memory (test)",
    data: [],
    fill: false,
    borderColor: "rgb(255, 95, 95)",
    backgroundColor: "rgba(255, 95, 95, 0.8)",
    tension: 0.3
},
{
    label: "Native memory (test)",
    data: [],
    fill: false,
    borderColor: "rgb(255, 177, 86)",
    backgroundColor: "rgba(255, 177, 86, 0.8)",
    tension: 0.3
},
]
""".trimIndent()

private val testData = mapOf(
    DEFAULT_REPORT_OUTPUT to mapOf(
        "builder" to BUILDER_JS,
        "html" to MEMORY_REPORT,
        "data" to DATA_JS
    ),
)