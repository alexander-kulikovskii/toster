package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import fi.epicbot.toster.model.Screen
import io.kotest.core.spec.style.BehaviorSpec

private class ScreensContextData(
    val name: String,
    val action: ScreensContext.() -> Unit,
    val expectedScreens: MutableList<Screen>,
)

private val screensList = listOf(
    ScreensContextData(
        "Empty list",
        {},
        mutableListOf(),
    ),
    ScreensContextData(
        "Add one screen",
        {
            screen {
                name("test")
            }
        },
        mutableListOf(Screen(name = "test")),
    ),
    ScreensContextData(
        "Add two screens",
        {
            screen {
                name("test")
            }
            screen {
                name("test 2")
            }
        },
        mutableListOf(Screen(name = "test"), Screen(name = "test 2")),
    )
)

internal class ScreensContextTest : BehaviorSpec({
    screensList.forEach { screensData ->
        Given("check ${screensData.name}") {
            val screensContext = ScreensContext()
            When("Invoke action") {
                screensData.action.invoke(screensContext)
                val actual = screensContext.screens.joinToString { it.name }
                val expected = screensData.expectedScreens.joinToString { it.name }
                Then("screens should be $expected", actual, expected)
            }
        }
    }
})
