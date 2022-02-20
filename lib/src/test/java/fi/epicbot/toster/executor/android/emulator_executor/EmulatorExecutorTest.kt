package fi.epicbot.toster.executor.android.emulator_executor

import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.Verify
import fi.epicbot.toster.executor.android.android_executor.MockedFacade
import fi.epicbot.toster.executor.android.android_executor.SERIAL_NAME
import fi.epicbot.toster.executor.android.android_executor.provideEmulatorExecutor
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class EmulatorExecutorTest : BehaviorSpec({
    Given("Emulator Executor. Check executor") {
        val facade = MockedFacade()
        val emulatorExecutor = provideEmulatorExecutor(facade)
        When("Get executor") {
            val actualExecutor = emulatorExecutor.executor()
            Then("check type") {
                actualExecutor.type shouldBe "Emulator"
            }
            Then("check name") {
                actualExecutor.name shouldBe SERIAL_NAME
            }
        }
    }

    Given("Emulator Executor. Check prepareEnvironment") {
        val facade = MockedFacade()
        val emulatorExecutor = provideEmulatorExecutor(facade)
        When("Execute prepare environment") {
            emulatorExecutor.prepareEnvironment()
            Verify("check start emulator", exactly = 1) {
                facade.shell("path/emulator -avd $SERIAL_NAME -port 5554 & adb wait-for-device")
            }
            CoVerify("check delay", exactly = 1) {
                facade.shellExecutor.delay(2000L)
            }
        }
    }

    Given("Emulator Executor. Check startDelay") {
        val facade = MockedFacade()
        val emulatorExecutor = provideEmulatorExecutor(facade, 3000L)
        When("Execute prepare environment") {
            emulatorExecutor.prepareEnvironment()
            CoVerify("check delay", exactly = 1) {
                facade.shellExecutor.delay(3000L)
            }
        }
    }

    Given("Emulator Executor. Check finishEnvironment") {
        val facade = MockedFacade()
        val emulatorExecutor = provideEmulatorExecutor(facade)
        When("Execute finish environment") {
            emulatorExecutor.finishEnvironment()
            Verify("check start emulator", exactly = 1) {
                facade.shell("adb -s emulator-5554 emu kill & wait")
            }
        }
    }
})
