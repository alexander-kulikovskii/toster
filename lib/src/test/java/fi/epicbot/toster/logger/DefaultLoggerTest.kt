package fi.epicbot.toster.logger

import fi.epicbot.toster.time.TimeProvider
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DefaultLoggerTest : BehaviorSpec({
    Given("Default logger with commands") {
        val timeProvider = mockk<TimeProvider>(relaxed = true)
        every { timeProvider.getTimeMillis() }.returnsMany(1L, 2L)
        val defaultLogger = DefaultLogger(timeProvider)

        defaultLogger.logCommand("adb devices")
        defaultLogger.logCommand("mkdir test/")

        When("Get all commands with timestamp") {
            val output = defaultLogger.getAllCommands(true)
            Then("Commands should be") {
                output shouldBe "1\tadb devices\n2\tmkdir test/"
            }
        }
        When("Get all commands without timestamp") {
            val output = defaultLogger.getAllCommands(false)
            Then("Commands should be") {
                output shouldBe "adb devices\nmkdir test/"
            }
        }
    }
})
