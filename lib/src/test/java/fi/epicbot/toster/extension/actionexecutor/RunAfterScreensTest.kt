package fi.epicbot.toster.extension.actionexecutor

import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.extension.runAfterScreens
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.ScreenSize
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
import kotlin.reflect.KClass

private open class RunAfterScreensTestSpec(
    actionExecutor: ActionExecutor,
    config: Config,
    reportScreen: MutableList<ReportScreen>,
) : DescribeSpec({
    describe("test") {
        reportScreen.add(
            runAfterScreens(actionExecutor, config)
        )
    }
})

internal class RunAfterScreensTest : BehaviorSpec({

    val collector = CollectingTestEngineListener()

    applySizeTestData.forEach { runBeforeScreensModel ->
        Given("run Shells For Apk ${runBeforeScreensModel.title}") {
            val projectConfiguration = ProjectConfiguration()
            val tmpList = mutableListOf<ReportScreen>()

            val actionExecutor = mockk<ActionExecutor>(relaxed = true)
            coEvery { actionExecutor.execute(any()) } returns Common(2, "", 3, 4)

            When("execute action") {
                val ref = SpecRef.Function({
                    RunAfterScreensTestSpec(
                        actionExecutor,
                        runBeforeScreensModel.config,
                        tmpList,
                    )
                }, RunAfterScreensTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()
                CoVerify("finishEnvironment called") {
                    actionExecutor.finishEnvironment()
                }
                runBeforeScreensModel.expectedActions.forEach {
                    CoVerify("${it.simpleName} called") {
                        actionExecutor.execute(ofType(it))
                    }
                }
                Then("called test") {
                    collector.result("test")?.isSuccess shouldBe true
                }
                Then("report name should be After") {
                    tmpList[0].name shouldBe "After"
                }
            }
        }
    }
})

private class RunAfterScreensTestModel(
    val title: String,
    val config: Config,
    val expectedActions: List<KClass<out Action>>,
)

private val applySizeTestData = listOf(
    RunAfterScreensTestModel(
        "Default config",
        Config(),
        listOf(
            Action.HideDemoMode::class,
        ),
    ),
    RunAfterScreensTestModel(
        "Custom config",
        Config().apply {
            shellsAfterAllScreens = arrayOf("shell 1", "shell 2")
            globalScreenDensity = Density.XHDPI
            globalScreenSize = ScreenSize(2, 3)
            useDemoMode = false
        },
        listOf(
            Action.ShellAfterAllScreens::class,
            Action.ShellAfterAllScreens::class,
            Action.ResetScreenDensity::class,
            Action.ResetScreenSize::class,
        ),
    ),
)
