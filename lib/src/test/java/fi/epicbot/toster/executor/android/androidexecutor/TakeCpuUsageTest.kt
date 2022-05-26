package fi.epicbot.toster.executor.android.androidexecutor

import fi.epicbot.toster.Then
import fi.epicbot.toster.ThenInstanseOf
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.report.model.CpuCell
import fi.epicbot.toster.report.model.CpuUsage
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private const val TAKE_CPU_USAGE_TITLE = "Take cpu usage"
private val MEMORY_MEASUREMENTS = CpuCell(56.0)

class TakeCpuUsageTest : BehaviorSpec({

    Given("AndroidExecutor") {
        val facade = MockedFacade()
        every { facade.timeProvider.getTimeMillis() }.returnsMany(4L, 23L)
        every {
            facade.adbShell("ps | grep $PACKAGE_NAME")
        }.returns("123 234")

        every {
            facade.adbShell("nproc")
        }.returns("8")

        every {
            facade.adbShell("top -p 234 -d 0.1 -n 5")
        }.returns("test_data")

        every { facade.parserProvider.cpuUsageParser.parse("test_data", sampleNumber = 5, coreNumber = 8) }
            .returns(MEMORY_MEASUREMENTS)
        val androidExecutor = provideAndroidExecutor(facade)
        When("Execute action TakeMemoryAllocation") {
            val res = androidExecutor.execute(Action.TakeCpuUsage, IMAGE_PREFIX)
            Then("Name should be $TAKE_CPU_USAGE_TITLE", res.name, TAKE_CPU_USAGE_TITLE)
            Verify("check pid shell") {
                facade.adbShell("ps | grep $PACKAGE_NAME")
            }
            Verify("check core number shell") {
                facade.adbShell("nproc")
            }
            Verify("check cpu info shell") {
                facade.adbShell("top -p 234 -d 0.1 -n 5")
            }
            Then("Index should be 0", res.index, 0)
            Then("startTime should be 4", res.startTime, 4L)
            Then("endTime should be 23", res.endTime, 23L)
            ThenInstanseOf<CpuUsage>("ReportAction should be Memory", res)
            Then("Check Measurements", (res as CpuUsage).measurement, MEMORY_MEASUREMENTS)
        }
    }
})
