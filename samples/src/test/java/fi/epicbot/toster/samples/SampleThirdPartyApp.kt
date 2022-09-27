package fi.epicbot.toster.samples

import fi.epicbot.toster.Config
import fi.epicbot.toster.Screens
import fi.epicbot.toster.TosterTest
import fi.epicbot.toster.model.SwipeMove

class SampleThirdPartyApp : TosterTest(
    Config {
        applicationName("SampleThirdPartyApp")
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
            name("Main screen") // Assumes that you are already logged in
            url("com.instagram.android.activity.MainTabActivity")
            actions {
                repeat(10) {
                    swipe(SwipeMove.BottomToTop, delayMillis = 500)
                    takeMemoryAllocation()
                }
            }
        }
    }
)
