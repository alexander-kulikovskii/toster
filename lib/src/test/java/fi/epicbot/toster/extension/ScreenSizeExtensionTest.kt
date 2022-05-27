package fi.epicbot.toster.extension

import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.ScreenSize
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

private open class ApplySizeTestSpec(
    size: ScreenSize?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    executeCondition: Boolean?,
) :
    DescribeSpec({
        describe("${size?.width} ${size?.height}") {
            if (executeCondition == null) {
                size.apply(
                    actionExecutor,
                    reportScreen,
                )
            } else {
                size.apply(
                    actionExecutor,
                    reportScreen,
                    executeCondition,
                )
            }
        }
    })

private open class ResetSizeTestSpec(
    size: ScreenSize?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen
) :
    DescribeSpec({
        describe("${size?.width} ${size?.height}") {
            size.reset(
                actionExecutor,
                reportScreen,
            )
        }
    })

internal class ScreenSizeExtensionTest : BehaviorSpec({

    val collector = CollectingTestEngineListener()

    applySizeTestData.forEach { sizeTest ->
        Given("Apply size ${sizeTest.size}") {
            val projectConfiguration = ProjectConfiguration()
            val reportScreen = ReportScreen("test report")
            val action = sizeTest.size?.let {
                Action.SetScreenSize(sizeTest.size)
            }
            val actionExecutor = mockk<ActionExecutor>(relaxed = true, relaxUnitFun = true)
            coEvery { actionExecutor.execute(any()) } returns Common(1, action?.title() ?: "", 2, 4)

            When("execute action") {
                val ref = SpecRef.Function({
                    ApplySizeTestSpec(
                        sizeTest.size,
                        actionExecutor,
                        reportScreen,
                        sizeTest.executeCondition
                    )
                }, ApplySizeTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()

                Then("test name should be ${sizeTest.size?.width}") {
                    collector.result("${sizeTest.size?.width} ${sizeTest.size?.height}")?.isSuccess shouldBe true
                }
                CoVerify("check action executor", exactly = sizeTest.commonSize) {
                    actionExecutor.execute(ofType(Action.SetScreenSize::class))
                }
                Then("common list size should be ${sizeTest.commonSize}") {
                    reportScreen.common.size shouldBe sizeTest.commonSize
                }
                if (sizeTest.commonSize > 0) {
                    Then("common item name should be ${sizeTest.commonNameApply}") {
                        reportScreen.common[0].name shouldBe sizeTest.commonNameApply
                    }
                }
            }
        }
    }

    resetSizeTestData.forEach { sizeTest ->
        Given("Reset density ${sizeTest.size}") {
            val projectConfiguration = ProjectConfiguration()
            val reportScreen = ReportScreen("test report")
            val action = sizeTest.size?.let {
                Action.ResetScreenDensity
            }
            val actionExecutor = mockk<ActionExecutor>(relaxed = true, relaxUnitFun = true)
            coEvery { actionExecutor.execute(any()) } returns Common(1, action?.title() ?: "", 2, 4)

            When("execute action") {
                val ref = SpecRef.Function({
                    ResetSizeTestSpec(
                        sizeTest.size,
                        actionExecutor,
                        reportScreen,
                    )
                }, ResetSizeTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()

                Then("test name should be ${sizeTest.size?.width}") {
                    collector.result("${sizeTest.size?.width} ${sizeTest.size?.height}")?.isSuccess shouldBe true
                }
                val expectedCommonSize = if (sizeTest.size == null) 0 else 1
                CoVerify("check action executor", exactly = expectedCommonSize) {
                    actionExecutor.execute(ofType(Action.ResetScreenSize::class))
                }
                Then("common list size should be $expectedCommonSize") {
                    reportScreen.common.size shouldBe expectedCommonSize
                }
                if (sizeTest.size != null) {
                    Then("common item name should be ${sizeTest.commonNameReset}") {
                        reportScreen.common[0].name shouldBe sizeTest.commonNameReset
                    }
                }
            }
        }
    }
})

private class SizeTestModel(
    val size: ScreenSize?,
    val executeCondition: Boolean?,
    val commonSize: Int,
    val commonNameApply: String?,
    val commonNameReset: String = "",
)

private val applySizeTestData = listOf(
    SizeTestModel(
        ScreenSize(1, 2),
        true,
        1,
        "Set screen size <1x2>",
    ),
    SizeTestModel(
        ScreenSize(3, 5),
        false,
        0,
        "",
    ),
    SizeTestModel(
        null,
        false,
        0,
        "",
    ),
    SizeTestModel(
        ScreenSize(42, 2),
        null,
        1,
        "Set screen size <42x2>",
    ),
)

private val resetSizeTestData = listOf(
    SizeTestModel(
        ScreenSize(44, 2),
        true,
        1,
        "",
        "Reset screen density"
    ),
    SizeTestModel(
        ScreenSize(53, 32),
        true,
        0,
        "",
        "Reset screen density"
    ),
)
