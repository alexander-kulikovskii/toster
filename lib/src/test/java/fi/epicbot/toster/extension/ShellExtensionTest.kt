package fi.epicbot.toster.extension

import fi.epicbot.toster.Verify
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.model.Apk
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.time.TimeProvider
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

private open class RunShellsForApkTestSpec(
    shellExecutor: ShellExecutor,
    timeProvider: TimeProvider,
    apk: Apk,
    reportScreen: MutableList<ReportScreen>,
) : DescribeSpec({
    describe("test") {
        reportScreen.add(
            shellExecutor.runShellsForApk(
                timeProvider,
                apk,
            )
        )
    }
})

internal class ShellExtensionTest : BehaviorSpec({

    val collector = CollectingTestEngineListener()

    applySizeTestData.forEach { runShellsModel ->
        Given("run Shells For Apk ${runShellsModel.shellCommands.joinToString(", ")}") {
            val projectConfiguration = ProjectConfiguration()
            val tmpList = mutableListOf<ReportScreen>()

            val shellExecutor = mockk<ShellExecutor>(relaxed = true)
            every { shellExecutor.runShellCommand(any(), any()) } returns "Blaf"
            val timeProvider = mockk<TimeProvider>(relaxed = true)
            every { timeProvider.getTimeMillis() } returns 7
            val apk = Apk("url", runShellsModel.shellCommands.toTypedArray(), "prefix")

            When("execute action") {
                val ref = SpecRef.Function({
                    RunShellsForApkTestSpec(
                        shellExecutor,
                        timeProvider,
                        apk,
                        tmpList,
                    )
                }, RunShellsForApkTestSpec::class)
                TestEngineLauncher(collector, projectConfiguration, emptyList(), listOf(ref), null)
                    .withExtensions()
                    .launch()

                Then("called test") {
                    collector.result("test")?.isSuccess shouldBe true
                }
                runShellsModel.expectedTitles.forEach {
                    Then("called $it") {
                        collector.result(it)?.isSuccess shouldBe true
                    }
                }
                runShellsModel.shellCommands.filter { it.isNotBlank() }.forEach {
                    Verify("check action executor with command $it") {
                        shellExecutor.runShellCommand(it, true)
                    }
                }
                Then("common list size should be 1") {
                    tmpList.size shouldBe 1
                }

                Then("first item  size should be ${runShellsModel.expectedTitles.size}") {
                    tmpList[0].common.size shouldBe runShellsModel.expectedTitles.size
                }
                runShellsModel.expectedTitles.forEachIndexed { index, s ->
                    val actualCommon = tmpList[0].common[index]
                    Then("common name should be $s") {
                        actualCommon.name shouldBe s
                    }
                    Then("common index should be -1") {
                        actualCommon.index shouldBe -1
                    }
                    Then("common start time should be 7") {
                        actualCommon.startTime shouldBe 7
                    }
                    Then("common end time should be 7") {
                        actualCommon.endTime shouldBe 7
                    }
                }
            }
        }
    }
})

private class RunShellsTestModel(
    val shellCommands: List<String>,
    val expectedTitles: List<String>,
)

private val applySizeTestData = listOf(
    RunShellsTestModel(
        listOf("test1", "test2"),
        listOf("Run command <test1>", "Run command <test2>"),
    ),
    RunShellsTestModel(
        listOf("test1", " "),
        listOf("Run command <test1>"),
    ),
)
