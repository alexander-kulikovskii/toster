package fi.epicbot.toster.report.html

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

internal class ColorProviderTest : BehaviorSpec({
    Given("Reporter") {
        testData.forEach { testColor ->
            When("get color with index ${testColor.index} with transparent ${testColor.transparent}") {
                val actualResult = CpuUsageHtmlReporter().getColorByIndex(
                    testColor.index,
                    testColor.transparent
                )
                Then("Color should be ${testColor.expected}") {
                    actualResult shouldBe testColor.expected
                }
            }
        }
    }
})

private val testData = listOf(
    TestColorClass(0, "rgb(255, 95, 95)"),
    TestColorClass(0, "rgba(255, 95, 95, 0.8)", transparent = true),
    TestColorClass(1, "rgb(255, 177, 86)"),
    TestColorClass(2, "rgb(103, 237, 116)"),
    TestColorClass(3, "rgb(19, 211, 200)"),
    TestColorClass(4, "rgb(90, 117, 255)"),
    TestColorClass(5, "rgb(219, 56, 203)"),
    TestColorClass(6, "rgb(143, 71, 71)"),
    TestColorClass(7, "rgb(182, 144, 101)"),
    TestColorClass(8, "rgb(35, 189, 97)"),
    TestColorClass(9, "rgb(122, 174, 171)"),
    TestColorClass(10, "rgb(118, 124, 160)"),
    TestColorClass(11, "rgb(136, 87, 131)"),
    TestColorClass(12, "rgb(255, 168, 168)"),
    TestColorClass(12, "rgb(255, 168, 168)"),
    TestColorClass(13, "rgb(255, 199, 0)"),
    TestColorClass(14, "rgb(82, 143, 88)"),
    TestColorClass(15, "rgb(58, 143, 138)"),
    TestColorClass(16, "rgb(127, 38, 216)"),
    TestColorClass(17, "rgb(184, 32, 114)"),
    TestColorClass(18, "rgb(255, 95, 95)"),
)

private class TestColorClass(
    val index: Int,
    val expected: String,
    val transparent: Boolean = false,
)
