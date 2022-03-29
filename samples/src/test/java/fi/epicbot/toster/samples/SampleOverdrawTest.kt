package fi.epicbot.toster.samples

import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.model.Overdraw

class SampleOverdrawTest : TosterTest(
    Config {
        runShellsBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("SampleOverdraw")
        applicationPackageName("fi.epicbot.toster.samples")
        apkUrl("build/outputs/apk/debug/samples-debug.apk")
        report {
        }

        devices {
            phone("UUID") // get from `adb devices`
        }
        checkOverdraw(Overdraw(check = true))
    },
    Screens {
        screen {
            name("Overdraw")
            shortUrl("SampleOverdrawActivity")
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
    }
)
