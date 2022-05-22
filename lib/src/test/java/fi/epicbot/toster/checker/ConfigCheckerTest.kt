package fi.epicbot.toster.checker

import fi.epicbot.toster.WhenWithException
import fi.epicbot.toster.WhenWithoutException
import fi.epicbot.toster.model.Apk
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.Devices
import fi.epicbot.toster.model.Emulator
import fi.epicbot.toster.model.MultiApk
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.Phone
import io.kotest.core.spec.style.BehaviorSpec

class ConfigCheckerPositiveTest : BehaviorSpec({
    correctData.forEach() { inputConfig ->
        Given("ConfigChecker with $inputConfig") {
            val configChecker = ConfigChecker(inputConfig)
            WhenWithoutException("Check config without any exception") {
                configChecker.check()
            }
        }
    }
})

class ConfigCheckerNegativeTest : BehaviorSpec({
    exceptionData.forEach() { (inputConfig, expectedMessage) ->
        Given("ConfigChecker with $inputConfig") {
            val configChecker = ConfigChecker(inputConfig)
            WhenWithException<IllegalArgumentException>(
                "Check config with exception",
                expectedMessage
            ) {
                configChecker.check()
            }
        }
    }
})

private const val EMPTY_APPLICATION_NAME = "Application name shouldn't be empty"
private const val EMPTY_APPLICATION_PACKAGE_NAME =
    "Application package name shouldn't be empty"
private const val EMPTY_APK_URL = "Apk url shouldn't be empty"
private const val EMPTY_EMULATOR_PATH = "Emulator path shouldn't be empty"
private const val EMPTY_DEVICE_LIST = "Set at least one emulator or one phone"
private const val EMPTY_EMULATOR_NAME = "Emulator name shouldn't be empty"
private const val EMPTY_PHONE_UUID = "Phone uuid shouldn't be empty"
private const val BLANK_SHELL_BEFORE_ALL_SCREENS = "Set non empty shell before all screens"
private const val BLANK_SHELL_AFTER_ALL_SCREENS = "Set non empty shell after all screens"
private const val TIMEOUT_TOO_SMALL = "Test timeout too small"
private const val EMPTY_GRANTED_PERMISSIONS = "All granted permissions shouldn't be empty"
private const val NOT_EMPTY_FIELD = "Test"
private val NOT_EMPTY_APK_LIST = MultiApk().apply { add(Apk(url = NOT_EMPTY_FIELD)) }
private val EMPTY_APK_LIST = MultiApk()
private val APK_LIST_WITH_EMPTY_URL = MultiApk().apply { add(Apk(url = "")) }

private val correctData = listOf(
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        deleteAndInstallApk = false,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD))),
        emulatorPath = NOT_EMPTY_FIELD,
        testTimeoutMillis = 1,
    ),
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        devices = Devices(phones = listOf(Phone(NOT_EMPTY_FIELD))),
    ),
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        deleteAndInstallApk = false,
        devices = Devices(phones = listOf(Phone(NOT_EMPTY_FIELD))),
    ),
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        deleteAndInstallApk = false,
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD))),
        emulatorPath = NOT_EMPTY_FIELD,
    ),
)

private val exceptionData = mapOf(
    Config(
        applicationName = "",
    ) to EMPTY_APPLICATION_NAME,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = ""
    ) to EMPTY_APPLICATION_PACKAGE_NAME,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = EMPTY_APK_LIST,
        deleteAndInstallApk = true,
    ) to EMPTY_APK_URL,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = APK_LIST_WITH_EMPTY_URL,
        deleteAndInstallApk = true,
    ) to EMPTY_APK_URL,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(""))
    ) to EMPTY_GRANTED_PERMISSIONS,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD, ""))
    ) to EMPTY_GRANTED_PERMISSIONS,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD))
    ) to EMPTY_DEVICE_LIST,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD)))
    ) to EMPTY_EMULATOR_PATH,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD), Emulator(""))),
        emulatorPath = NOT_EMPTY_FIELD
    ) to EMPTY_EMULATOR_NAME,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(
            emulators = listOf(Emulator(NOT_EMPTY_FIELD)),
            phones = listOf(Phone(""))
        ),
        emulatorPath = NOT_EMPTY_FIELD
    ) to EMPTY_PHONE_UUID,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD))),
        emulatorPath = NOT_EMPTY_FIELD,
        shellsBeforeAllScreens = arrayOf(" "),
    ) to BLANK_SHELL_BEFORE_ALL_SCREENS,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD))),
        emulatorPath = NOT_EMPTY_FIELD,
        shellsAfterAllScreens = arrayOf(" "),
    ) to BLANK_SHELL_AFTER_ALL_SCREENS,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD))),
        emulatorPath = NOT_EMPTY_FIELD,
        testTimeoutMillis = 0,
    ) to TIMEOUT_TOO_SMALL,
    Config(
        applicationName = NOT_EMPTY_FIELD,
        applicationPackageName = NOT_EMPTY_FIELD,
        multiApk = NOT_EMPTY_APK_LIST,
        deleteAndInstallApk = true,
        permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD)),
        devices = Devices(emulators = listOf(Emulator(NOT_EMPTY_FIELD))),
        emulatorPath = NOT_EMPTY_FIELD,
        testTimeoutMillis = -1,
    ) to TIMEOUT_TOO_SMALL,
)
