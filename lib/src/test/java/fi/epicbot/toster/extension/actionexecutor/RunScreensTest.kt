package fi.epicbot.toster.extension.actionexecutor

import fi.epicbot.toster.CoVerify
import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.extension.runScreens
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.ActivityParam
import fi.epicbot.toster.model.Apk
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Overdraw
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.Screen
import fi.epicbot.toster.model.ScreenSize
import fi.epicbot.toster.report.model.Device
import fi.epicbot.toster.report.model.ReportDevice
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KClass

private open class RunScreensTestSpec(
    actionExecutor: ActionExecutor,
    config: Config,
    apk: Apk,
    screens: List<Screen>,
    reportDevices: MutableList<ReportDevice>,
) : DescribeSpec({
    describe("test") {
        actionExecutor.runScreens(config, apk, screens, reportDevices)
    }
})

internal class RunScreensTest : BehaviorSpec({

    val collector = CollectingTestEngineListener()

    applySizeTestData.forEach { runScreensModel ->
        Given("run Shells For Apk ${runScreensModel.title}") {
            val projectConfiguration = ProjectConfiguration()
            val tmpList = mutableListOf<ReportDevice>()

            val actionExecutor = mockk<ActionExecutor>(relaxed = true)
            every { actionExecutor.executor() } returns Device("test type", "test name")
            if (runScreensModel.config.checkOverdraw.check) {
                every { actionExecutor.imagePrefix }.returnsMany("normal", "overdraw")
            } else {
                every { actionExecutor.imagePrefix } returns ("normal")
            }

            When("execute action") {
                val ref = SpecRef.Function({
                    RunScreensTestSpec(
                        actionExecutor,
                        runScreensModel.config,
                        runScreensModel.apk,
                        runScreensModel.screens,
                        tmpList,
                    )
                }, RunScreensTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()
                CoVerify("prepareEnvironment called") {
                    actionExecutor.prepareEnvironment()
                }
                CoVerify("finishEnvironment called") {
                    actionExecutor.finishEnvironment()
                }
                runScreensModel.expectedActions.forEach {
                    CoVerify("${it.simpleName} called", ordering = Ordering.ORDERED) {
                        actionExecutor.execute(ofType(it))
                    }
                }
                Then("called test for device") {
                    collector.result("test type <test name>")?.isSuccess shouldBe true
                }
                Then("report name should be Before") {
                    tmpList[0].device.name shouldBe "test name"
                }
                runScreensModel.expectedReportScreenNames.forEachIndexed { index, s ->
                    Then("test") {
                        tmpList[0].reportScreens[index].name shouldBe s
                    }
                }
                runScreensModel.screens.forEach { screen ->
                    val screenName = "Screen: ${screen.name}; normal"
                    Then("run screen with name $screenName") {
                        collector.result(screenName)?.isSuccess shouldBe true
                    }
                    if (runScreensModel.config.checkOverdraw.check) {
                        val screenNameOverdraw = "Screen: ${screen.name}; overdraw"
                        Then("run screen with name $screenNameOverdraw") {
                            collector.result(screenNameOverdraw)?.isSuccess shouldBe true
                        }
                    }
                }
            }
        }
    }
})

private class RunScreensTestModel(
    val title: String,
    val config: Config,
    val apk: Apk,
    val screens: List<Screen>,
    val expectedActions: List<KClass<out Action>>,
    val expectedReportScreenNames: List<String>,
)

private val applySizeTestData = listOf(
    RunScreensTestModel(
        "Default config",
        Config(),
        Apk(),
        emptyList(),
        listOf(
            Action.ClearAppData::class,
            Action.DeleteApk::class,
            Action.InstallApk::class,
            Action.HideDemoMode::class,
            Action.SetDemoModeEnable::class,
            Action.ShowDemoMode::class,
            Action.HideGpuOverdraw::class,

            Action.HideDemoMode::class,
        ),
        listOf("Before", "After"),
    ),
    RunScreensTestModel(
        "One default screen",
        Config(),
        Apk(),
        listOf(
            Screen()
        ),
        listOf(
            Action.ClearAppData::class,
            Action.DeleteApk::class,
            Action.InstallApk::class,
            Action.HideDemoMode::class,
            Action.SetDemoModeEnable::class,
            Action.ShowDemoMode::class,
            Action.HideGpuOverdraw::class,

            Action.TakeScreenshot::class,
            Action.CloseApp::class,

            Action.HideDemoMode::class,
        ),
        listOf("Before", "", "After"),
    ),

    RunScreensTestModel(
        "One custom screen",
        Config(
            checkOverdraw = Overdraw(check = true),
        ),
        Apk(),
        listOf(
            Screen(
                name = "screen",
                url = "url",
                fontScale = FontScale.LARGE,
                activityParams = mutableListOf(ActivityParam.IntegerActivityParam("param", 31)),
                actions = mutableListOf(Action.TakeScreenshot("take screenshot")),
                clearDataBeforeRun = true,
                shellsBefore = arrayOf("shell before"),
                shellsAfter = arrayOf("shell after"),
                permissions = Permissions(listOf("perm1"), listOf("perm2")),
                resetGfxInfoBeforeStart = true,
                closeAppsInTrayBeforeStart = true,
                screenDensity = Density.XHDPI,
                screenSize = ScreenSize(43, 22),
                clearLogcatBefore = true,
            )
        ),
        listOf(
            Action.ClearAppData::class,
            Action.DeleteApk::class,
            Action.InstallApk::class,
            Action.HideDemoMode::class,
            Action.SetDemoModeEnable::class,
            Action.ShowDemoMode::class,
            Action.HideGpuOverdraw::class,

            Action.ShellBeforeScreen::class,
            Action.ClearLogcat::class,
            Action.SetScreenDensity::class,
            Action.SetScreenSize::class,

            Action.ClearAppData::class,
            Action.GrantPermission::class,
            Action.RevokePermission::class,
            Action.SetFontScale::class,

            Action.CloseAppsInTray::class,
            Action.ResetGfxInfo::class,
            Action.OpenScreen::class,
            Action.TakeScreenshot::class,

            Action.SetFontScale::class,
            Action.CloseApp::class,
            Action.ResetScreenSize::class,
            Action.ResetScreenDensity::class,

            Action.ShellAfterScreen::class,

            // Overdraw
            Action.ShowGpuOverdraw::class,

            Action.ShellBeforeScreen::class,
            Action.ClearLogcat::class,
            Action.SetScreenDensity::class,
            Action.SetScreenSize::class,

            Action.ClearAppData::class,
            Action.GrantPermission::class,
            Action.RevokePermission::class,
            Action.SetFontScale::class,

            Action.CloseAppsInTray::class,
            Action.ResetGfxInfo::class,
            Action.OpenScreen::class,
            Action.TakeScreenshot::class,

            Action.SetFontScale::class,
            Action.CloseApp::class,
            Action.ResetScreenSize::class,
            Action.ResetScreenDensity::class,

            Action.ShellAfterScreen::class,

            Action.HideGpuOverdraw::class,

            // end overdraw

            Action.HideDemoMode::class,
        ),
        listOf("Before", "screen", "After"),
    ),
)
