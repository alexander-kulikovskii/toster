package fi.epicbot.toster.time

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class SkipCommandsTimeProviderTest : BehaviorSpec({
    Given("Skip Commands Time Provider with 0 start time") {
        val timeProvider = SkipCommandsTimeProvider(0L)
        When("Get time and change add offset") {
            val resultList = mutableListOf<Long>()
            resultList.add(timeProvider.getTimeMillis())
            resultList.add(timeProvider.getTimeMillis())
            timeProvider.addOffset(20L)
            resultList.add(timeProvider.getTimeMillis())
            timeProvider.addOffset(500L)
            resultList.add(timeProvider.getTimeMillis())
            Then("Final list should be") {
                resultList shouldBe mutableListOf(10L, 20L, 50L, 560L)
            }
        }
    }

    Given("Skip Commands Time Provider") {
        val timeProvider = SkipCommandsTimeProvider(1643837263502L)
        When("Get time and change add offset") {
            val resultList = mutableListOf<Long>()
            resultList.add(timeProvider.getTimeMillis())
            timeProvider.addOffset(20L)
            resultList.add(timeProvider.getTimeMillis())
            Then("Final list should be") {
                resultList shouldBe mutableListOf(1643837263512L, 1643837263542L)
            }
        }
    }
})
