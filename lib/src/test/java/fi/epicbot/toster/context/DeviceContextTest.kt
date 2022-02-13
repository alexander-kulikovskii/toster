package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import fi.epicbot.toster.model.Emulator
import fi.epicbot.toster.model.Phone
import io.kotest.core.spec.style.BehaviorSpec

private class DeviceContextData(
    val name: String,
    val action: DeviceContext.() -> Unit,
    val expectedEmulators: List<Emulator> = emptyList(),
    val expectedPhones: List<Phone> = emptyList(),
)

private const val EMULATOR_NAME = "emulator Name"
private const val PHONE_UUID = "phone uuid"

private val deviceContextList = listOf(
    DeviceContextData(
        "All empty",
        {
        },
    ),
    DeviceContextData(
        "One emulator",
        {
            emulator(EMULATOR_NAME)
        },
        expectedEmulators = listOf(Emulator(EMULATOR_NAME))
    ),
    DeviceContextData(
        "One phone",
        {
            phone(PHONE_UUID)
        },
        expectedPhones = listOf(Phone(PHONE_UUID))
    ),
    DeviceContextData(
        "Two emulators",
        {
            emulator(EMULATOR_NAME)
            emulator(EMULATOR_NAME + "2")
        },
        expectedEmulators = listOf(
            Emulator(EMULATOR_NAME),
            Emulator(EMULATOR_NAME + 2),
        )
    ),
    DeviceContextData(
        "Two emulators (vararg)",
        {
            emulators(EMULATOR_NAME, EMULATOR_NAME + "2")
        },
        expectedEmulators = listOf(
            Emulator(EMULATOR_NAME),
            Emulator(EMULATOR_NAME + 2),
        )
    ),
    DeviceContextData(
        "Two phones",
        {
            phone(PHONE_UUID)
            phone(PHONE_UUID + "2")
        },
        expectedPhones = listOf(
            Phone(PHONE_UUID),
            Phone(PHONE_UUID + "2"),
        )
    ),
    DeviceContextData(
        "Two phones (vararg)",
        {
            phones(PHONE_UUID, PHONE_UUID + "2")
        },
        expectedPhones = listOf(
            Phone(PHONE_UUID),
            Phone(PHONE_UUID + "2"),
        )
    ),
    DeviceContextData(
        "All types",
        {
            emulators(EMULATOR_NAME)
            phone(PHONE_UUID)
        },
        expectedEmulators = listOf(
            Emulator(EMULATOR_NAME),
        ),
        expectedPhones = listOf(
            Phone(PHONE_UUID),
        )
    ),
)

internal class DeviceContextTest : BehaviorSpec({
    deviceContextList.forEach { deviceData ->
        Given("check ${deviceData.name}") {
            val deviceContext = DeviceContext()
            When("Invoke action") {
                deviceData.action.invoke(deviceContext)
                val actualEmulators =
                    deviceContext.emulators.joinToString { it.name + it.startDelayMillis }
                val actualPhones = deviceContext.phones.joinToString { it.uuid }
                val expectedEmulators =
                    deviceData.expectedEmulators.joinToString { it.name + it.startDelayMillis }
                val expectedPhones = deviceData.expectedPhones.joinToString { it.uuid }

                Then("Emulators should be $expectedEmulators", expectedEmulators, actualEmulators)
                Then("Phones should be $expectedPhones", expectedPhones, actualPhones)
            }
        }
    }
})
