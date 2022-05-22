package fi.epicbot.toster.samples

import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens

class SampleMultiApkTest : TosterTest(
    Config {
        runShellsBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("SampleMultiApk")
        applicationPackageName("fi.epicbot.toster.samples")
        multiApk {
            apk {
                runShellsBefore("", "")
                url("build/outputs/apk/debug/samples-debug.apk")
                prefix("main")
            }
            apk {
                runShellsBefore("", "")
                url("build/outputs/apk/debug/samples-debug.apk")
                prefix("dev")
            }
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
                repeat(5) {
                    takeCpuUsage()
                    takeMemoryAllocation()
                    delay(100L)
                }
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
                repeat(8) {
                    takeCpuUsage()
                    takeMemoryAllocation()
                    delay(100L)
                }
                takeScreenshot()
            }
        }
    }
)
