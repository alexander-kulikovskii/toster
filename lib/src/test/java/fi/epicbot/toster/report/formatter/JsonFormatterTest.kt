package fi.epicbot.toster.report.formatter

import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.Memory
import fi.epicbot.toster.report.model.MemoryCell
import fi.epicbot.toster.report.model.ReportAppInfo
import fi.epicbot.toster.report.model.ReportCollage
import fi.epicbot.toster.report.model.ReportDevice
import fi.epicbot.toster.report.model.ReportOutput
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.report.model.Screenshot
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class JsonFormatterTest : BehaviorSpec({
    testDataList.forEach { testData ->
        Given("Json formatter with prettyPrintJson = ${testData.prettyPrintJson}") {
            val reporter = JsonFormatter(prettyPrintJson = testData.prettyPrintJson)
            When("make report") {
                val actualAnswer = reporter.format(testData.reportOutput)
                Then("format result should be ${testData.expectedAnswer}") {
                    actualAnswer shouldBe testData.expectedAnswer
                }
            }
        }
    }
})

private val reportShort = ReportOutput(
    ReportAppInfo(appName = "Test", testTime = 0L),
    devices = mutableListOf()
)

private val reportFull = ReportOutput(
    ReportAppInfo(appName = "Test", testTime = 10L),
    devices = mutableListOf(
        ReportDevice(
            deviceName = "device",
            reportScreens = listOf(
                ReportScreen(
                    name = "screen",
                    common = mutableListOf(
                        Common(index = 0, name = "Before", startTime = 0L, endTime = 1L)
                    ),
                    memory = mutableListOf(
                        Memory(
                            index = 1,
                            name = "Memory",
                            startTime = 2L,
                            endTime = 3L,
                            measurements = mapOf(
                                "total" to MemoryCell(1L, 2L, 3L, 4L)
                            )
                        ),
                        Memory(
                            index = 4,
                            name = "Memory",
                            startTime = 8L,
                            endTime = 9L,
                            measurements = mapOf(
                                "total" to MemoryCell(4L, 0L, 1L, 9L),
                                "heap" to MemoryCell(0L, 0L, 0L, 0L)
                            )
                        )
                    ),
                    screenshots = mutableListOf(
                        Screenshot(
                            index = 2,
                            name = "Screenshot",
                            startTime = 4L,
                            endTime = 5L,
                            prefix = "normal",
                            pathUrl = "image.png"
                        ),
                        Screenshot(
                            index = 3,
                            name = "Screenshot",
                            startTime = 6L,
                            endTime = 7L,
                            prefix = "",
                            pathUrl = ""
                        )
                    )
                )
            ),
            collage = ReportCollage()
        ),
        ReportDevice(
            deviceName = "device 2",
            reportScreens = listOf(
                ReportScreen(
                    name = "screen 2",
                    common = mutableListOf(
                        Common(index = 1, name = "Before", startTime = 24L, endTime = 100L)
                    )
                )
            ),
            collage = ReportCollage()
        )
    )
)

private val REPORT_OUTPUT_FLAT =
    """{"appInfo":{"appName":"Test","testTime":0},"devices":[]}""".trimIndent()

private val REPORT_OUTPUT_PRETTY = """
{
    "appInfo": {
        "appName": "Test",
        "testTime": 0
    },
    "devices": [
    ]
}
""".trimIndent()

private val REPORT_FULL_PRETTY = """
{
    "appInfo": {
        "appName": "Test",
        "testTime": 10
    },
    "devices": [
        {
            "deviceName": "device",
            "reportScreens": [
                {
                    "name": "screen",
                    "common": [
                        {
                            "index": 0,
                            "name": "Before",
                            "startTime": 0,
                            "endTime": 1
                        }
                    ],
                    "gfxInfo": [
                    ],
                    "memory": [
                        {
                            "index": 1,
                            "name": "Memory",
                            "startTime": 2,
                            "endTime": 3,
                            "measurements": {
                                "total": {
                                    "memory": 1,
                                    "heapSize": 2,
                                    "heapAlloc": 3,
                                    "heapFree": 4
                                }
                            }
                        },
                        {
                            "index": 4,
                            "name": "Memory",
                            "startTime": 8,
                            "endTime": 9,
                            "measurements": {
                                "total": {
                                    "memory": 4,
                                    "heapSize": 0,
                                    "heapAlloc": 1,
                                    "heapFree": 9
                                },
                                "heap": {
                                    "memory": 0,
                                    "heapSize": 0,
                                    "heapAlloc": 0,
                                    "heapFree": 0
                                }
                            }
                        }
                    ],
                    "screenshots": [
                        {
                            "index": 2,
                            "name": "Screenshot",
                            "startTime": 4,
                            "endTime": 5,
                            "prefix": "normal",
                            "pathUrl": "image.png"
                        },
                        {
                            "index": 3,
                            "name": "Screenshot",
                            "startTime": 6,
                            "endTime": 7,
                            "prefix": "",
                            "pathUrl": ""
                        }
                    ]
                }
            ],
            "collage": {
            }
        },
        {
            "deviceName": "device 2",
            "reportScreens": [
                {
                    "name": "screen 2",
                    "common": [
                        {
                            "index": 1,
                            "name": "Before",
                            "startTime": 24,
                            "endTime": 100
                        }
                    ],
                    "gfxInfo": [
                    ],
                    "memory": [
                    ],
                    "screenshots": [
                    ]
                }
            ],
            "collage": {
            }
        }
    ]
}
""".trimIndent()

private val testDataList = listOf(
    TestData(true, reportShort, REPORT_OUTPUT_PRETTY),
    TestData(false, reportShort, REPORT_OUTPUT_FLAT),
    TestData(true, reportFull, REPORT_FULL_PRETTY),
)

private class TestData(
    val prettyPrintJson: Boolean,
    val reportOutput: ReportOutput,
    val expectedAnswer: String,
)
