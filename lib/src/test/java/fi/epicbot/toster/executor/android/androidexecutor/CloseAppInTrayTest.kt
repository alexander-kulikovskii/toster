package fi.epicbot.toster.executor.android.androidexecutor

import fi.epicbot.toster.Then
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private const val CLOSE_APPS_IN_TRAY_TITLE = "Close apps in tray"
private val APPS_IN_TRAY = listOf(
    "fi.epicbot.toster.samples",
    "com.google.android.apps.maps",
)
private const val DUMSYS_COMMAND =
    "dumpsys window a | grep \"/\" | cut -d \"{\" -f2 | cut -d \"/\" -f1 | cut -d \" \" -f2"

class CloseAppInTrayTest : BehaviorSpec({

    Given("AndroidExecutor") {
        val facade = MockedFacade()
        every {
            facade.adbShell(DUMSYS_COMMAND)
        }.returns(APPS_IN_TRAY.joinToString("\n"))

        val androidExecutor = provideAndroidExecutor(facade).apply {
            imagePrefix = IMAGE_PREFIX
        }

        When("Execute action CloseAppsInTray") {
            val res = androidExecutor.execute(Action.CloseAppsInTray)
            Then("Name should be $CLOSE_APPS_IN_TRAY_TITLE", res.name, CLOSE_APPS_IN_TRAY_TITLE)
            Verify("check shell") {
                facade.adbShell(DUMSYS_COMMAND)
            }
            APPS_IN_TRAY.forEach {
                Verify("check force-stop $it") {
                    facade.adbShell("am force-stop $it")
                }
            }
        }
    }
})
