package fi.epicbot.toster.samples

import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.model.Overdraw

class SampleOverdrawTest : TosterTest(
    Config {
        runShellBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("Samples")
        applicationPackageName("fi.epicbot.toster.samples")
        apkUrl("build/outputs/apk/debug/samples-debug.apk")
        report {
            enable(true)
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