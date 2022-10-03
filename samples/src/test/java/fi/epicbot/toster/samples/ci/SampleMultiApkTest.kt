package fi.epicbot.toster.samples.ci

import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.TosterTest

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
        testTimeout(100_000_000L)
        devices {
            phone("UUID") // get from `adb devices`
        }
    },
    Screens {
        screen {
            name("Default locale")
            url("fi.epicbot.toster.samples.SampleLanguageActivity")
            actions {
                repeat(50) {
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
                repeat(40) {
                    takeCpuUsage()
                    takeMemoryAllocation()
                    delay(100L)
                }
                takeScreenshot()
            }
        }
    }
)
