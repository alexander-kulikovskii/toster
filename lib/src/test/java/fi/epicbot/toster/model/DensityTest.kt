package fi.epicbot.toster.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

private val densityData = mapOf(
    Density.LDPI to 120,
    Density.MDPI to 160,
    Density.HDPI to 240,
    Density.XHDPI to 320,
    Density.XXHDPI to 480,
    Density.XXXHDPI to 640,
    Density.TVDPI to 213,
    Density.CUSTOM(42) to 42,
)

class DensityTest : BehaviorSpec({
    densityData.forEach { (density, expectedDpi) ->
        Given("Density with dpi ${density.dpi}") {
            When("Get dpi") {
                val actualDpi = density.dpi
                Then("it should be $expectedDpi") {
                    actualDpi shouldBe expectedDpi
                }
            }
        }
    }
})
