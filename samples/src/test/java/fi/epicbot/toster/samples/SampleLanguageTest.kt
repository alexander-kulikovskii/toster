package fi.epicbot.toster.samples

import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens

class SampleLanguageTest : TosterTest(
    Config {
        runShellBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("Samples")
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
