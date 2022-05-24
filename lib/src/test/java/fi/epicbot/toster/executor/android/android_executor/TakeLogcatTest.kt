package fi.epicbot.toster.executor.android.android_executor

import fi.epicbot.toster.Then
import fi.epicbot.toster.Verify
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Buffer
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every

private const val TAKE_LOGCAT_TITLE = "Take logcat "
private val BUFFER_DATA = mapOf(
    Buffer.DEFAULT to "default",
    Buffer.RADIO to "radio",
    Buffer.EVENTS to "events",
    Buffer.MAIN to "main",
    Buffer.SYSTEM to "system",
    Buffer.CRASH to "crash",
    Buffer.ALL to "all",
)

class TakeLogcatTest : BehaviorSpec({

    Given("AndroidExecutor") {
        val facade = MockedFacade()
        val androidExecutor = provideAndroidExecutor(facade)
        var index = 0
        BUFFER_DATA.forEach { (buffer, bufferName) ->
            every { facade.timeProvider.getTimeMillis() }.returnsMany(1L + index, 42L)
            val screenshotFileName = "$SERIAL_NAME/logcat_$index.txt"
            every {
                facade.shell("adb logcat -b ${buffer.bufferName} -d > $screenshotFileName", false)
            }.returns("")
            When("Execute action TakeLogcat ${buffer.bufferName}") {
                val res = androidExecutor.execute(Action.TakeLogcat(buffer), IMAGE_PREFIX)
                Then(
                    "Name should be $TAKE_LOGCAT_TITLE${buffer.bufferName}",
                    res.name,
                    TAKE_LOGCAT_TITLE + buffer.bufferName
                )
                Verify("check shell") {
                    facade.shell(
                        "adb logcat -b $bufferName -d > $screenshotFileName",
                        false
                    )
                }
                Then("Index should be $index", res.index, index)
                Then("startTime should be ${index + 1}", res.startTime, 1L + index)
                Then("endTime should be 42", res.endTime, 42L)
            }
            index++
        }
    }
})
