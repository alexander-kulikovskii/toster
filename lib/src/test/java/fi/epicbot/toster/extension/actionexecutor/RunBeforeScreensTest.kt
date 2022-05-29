package fi.epicbot.toster.extension.actionexecutor

import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.extension.runAfterScreens
import fi.epicbot.toster.extension.runBeforeScreens
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.BufferDimension
import fi.epicbot.toster.model.BufferSize
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

private open class RunBeforeScreensTestSpec(
    actionExecutor: ActionExecutor,
    config: Config,
    apkUrl: String,
    reportScreen: MutableList<ReportScreen>,
) : DescribeSpec({
    describe("test") {
        reportScreen.add(
            actionExecutor.runBeforeScreens(config, apkUrl)
        )
    }
})

internal class RunBeforeScreensTest : BehaviorSpec({

    val collector = CollectingTestEngineListener()

    applySizeTestData.forEach { runBeforeScreensModel ->
        Given("run Shells For Apk ${runBeforeScreensModel.title}") {
            val projectConfiguration = ProjectConfiguration()
            val tmpList = mutableListOf<ReportScreen>()

            val actionExecutor = mockk<ActionExecutor>(relaxed = true)
            coEvery { actionExecutor.execute(any()) } returns Common(2, "", 3, 4)

            When("execute action") {
                val ref = SpecRef.Function({
                    RunBeforeScreensTestSpec(
                        actionExecutor,
                        runBeforeScreensModel.config,
                        runBeforeScreensModel.apkUrl,
                        tmpList,
                    )
                }, RunBeforeScreensTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()
                CoVerify("prepareEnvironment called") {
                    actionExecutor.prepareEnvironment()
                }
                runBeforeScreensModel.expectedActions.forEach {
                    CoVerify("${it.simpleName} called") {
                        actionExecutor.execute(ofType(it))
                    }
                }
                Then("called test") {
                    collector.result("test")?.isSuccess shouldBe true
                }
                Then("report name should be Before") {
                    tmpList[0].name shouldBe "Before"
                }
            }
        }
    }
})

private class RunBeforeScreensTestModel(
    val title: String,
    val config: Config,
    val apkUrl: String,
    val expectedActions: List<KClass<out Action>>,
)

private val applySizeTestData = listOf(
    RunBeforeScreensTestModel(
        "Default config",
        Config(),
        "url default",
        listOf(
            Action.ClearAppData::class,
            Action.DeleteApk::class,
            Action.InstallApk::class,
            Action.HideDemoMode::class,
            Action.SetDemoModeEnable::class,
            Action.ShowDemoMode::class,
            Action.HideGpuOverdraw::class,
        ),
    ),
    RunBeforeScreensTestModel(
        "Custom config",
        Config().apply {
            restartAdbServiceBeforeEachDevice = true
            globalLogcatBufferSize = BufferSize(2, BufferDimension.MEGABYTES)
            shellsBeforeAllScreens = arrayOf("shell 1", "shell 2")
            globalScreenDensity = Density.XHDPI
            globalScreenSize = ScreenSize(2, 3)
            deleteAndInstallApk = false
            useDemoMode = false
        },
        "url custom",
        listOf(
            Action.RestartAdbService::class,
            Action.SetLogcatBufferSize::class,
            Action.ShellBeforeAllScreens::class,
            Action.ShellBeforeAllScreens::class,
            Action.SetScreenDensity::class,
            Action.SetScreenSize::class,
            Action.HideDemoMode::class,
            Action.HideGpuOverdraw::class,
        ),
    ),
)
