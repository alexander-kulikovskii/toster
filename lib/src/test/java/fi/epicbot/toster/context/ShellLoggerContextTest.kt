package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import fi.epicbot.toster.model.ShellLoggerConfig
import io.kotest.core.spec.style.BehaviorSpec

private class ShellLoggerContextData(
    val name: String,
    val action: ShellLoggerContext.() -> Unit,
    val expectedShellLogger: ShellLoggerConfig,
)

private val shellLoggerContextList = listOf(
    ShellLoggerContextData(
        name = "Default logger",
        action = {},
        expectedShellLogger = ShellLoggerConfig(enable = true, enableTimestamp = true)
    ),
    ShellLoggerContextData(
        name = "Disable shell logger",
        action = {
            disable()
        },
        expectedShellLogger = ShellLoggerConfig(enable = false, enableTimestamp = true)
    ),
    ShellLoggerContextData(
        name = "Disable timestamp",
        action = {
            disableTimestamp()
        },
        expectedShellLogger = ShellLoggerConfig(enable = true, enableTimestamp = false)
    ),
)

internal class ShellLoggerContextTest : BehaviorSpec({
    shellLoggerContextList.forEach { shellLoggerData ->
        Given("check ${shellLoggerData.name}") {
            val shellLoggerContext = ShellLoggerContext()
            When("Invoke action") {
                shellLoggerData.action.invoke(shellLoggerContext)
                val actualReport = shellLoggerContext.shellLogger
                val expectedReport = shellLoggerData.expectedShellLogger

                Then("Check enable", expectedReport.enable, actualReport.enable)
                Then("Check enable timestamp", expectedReport.enableTimestamp, actualReport.enableTimestamp)
            }
        }
    }
})
