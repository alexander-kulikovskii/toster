package fi.epicbot.toster.extension

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class StringExtensionTest : BehaviorSpec({
    testData.forEach() { (inputData, expectedValue) ->
        Given(inputData) {
            When("Get save string for path") {
                val actualValue = inputData.safeForPath()
                Then("It should be $expectedValue") {
                    actualValue shouldBe expectedValue
                }
            }
        }
    }
})

private val testData = mapOf(
    "test" to "test",
    " Test" to "Test",
    " test _" to "test__",
    "te@st" to "te_st",
    " test;.test" to "test__test",
    " test_test" to "test_test",
    " test !@#\$%^&*(()+ test =>< " to "test______________test____",
)
