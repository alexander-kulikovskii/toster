package fi.epicbot.toster.parser

import fi.epicbot.toster.report.model.CpuCell
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CpuUsageParserTest : BehaviorSpec({
    Given("CpuUsageParser") {
        val parser = CpuUsageParser()
        testMap.forEach { (rawData, expectedAnswer) ->
            When("Parse raw data <$rawData>") {
                val actual = parser.parse(rawData, apkPackage = "fi.epicbot.test")
                Then("user should be (${expectedAnswer.user})") {
                    expectedAnswer.user shouldBe actual.user
                }
                Then("kernel should be (${expectedAnswer.kernel})") {
                    expectedAnswer.kernel shouldBe actual.kernel
                }
            }
        }
    }
})

private val testMap = mapOf(
    "" to CpuCell(0f, 0f),
    "0% 11195/fi.epicbot.test: 0% user + 0% kernel / faults: 29 minor" to CpuCell(0f, 0f),
    "0% 11195/fi.epicbot.test: 6.4% user + 0% kernel / faults: 29 minor" to CpuCell(6.4f, 0f),
    "0% 11195/fi.epicbot.test: 0.4% user + 10% kernel / faults: 14 minor" to CpuCell(0.4f, 10f),
    "0% 11195/fi.epicbot.test: 0.1% user + 99.9% kernel / faults: 17 minor" to CpuCell(0.1f, 99.9f),
    "0% 11195/fi.epicbot.test: 0.1% user +" to CpuCell(0f, 0f),
)
