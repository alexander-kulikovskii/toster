package fi.epicbot.toster.samples

import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.model.SwipeMove

class SampleMeasurementsTest : TosterTest(
    Config {
        applicationName("SampleMeasurementsTest")
        applicationPackageName("com.instagram.android")
        doNotDeleteAndInstallApk()
        report {
        }
        devices {
            phone("UUID") // get from `adb devices`
        }
    },
    Screens {
        screen {
            name("Main screen 200 delay") // Assumes that you are already logged in
            url("com.instagram.android.activity.MainTabActivity")
            actions {
                repeat(20) {
                    swipe(SwipeMove.BottomToTop, delayMillis = 200)
                    takeCpuUsage()
                    takeMemoryAllocation()
                }
            }
        }
        screen {
            name("Main screen 500 delay") // Assumes that you are already logged in
            url("com.instagram.android.activity.MainTabActivity")
            actions {
                repeat(20) {
                    swipe(SwipeMove.BottomToTop, delayMillis = 500)
                    takeCpuUsage()
                    takeMemoryAllocation()
                }
            }
        }
    }
)
