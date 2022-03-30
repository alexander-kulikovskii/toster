package fi.epicbot.toster.model

import fi.epicbot.toster.executor.android.AndroidExecutor
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.GfxInfo
import fi.epicbot.toster.report.model.Memory
import fi.epicbot.toster.report.model.ReportAction
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.report.model.Screenshot
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestScope
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.mockk.mockk

private val reportScreen = ReportScreen("test report")
private val actionExecutor = AndroidExecutor(
    "serial", mockk(relaxed = true), mockk(relaxed = true),
    mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true)
)
private const val IMAGE_PREFIX = "prefix"

private open class ActionTestSpec(action: Action, reportScreen: ReportScreen) : DescribeSpec({
    describe(action.title()) {
        runAction(
            action,
            actionExecutor,
            reportScreen,
            IMAGE_PREFIX,
            true,
        )
    }
})

private class ActionTestData(
    val name: String,
    val action: Action,
    val reportAction: ReportAction,
    val reportScreenNumberCheck: suspend TestScope.() -> Unit,
    val reportScreenModelCheck: suspend TestScope.() -> Unit,
)

private val data = listOf(
    ActionTestData(
        "Clear app data",
        Action.ClearAppData,
        Common(0, "Clear app data", 0, 4),
        {
            reportScreen.common.size shouldBe 1
        },
        {
            val actualCommon = reportScreen.common[0]
            actualCommon.name shouldBe "Clear app data"
        }
    ),
    ActionTestData(
        "Take screenshot",
        Action.TakeScreenshot("name"),
        Screenshot(1, "name", 0, 4, "prefix", "path"),
        {
            reportScreen.screenshots.size shouldBe 1
        },
        {
            val actualCommon = reportScreen.screenshots[0]
            actualCommon.name shouldBe "Take screenshot"
        }
    ),
    ActionTestData(
        "Take gfxinfo",
        Action.TakeGfxInfo,
        GfxInfo(0, "name", 0, 4, emptyMap()),
        {
            reportScreen.gfxInfo.size shouldBe 1
        },
        {
            val actualCommon = reportScreen.gfxInfo[0]
            actualCommon.name shouldBe "Take gfxinfo"
        }
    ),
    ActionTestData(
        "Take memory allocation",
        Action.TakeMemoryAllocation,
        Memory(0, "name", 0, 4, emptyMap()),
        {
            reportScreen.memory.size shouldBe 1
        },
        {
            val actualCommon = reportScreen.memory[0]
            actualCommon.name shouldBe "Take memory allocation"
        }
    ),
)

class ActionTest : BehaviorSpec({

    @AnnotationSpec.BeforeEach
    fun beforeTest() {
        reportScreen.common.clear()
        reportScreen.screenshots.clear()
        reportScreen.gfxInfo.clear()
        reportScreen.memory.clear()
    }

    data.forEach { actionTestData ->
        Given("Action ${actionTestData.action.title()}") {
            val collector = CollectingTestEngineListener()
            val projectConfiguration = ProjectConfiguration()

            When("execute action") {
                val ref = SpecRef.Function({
                    ActionTestSpec(actionTestData.action, reportScreen)
                }, ActionTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()

                Then("check test name") {
                    collector.result(actionTestData.name)?.isSuccess shouldBe true
                }
                Then("check report", actionTestData.reportScreenNumberCheck)
                Then("check model", actionTestData.reportScreenModelCheck)
            }
        }
    }
})
