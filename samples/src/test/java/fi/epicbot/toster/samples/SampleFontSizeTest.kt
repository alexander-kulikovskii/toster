package fi.epicbot.toster.samples

import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.model.FontScale

class SampleFontSizeTest : TosterTest(
    Config {
        runShellBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("SampleFontSize")
        applicationPackageName("fi.epicbot.toster.samples")
        apkUrl("build/outputs/apk/debug/samples-debug.apk")
        report {
        }

        devices {
            phone("UUID") // get from `adb devices`
        }
    },
    Screens {
        screen {
            name("Small size")
            url("fi.epicbot.toster.samples.SampleFontSizeActivity")
            fontScale(FontScale.SMALL)
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
        screen {
            name("Large size")
            url("fi.epicbot.toster.samples.SampleFontSizeActivity")
            fontScale(FontScale.LARGE)
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
    }
)
