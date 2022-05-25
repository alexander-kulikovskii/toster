package fi.epicbot.toster.executor.android.android_executor

import fi.epicbot.toster.Then
import fi.epicbot.toster.ThenInstanseOf
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.report.model.GfxInfo
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private const val GFX_INFO_TITLE = "Take gfxinfo"
private val GFX_INFO_MEASUREMENTS = mapOf("test" to 1.0)

class TakeGfxInfoTest : BehaviorSpec({

    Given("AndroidExecutor") {
        val facade = MockedFacade()
        every { facade.timeProvider.getTimeMillis() }.returnsMany(1L, 3L)
        every {
            facade.adbShell("dumpsys gfxinfo $PACKAGE_NAME")
        }.returns("raw data")
        every { facade.parserProvider.gfxInfoParser.parse("raw data") }.returns(GFX_INFO_MEASUREMENTS)
        val androidExecutor = provideAndroidExecutor(facade)

        When("Execute action TakeGfxInfo") {
            val res = androidExecutor.execute(Action.TakeGfxInfo, IMAGE_PREFIX)
            Then("Name should be $GFX_INFO_TITLE", res.name, GFX_INFO_TITLE)
            Verify("check shell") {
                facade.adbShell("dumpsys gfxinfo $PACKAGE_NAME")
            }
            Then("Index should be 0", res.index, 0)
            Then("startTime should be 1", res.startTime, 1L)
            Then("endTime should be 3", res.endTime, 3L)
            ThenInstanseOf<GfxInfo>("ReportAction should be GfxInfo", res)
            Then("Check Measurements", (res as GfxInfo).measurements, GFX_INFO_MEASUREMENTS)
        }
    }
})
