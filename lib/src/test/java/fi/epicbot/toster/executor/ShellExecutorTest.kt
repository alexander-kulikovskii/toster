package fi.epicbot.toster.executor

import com.lordcodes.turtle.ShellLocation
import com.lordcodes.turtle.shellRun
import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.Verify
import fi.epicbot.toster.logger.ShellLogger
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.delay
import java.io.File

private const val HOME_PATH = "user/"
private const val CURRENT_WORKING_PATH = HOME_PATH + "toster"
private val CURRENT_WORKING = File(CURRENT_WORKING_PATH)
private val HOME = File(HOME_PATH)
private const val PROJECT_DIR = "/test"
private const val FULL_PATH = CURRENT_WORKING_PATH + PROJECT_DIR
private const val SCREEN_NAME = "/screen"
private const val SCREEN_SUB_NAME = "overdraw"
private const val DELAY_MS = 250L

class ShellExecutorTest : BehaviorSpec({
    timeout = 60000L

    Given("Shell executor") {
        val logger = mockShellAndProvideMockedLogger()

        When("init it") {
            val shellEx = ShellExecutor(PROJECT_DIR, logger)
            Then("output dir should be created") {
                shellEx.workingDir.toString() shouldBe FULL_PATH
            }
            Verify("check log sh") {
                logger.logCommand("/bin/sh -c rm -rf $FULL_PATH || true")
            }
            Verify("check shell command sh") {
                shellRun(
                    command = "/bin/sh",
                    arguments = listOf("-c", "rm -rf $FULL_PATH || true"),
                    workingDirectory = CURRENT_WORKING
                )
            }
            Verify("check log mkdir") {
                logger.logCommand("mkdir -p $FULL_PATH")
            }
            Verify("check shell command mkdir") {
                shellRun(
                    command = "mkdir",
                    arguments = listOf("-p", FULL_PATH),
                    workingDirectory = CURRENT_WORKING
                )
            }
        }
    }

    Given("Shell executor 2") {
        val logger = mockShellAndProvideMockedLogger()

        When("makeDir") {
            val shellEx = ShellExecutor(PROJECT_DIR, logger)
            shellEx.makeDir(SCREEN_NAME, clearBefore = false)

            Verify("check log") {
                logger.logCommand("mkdir -p $SCREEN_NAME")
            }
            Verify("check shell command") {
                shellRun(
                    command = "mkdir",
                    arguments = listOf("-p", SCREEN_NAME),
                    workingDirectory = CURRENT_WORKING
                )
            }
        }
    }

    Given("Shell executor 3") {
        val logger = mockShellAndProvideMockedLogger()

        When("makeDir work dir") {
            val shellEx = ShellExecutor(PROJECT_DIR, logger)
            shellEx.makeDirForScreen(SCREEN_NAME)

            Verify("check log") {
                logger.logCommand("mkdir -p $SCREEN_NAME")
            }
            Verify("check shell command") {
                shellRun(
                    command = "mkdir",
                    arguments = listOf("-p", SCREEN_NAME),
                    workingDirectory = File(FULL_PATH)
                )
            }
        }
    }

    Given("Shell executor 4") {
        val logger = mockShellAndProvideMockedLogger()

        When("run command for screen") {
            val shellEx = ShellExecutor(PROJECT_DIR, logger)
            shellEx.runCommandForScreen("command", "argument1 argument2")

            Verify("check log") {
                logger.logCommand("command argument1 argument2")
            }
            Verify("check shell command") {
                shellRun(
                    command = "command",
                    arguments = listOf("argument1", "argument2"),
                    workingDirectory = File(FULL_PATH)
                )
            }
        }
    }

    Given("Shell executor 5") {
        val logger = mockShellAndProvideMockedLogger()

        When("run shell command") {
            val shellEx = ShellExecutor(PROJECT_DIR, logger)
            shellEx.runShellCommand("command argument1 argument2", false)

            Verify("check log") {
                logger.logCommand("/bin/sh -c command argument1 argument2")
            }
            Verify("check shell command") {
                shellRun(
                    command = "/bin/sh",
                    arguments = listOf("-c", "command argument1 argument2"),
                    workingDirectory = File(FULL_PATH)
                )
            }
        }
    }

    Given("Shell executor 6") {
        val logger = mockShellAndProvideMockedLogger()

        When("setScreenDirAndMakeIt") {
            val shellEx = ShellExecutor(PROJECT_DIR, logger)
            shellEx.setScreenDirAndMakeIt(SCREEN_SUB_NAME)

            Verify("check log") {
                logger.logCommand("mkdir -p $FULL_PATH/$SCREEN_SUB_NAME")
            }
            Verify("check shell command") {
                shellRun(
                    command = "mkdir",
                    arguments = listOf("-p", "$FULL_PATH/$SCREEN_SUB_NAME"),
                    workingDirectory = CURRENT_WORKING
                )
            }
        }
    }

    Given("Shell executor 7") {
        val logger = mockShellAndProvideMockedLogger()

        When("set delay") {
            mockkStatic("kotlinx.coroutines.DelayKt")
            coJustRun { delay(DELAY_MS) }
            val shellEx = ShellExecutor(PROJECT_DIR, logger)
            shellEx.delay(DELAY_MS)

            CoVerify("check delay") {
                delay(DELAY_MS)
            }
        }
    }
})

private fun mockShellAndProvideMockedLogger(): ShellLogger {
    val logger = mockk<ShellLogger>(relaxed = true)
    mockkStatic("com.lordcodes.turtle.ShellKt")
    every { shellRun(command = any(), arguments = any(), workingDirectory = any()) } returns ""

    mockkObject(ShellLocation)
    every { ShellLocation.CURRENT_WORKING } returns CURRENT_WORKING
    every { ShellLocation.HOME } returns HOME
    return logger
}
