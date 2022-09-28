package fi.epicbot.toster.samples.ci

import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.TosterTest

class SampleParamsTest : TosterTest(
    Config {
        runShellsBeforeAllScreens("../gradlew :samples:assembleDebug")
        applicationName("SampleParams")
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
            name("Set Params")
            url("fi.epicbot.toster.samples.SampleParamsActivity")
            activityParams {
                integer("int_value", 42)
            }
            actions {
                delay(1000L)
                takeMemoryAllocation()
                takeScreenshot()
            }
        }
    }
)
