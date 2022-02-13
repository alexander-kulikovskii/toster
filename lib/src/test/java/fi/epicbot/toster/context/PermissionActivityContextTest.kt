package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import io.kotest.core.spec.style.BehaviorSpec

private class PermissionActivityContextData(
    val name: String,
    val action: PermissionActivityContext.() -> Unit,
    val expectedGranted: List<String> = emptyList(),
    val expectedRevoked: List<String> = emptyList(),
)

private const val GRANTED = "perm1"
private const val REVOKED = "perm2"

private val permissionActivityContextList = listOf(
    PermissionActivityContextData(
        name = "All empty",
        action = {},
    ),
    PermissionActivityContextData(
        name = "Grand one",
        action = {
            grant(GRANTED)
        },
        expectedGranted = listOf(GRANTED),
    ),
    PermissionActivityContextData(
        name = "Revoke one",
        action = {
            revoke(REVOKED)
        },
        expectedRevoked = listOf(REVOKED),
    ),
    PermissionActivityContextData(
        name = "all",
        action = {
            revoke(REVOKED)
            grant(GRANTED)
        },
        expectedGranted = listOf(GRANTED),
        expectedRevoked = listOf(REVOKED),
    ),
)

internal class PermissionActivityContextTest : BehaviorSpec({
    permissionActivityContextList.forEach { permissionActivityData ->
        Given("check ${permissionActivityData.name}") {
            val permissionActivityContext = PermissionActivityContext()
            When("Invoke action") {
                permissionActivityData.action.invoke(permissionActivityContext)
                val actualGranted =
                    permissionActivityContext.granted.joinToString()
                val actualRevoked = permissionActivityContext.revoked.joinToString()
                val expectedGranted =
                    permissionActivityData.expectedGranted.joinToString()
                val expectedRevoked = permissionActivityData.expectedRevoked.joinToString()

                Then("Granted should be $expectedGranted", expectedGranted, actualGranted)
                Then("Revoked should be $expectedRevoked", expectedRevoked, actualRevoked)
            }
        }
    }
})
