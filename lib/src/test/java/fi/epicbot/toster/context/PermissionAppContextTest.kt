package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import io.kotest.core.spec.style.BehaviorSpec

private class PermissionAppContextData(
    val name: String,
    val action: PermissionAppContext.() -> Unit,
    val expectedGranted: List<String> = emptyList(),
)

private const val GRANTED = "perm1"

private val permissionAppContextList = listOf(
    PermissionAppContextData(
        name = "All empty",
        action = {},
    ),
    PermissionAppContextData(
        name = "Grand one",
        action = {
            grant(GRANTED)
        },
        expectedGranted = listOf(GRANTED),
    ),
)

internal class PermissionAppContextTest : BehaviorSpec({
    permissionAppContextList.forEach { permissionAppData ->
        Given("check ${permissionAppData.name}") {
            val permissionAppContext = PermissionAppContext()
            When("Invoke action") {
                permissionAppData.action.invoke(permissionAppContext)
                val actualGranted =
                    permissionAppContext.granted.joinToString()
                val expectedGranted =
                    permissionAppData.expectedGranted.joinToString()

                Then("Granted should be $expectedGranted", expectedGranted, actualGranted)
            }
        }
    }
})
