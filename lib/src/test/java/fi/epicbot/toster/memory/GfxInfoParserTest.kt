package fi.epicbot.toster.memory

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

private val rawData = """
Applications Graphics Acceleration Info:
Uptime: 314436624 Realtime: 1105003132

** Graphics info for pid 31185 [com.instagram.android] **

Stats since: 314435469478872ns
Total frames rendered: 51
Janky frames: 9 (17.65%)
Janky frames (legacy): 13 (25.49%)
50th percentile: 7ms
90th percentile: 48ms
95th percentile: 109ms
99th percentile: 300ms
Number Missed Vsync: 7
Number High input latency: 83
Number Slow UI thread: 9
Number Slow bitmap uploads: 0
Number Slow issue draw commands: 2
Number Frame deadline missed: 9
Number Frame deadline missed (legacy): 9
HISTOGRAM: 5ms=6 6ms=14 7ms=6 8ms=5 9ms=3 10ms=3 11ms=0 12ms=2 13ms=0 14ms=0 15ms=0 16ms=0 17ms=1 18ms=0 19ms=0 20ms=0 21ms=1 22ms=1 23ms=0 24ms=0 25ms=0 26ms=0 27ms=0 28ms=0 29ms=0 30ms=0 31ms=0 32ms=0 34ms=0 36ms=0 38ms=0 40ms=1 42ms=1 44ms=0 46ms=0 48ms=2 53ms=0 57ms=0 61ms=1 65ms=1 69ms=0 73ms=0 77ms=0 81ms=0 85ms=0 89ms=0 93ms=0 97ms=0 101ms=0 105ms=0 109ms=1 113ms=1 117ms=0 121ms=0 125ms=0 129ms=0 133ms=0 150ms=0 200ms=0 250ms=0 300ms=1 350ms=0 400ms=0 450ms=0 500ms=0 550ms=0 600ms=0 650ms=0 700ms=0 750ms=0 800ms=0 850ms=0 900ms=0 950ms=0 1000ms=0 1050ms=0 1100ms=0 1150ms=0 1200ms=0 1250ms=0 1300ms=0 1350ms=0 1400ms=0 1450ms=0 1500ms=0 1550ms=0 1600ms=0 1650ms=0 1700ms=0 1750ms=0 1800ms=0 1850ms=0 1900ms=0 1950ms=0 2000ms=0 2050ms=0 2100ms=0 2150ms=0 2200ms=0 2250ms=0 2300ms=0 2350ms=0 2400ms=0 2450ms=0 2500ms=0 2550ms=0 2600ms=0 2650ms=0 2700ms=0 2750ms=0 2800ms=0 2850ms=0 2900ms=0 2950ms=0 3000ms=0 3050ms=0 3100ms=0 3150ms=0 3200ms=0 3250ms=0 3300ms=0 3350ms=0 3400ms=0 3450ms=0 3500ms=0 3550ms=0 3600ms=0 3650ms=0 3700ms=0 3750ms=0 3800ms=0 3850ms=0 3900ms=0 3950ms=0 4000ms=0 4050ms=0 4100ms=0 4150ms=0 4200ms=0 4250ms=0 4300ms=0 4350ms=0 4400ms=0 4450ms=0 4500ms=0 4550ms=0 4600ms=0 4650ms=0 4700ms=0 4750ms=0 4800ms=0 4850ms=0 4900ms=0 4950ms=0
50th gpu percentile: 5ms
90th gpu percentile: 7ms
95th gpu percentile: 8ms
99th gpu percentile: 8ms
GPU HISTOGRAM: 1ms=3 2ms=6 3ms=8 4ms=8 5ms=6 6ms=11 7ms=6 8ms=3 9ms=0 10ms=0 11ms=0 12ms=0 13ms=0 14ms=0 15ms=0 16ms=0 17ms=0 18ms=0 19ms=0 20ms=0 21ms=0 22ms=0 23ms=0 24ms=0 25ms=0 4950ms=0
Pipeline=Skia (OpenGL)
CPU Caches:
  Glyph Cache: 41.78 KB (1 entry)
  Glyph Count: 13 
Total CPU memory usage:
  42779 bytes, 41.78 KB (0.00 bytes is purgeable)
GPU Caches:
  Other:
    Other: 3.84 KB (1 entry)
    Buffer Object: 13.50 KB (2 entries)
  SW Path Mask:
    Texture: 31.38 KB (9 entries)
  Image:
    Texture: 2.31 MB (11 entries)
  Scratch:
    RenderTarget: 28.53 MB (7 entries)
    Texture: 2.00 MB (10 entries)
    Buffer Object: 78.00 KB (2 entries)
Layer Info:
    GlLayer size 1440x2354
  Layers Total         13241.25 KB (numLayers = 1)
Total GPU memory usage:
  34570008 bytes, 32.97 MB (30.83 MB is purgeable)

Profile data in ms:

        com.instagram.android/com.instagram.mainactivity.MainActivity/android.view.ViewRootImpl@e418670 (visibility=0)
        KHCD.1Pc.feed_timeline/android.view.ViewRootImpl@d871fa5 (visibility=0)
View hierarchy:

  com.instagram.android/com.instagram.mainactivity.MainActivity/android.view.ViewRootImpl@e418670
  413 views, 594,63 kB of render nodes

  KHCD.1Pc.feed_timeline/android.view.ViewRootImpl@d871fa5
  1 views, 1,23 kB of render nodes


Total ViewRootImpl   : 2
Total attached Views : 414
Total RenderNode     : 595,86 kB (used) / 1204,11 kB (capacity)
""".trimIndent()

class GfxInfoParserTest : BehaviorSpec({
    Given("GfxInfoParser") {
        val parser = GfxInfoParser()
        When("Parse empty gfx info") {
            val map = parser.parse("")
            Then("map size should be 0") {
                map.size shouldBe 0
            }
        }
        When("parse data") {
            val map = parser.parse(rawData)
            Then("map size should be 16") {
                map.size shouldBe 16
            }
            testMap.forEach { (title, expectedValue) ->
                Then("Check $title") {
                    map[title] shouldBe expectedValue
                }
            }
        }
    }
})

private val testMap = mapOf(
    "totalFramesRendered" to 51.0,
    "jankyFrames" to 9.0,
    "jankyFramesPercent" to 17.65,
    "jankyFramesLegacy" to 13.0,
    "jankyFramesLegacyPercent" to 25.49,
    "50thPercentileMs" to 7.0,
    "90thPercentileMs" to 48.0,
    "95thPercentileMs" to 109.0,
    "99thPercentileMs" to 300.0,
    "numberMissedVsync" to 7.0,
    "numberHighInputLatency" to 83.0,
    "numberSlowUiThread" to 9.0,
    "numberSlowBitmapUploads" to 0.0,
    "numberSlowIssueDrawCommands" to 2.0,
    "numberFrameDeadlineMissed" to 9.0,
    "numberFrameDeadlineMissedLegacy" to 9.0,
)
