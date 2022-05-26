package fi.epicbot.toster.executor.android.androidexecutor

import fi.epicbot.toster.Then
import fi.epicbot.toster.ThenInstanseOf
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.report.model.Memory
import fi.epicbot.toster.report.model.MemoryCell
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private const val TAKE_MEMORY_TITLE = "Take memory allocation"
private val MEMORY_MEASUREMENTS = mapOf("memory" to MemoryCell(1L, 3L, 5L, 42L))

class MemoryAllocationTest : BehaviorSpec({

    Given("AndroidExecutor") {
        val facade = MockedFacade()
        every { facade.timeProvider.getTimeMillis() }.returnsMany(0L, 4L)
        every {
            facade.adbShell("dumpsys meminfo $PACKAGE_NAME -d")
        }.returns("raw data")
        every { facade.parserProvider.dumpSysParser.parse("raw data") }.returns(MEMORY_MEASUREMENTS)
        val androidExecutor = provideAndroidExecutor(facade)
        When("Execute action TakeMemoryAllocation") {
            val res = androidExecutor.execute(Action.TakeMemoryAllocation, IMAGE_PREFIX)
            Then("Name should be $TAKE_MEMORY_TITLE", res.name, TAKE_MEMORY_TITLE)
            Verify("check shell") {
                facade.adbShell("dumpsys meminfo $PACKAGE_NAME -d")
            }
            Then("Index should be 0", res.index, 0)
            Then("startTime should be 0", res.startTime, 0L)
            Then("endTime should be 4", res.endTime, 4L)
            ThenInstanseOf<Memory>("ReportAction should be Memory", res)
            Then("Check Measurements", (res as Memory).measurements, MEMORY_MEASUREMENTS)
        }
    }
})
