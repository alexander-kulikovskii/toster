package fi.epicbot.toster.samples

import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.FontScale

class SampleDensityTest : TosterTest(
    Config {
        runShellsBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("SampleDensity")
        applicationPackageName("fi.epicbot.toster.samples")
        apkUrl("build/outputs/apk/debug/samples-debug.apk")
        setScreenDensity(Density.HDPI)
        report {
        }

        devices {
            phone("UUID") // get from `adb devices`
        }
    },
    Screens {
        screen {
            name("Default HDPI density")
            url("fi.epicbot.toster.samples.SampleFontSizeActivity")
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
        screen {
            name("LDPI density")
            url("fi.epicbot.toster.samples.SampleFontSizeActivity")
            setScreenDensity(Density.LDPI)
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
    }
)
