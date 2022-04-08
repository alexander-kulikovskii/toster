package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.ActivityParam
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.ScreenSize
import fi.epicbot.toster.model.title
import fi.epicbot.toster.model.toStringParams
import io.kotest.core.spec.style.BehaviorSpec

private class ScreenContextData(
    val name: String,
    val action: ScreenContext.() -> Unit,
    val expectedScreen: Screen,
)

private val screenList = listOf(
    ScreenContextData(
        "Default screen",
        {
        },
        Screen(
            name = "",
            url = "",
            shortUrl = "",
            delayAfterOpenMillis = 2000L,
            fontScale = null,
            activityParams = mutableListOf(),
            actions = mutableListOf(),
            clearDataBeforeRun = false,
            shellsBefore = emptyArray(),
            shellsAfter = emptyArray(),
            permissions = Permissions(),
            screenshotAsLastAction = true,
            resetGfxInfoBeforeStart = false,
            closeAppsInTrayBeforeStart = false,
            screenDensity = null,
            screenSize = null,
        )
    ),
    ScreenContextData(
        "Set name",
        {
            name("test")
        },
        Screen(
            name = "test",
        )
    ),
    ScreenContextData(
        "set short url",
        {
            shortUrl("short url")
        },
        Screen(
            shortUrl = "short url",
        )
    ),
    ScreenContextData(
        "set url",
        {
            url("url")
        },
        Screen(
            url = "url",
        )
    ),
    ScreenContextData(
        "set delayAfterOpenMillis",
        {
            delayAfterOpenMillis(42L)
        },
        Screen(
            delayAfterOpenMillis = 42L,
        )
    ),
    ScreenContextData(
        "set activityParams",
        {
            activityParams {
                boolean("key", true)
                integer("int", 2048)
            }
        },
        Screen(
            activityParams = mutableListOf(
                ActivityParam.BooleanActivityParam("key", true),
                ActivityParam.IntegerActivityParam("int", 2048),
            )
        )
    ),
    ScreenContextData(
        "set permissions",
        {
            permissions {
                grant("perm")
                revoke("perm 2")
            }
        },
        Screen(
            permissions = Permissions(
                granted = listOf("perm"),
                revoked = listOf("perm 2"),
            ),
        )
    ),
    ScreenContextData(
        "set actions",
        {
            actions {
                takeGfxInfo()
            }
        },
        Screen(
            actions = mutableListOf(Action.TakeGfxInfo),
        )
    ),
    ScreenContextData(
        "set runShellBefore",
        {
            runShellsBefore("shell")
        },
        Screen(
            shellsBefore = arrayOf("shell")
        )
    ),
    ScreenContextData(
        "set runShellAfter",
        {
            runShellsAfter("shell")
        },
        Screen(
            shellsAfter = arrayOf("shell")
        )
    ),
    ScreenContextData(
        "set clearDataBeforeRun",
        {
            clearDataBeforeRun()
        },
        Screen(
            clearDataBeforeRun = true
        )
    ),
    ScreenContextData(
        "set fontScale",
        {
            fontScale(scale = FontScale.SMALL)
        },
        Screen(
            fontScale = FontScale.SMALL
        )
    ),
    ScreenContextData(
        "set disableScreenshotAsLastAction",
        {
            disableScreenshotAsLastAction()
        },
        Screen(
            screenshotAsLastAction = false,
        )
    ),
    ScreenContextData(
        "set resetGfxInfoBeforeStart",
        {
            resetGfxInfoBeforeStart()
        },
        Screen(
            resetGfxInfoBeforeStart = true,
        )
    ),
    ScreenContextData(
        "set closeAppsInTrayBeforeStart",
        {
            closeAppsInTrayBeforeStart()
        },
        Screen(
            closeAppsInTrayBeforeStart = true,
        )
    ),
    ScreenContextData(
        "set screenDensity",
        {
            setScreenDensity(Density.HDPI)
        },
        Screen(
            screenDensity = Density.HDPI,
        )
    ),
    ScreenContextData(
        "set screenSize",
        {
            setScreenSize(24, 42)
        },
        Screen(
            screenSize = ScreenSize(24, 42)
        )
    ),
)

internal class ScreenContextTest : BehaviorSpec({
    screenList.forEach { screenData ->
        Given("check ${screenData.name}") {
            val screenContext = ScreenContext()
            When("Invoke action") {
                screenData.action.invoke(screenContext)
                val actual = screenContext.screen
                val expected = screenData.expectedScreen
                Then("name should be ${expected.name}", actual.name, expected.name)
                Then("url should be ${expected.url}", actual.url, expected.url)
                Then("shortUrl should be ${expected.shortUrl}", actual.shortUrl, expected.shortUrl)
                Then(
                    "delayAfterOpenMillis should be ${expected.delayAfterOpenMillis}",
                    actual.delayAfterOpenMillis,
                    expected.delayAfterOpenMillis
                )
                Then(
                    "fontScale should be ${expected.fontScale}",
                    actual.fontScale,
                    expected.fontScale
                )
                Then(
                    "activityParams should be ${expected.activityParams.toStringParams()}",
                    actual.activityParams.toStringParams(),
                    expected.activityParams.toStringParams()
                )
                Then(
                    "actions should be ${expected.actions.joinToString { it.title() }}",
                    actual.actions.joinToString { it.title() },
                    expected.actions.joinToString { it.title() }
                )
                Then(
                    "clearDataBeforeRun should be ${expected.clearDataBeforeRun}",
                    actual.clearDataBeforeRun,
                    expected.clearDataBeforeRun,
                )
                Then(
                    "shellBefore should be ${expected.shellsBefore.joinToString()}",
                    actual.shellsBefore.joinToString(),
                    expected.shellsBefore.joinToString(),
                )
                Then(
                    "shellAfter should be ${expected.shellsAfter.joinToString()}",
                    actual.shellsAfter.joinToString(),
                    expected.shellsAfter.joinToString()
                )
                Then(
                    "permissions.revoked should be ${expected.permissions}",
                    actual.permissions.revoked.joinToString(),
                    expected.permissions.revoked.joinToString()
                )
                Then(
                    "permissions.granted should be ${expected.permissions}",
                    actual.permissions.granted.joinToString(),
                    expected.permissions.granted.joinToString()
                )
                Then(
                    "screenshotAsLastAction should be ${expected.screenshotAsLastAction}",
                    actual.screenshotAsLastAction,
                    expected.screenshotAsLastAction
                )
                Then(
                    "resetGfxInfoBeforeStart should be ${expected.resetGfxInfoBeforeStart}",
                    actual.resetGfxInfoBeforeStart,
                    expected.resetGfxInfoBeforeStart
                )
                Then(
                    "closeAppsInTrayBeforeStart should be ${expected.closeAppsInTrayBeforeStart}",
                    actual.closeAppsInTrayBeforeStart,
                    expected.closeAppsInTrayBeforeStart
                )
                Then(
                    "screenDensity should be ${expected.screenDensity}",
                    actual.screenDensity?.dpi ?: 0,
                    expected.screenDensity?.dpi ?: 0
                )
                Then(
                    "screenSize should be ${expected.screenSize}",
                    "${actual.screenSize?.width ?: 0}x${actual.screenSize?.height ?: 0}",
                    "${expected.screenSize?.width ?: 0}x${expected.screenSize?.height ?: 0}",
                )
            }
        }
    }
})
