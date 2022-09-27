package fi.epicbot.toster.samples

import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.TosterTest

class SampleLanguageTest : TosterTest(
    Config {
        runShellsBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("SampleLanguage")
        applicationPackageName("fi.epicbot.toster.samples")
        apk {
            url("build/outputs/apk/debug/samples-debug.apk")
        }
        report {
        }

        devices {
            phone("UUID") // get from `adb devices`
        }
    },
    Screens {
        screen {
            name("Default locale")
            url("fi.epicbot.toster.samples.SampleLanguageActivity")
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
        screen {
            name("Finnish locale")
            url("fi.epicbot.toster.samples.SampleLanguageActivity")
            activityParams {
                string("locale", "fi")
            }
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
        screen {
            name("Russian locale")
            url("fi.epicbot.toster.samples.SampleLanguageActivity")
            activityParams {
                string("locale", "ru")
            }
            actions {
                delay(1000L)
                takeScreenshot()
            }
        }
    }
)
