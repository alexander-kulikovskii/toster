package fi.epicbot.toster.extension

import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.title
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.ReportScreen
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

private open class ApplyDensityTestSpec(
    density: Density?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    executeCondition: Boolean?,
) :
    DescribeSpec({
        describe(density?.dpi.toString()) {
            if (executeCondition == null) {
                density.apply(
                    actionExecutor,
                    reportScreen,
                )
            } else {
                density.apply(
                    actionExecutor,
                    reportScreen,
                    executeCondition,
                )
            }
        }
    })

private open class ResetDensityTestSpec(
    density: Density?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen
) :
    DescribeSpec({
        describe(density?.dpi.toString()) {
            density.reset(
                actionExecutor,
                reportScreen,
            )
        }
    })

internal class ScreenDensityExtensionTest : BehaviorSpec({

    val collector = CollectingTestEngineListener()

    applyDensityTestData.forEach { densityTest ->
        Given("Apply density ${densityTest.density}") {
            val projectConfiguration = ProjectConfiguration()
            val reportScreen = ReportScreen("test report")
            val action = densityTest.density?.let {
                Action.SetScreenDensity(densityTest.density)
            }
            val actionExecutor = mockk<ActionExecutor>(relaxed = true, relaxUnitFun = true)
            coEvery { actionExecutor.execute(any()) } returns Common(1, action?.title() ?: "", 2, 4)

            When("execute action") {
                val ref = SpecRef.Function({
                    ApplyDensityTestSpec(
                        densityTest.density,
                        actionExecutor,
                        reportScreen,
                        densityTest.executeCondition
                    )
                }, ApplyDensityTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()

                Then("test name should be ${densityTest.density?.dpi}") {
                    collector.result(densityTest.density?.dpi.toString())?.isSuccess shouldBe true
                }
                CoVerify("check action executor", exactly = densityTest.commonSize) {
                    actionExecutor.execute(ofType(Action.SetScreenDensity::class))
                }
                Then("common list size should be ${densityTest.commonSize}") {
                    reportScreen.common.size shouldBe densityTest.commonSize
                }
                if (densityTest.commonSize > 0) {
                    Then("common item name should be ${densityTest.commonNameApply}") {
                        reportScreen.common[0].name shouldBe densityTest.commonNameApply
                    }
                }
            }
        }
    }

    resetDensityTestData.forEach { densityTest ->
        Given("Reset density ${densityTest.density}") {
            val projectConfiguration = ProjectConfiguration()
            val reportScreen = ReportScreen("test report")
            val action = densityTest.density?.let {
                Action.ResetScreenDensity
            }
            val actionExecutor = mockk<ActionExecutor>(relaxed = true, relaxUnitFun = true)
            coEvery { actionExecutor.execute(any()) } returns Common(1, action?.title() ?: "", 2, 4)

            When("execute action") {
                val ref = SpecRef.Function({
                    ResetDensityTestSpec(
                        densityTest.density,
                        actionExecutor,
                        reportScreen,
                    )
                }, ResetDensityTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()

                Then("test name should be ${densityTest.density?.dpi}") {
                    collector.result(densityTest.density?.dpi.toString())?.isSuccess shouldBe true
                }
                val expectedCommonSize = if (densityTest.density == null) 0 else 1
                CoVerify("check action executor", exactly = expectedCommonSize) {
                    actionExecutor.execute(ofType(Action.ResetScreenDensity::class))
                }
                Then("common list size should be $expectedCommonSize") {
                    reportScreen.common.size shouldBe expectedCommonSize
                }
                if (densityTest.density != null) {
                    Then("common item name should be ${densityTest.commonNameReset}") {
                        reportScreen.common[0].name shouldBe densityTest.commonNameReset
                    }
                }
            }
        }
    }
})

private class DensityTestModel(
    val density: Density?,
    val executeCondition: Boolean?,
    val commonSize: Int,
    val commonNameApply: String?,
    val commonNameReset: String = "",
)

private val applyDensityTestData = listOf(
    DensityTestModel(
        Density.LDPI,
        true,
        1,
        "Set screen density <120>",
    ),
    DensityTestModel(
        Density.MDPI,
        true,
        1,
        "Set screen density <160>",
    ),
    DensityTestModel(
        Density.HDPI,
        true,
        1,
        "Set screen density <240>",
    ),
    DensityTestModel(
        Density.XHDPI,
        true,
        1,
        "Set screen density <320>",
    ),

    DensityTestModel(
        Density.XXHDPI,
        true,
        1,
        "Set screen density <480>",
    ),
    DensityTestModel(
        Density.XXXHDPI,
        true,
        1,
        "Set screen density <640>",
    ),
    DensityTestModel(
        Density.TVDPI,
        true,
        1,
        "Set screen density <213>",
    ),
    DensityTestModel(
        Density.CUSTOM(42),
        true,
        1,
        "Set screen density <42>",
    ),

    DensityTestModel(
        Density.LDPI,
        false,
        0,
        "",
    ),
    DensityTestModel(
        null,
        false,
        0,
        "",
    ),
    DensityTestModel(
        Density.XHDPI,
        null,
        1,
        "Set screen density <320>",
    ),
)

private val resetDensityTestData = listOf(
    DensityTestModel(
        Density.HDPI,
        true,
        1,
        "",
        "Reset screen density"
    ),
    DensityTestModel(
        null,
        true,
        0,
        "",
        "Reset screen density"
    ),
)
