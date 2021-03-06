package fi.epicbot.toster.checker

import fi.epicbot.toster.extension.throwExceptionIfBlank
import fi.epicbot.toster.extension.throwExceptionIfNotEmptyButBlank
import fi.epicbot.toster.extension.throwExceptionIfOneBlank
import fi.epicbot.toster.model.Config

internal class ConfigChecker(private val config: Config) : Checker {

    @Suppress("ComplexMethod")
    override fun check() {
        config.run {
            applicationName.throwExceptionIfBlank(
                EMPTY_APPLICATION_NAME
            )
            applicationPackageName.throwExceptionIfBlank(
                EMPTY_APPLICATION_PACKAGE_NAME
            )
            val emptyMultiApk = multiApk.apks.isEmpty() || multiApk.apks.all { it.url.isBlank() }
            if (deleteAndInstallApk && emptyMultiApk) {
                throw IllegalArgumentException(EMPTY_APK_URL)
            }
            permissions.granted.throwExceptionIfOneBlank(EMPTY_GRANTED_PERMISSIONS)

            if (devices.emulators.isEmpty() && devices.phones.isEmpty()) {
                throw IllegalArgumentException(EMPTY_DEVICE_LIST)
            }
            if (devices.emulators.isNotEmpty()) {
                emulatorPath.throwExceptionIfBlank(
                    EMPTY_EMULATOR_PATH
                )
            }
            devices.emulators.forEach { emulator ->
                emulator.name.throwExceptionIfBlank(EMPTY_EMULATOR_NAME)
            }
            devices.phones.forEach { phone ->
                phone.uuid.throwExceptionIfBlank(EMPTY_PHONE_UUID)
            }
            shellsBeforeAllScreens.forEach { shell ->
                shell.throwExceptionIfNotEmptyButBlank(
                    BLANK_SHELL_BEFORE_ALL_SCREENS
                )
            }
            shellsAfterAllScreens.forEach { shell ->
                shell.throwExceptionIfNotEmptyButBlank(
                    BLANK_SHELL_AFTER_ALL_SCREENS
                )
            }
            if (testTimeoutMillis <= 0) {
                throw IllegalArgumentException(TIMEOUT_TOO_SMALL)
            }
            if (!demoModeTime.all { it.isDigit() } || demoModeTime.length != DEMO_MODE_TIME_DEFAULT_LENGTH) {
                throw IllegalArgumentException(DEMO_MODE_TIME_FORMAT)
            }
        }
    }

    private companion object {
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
        private const val DEMO_MODE_TIME_FORMAT = "Wrong format for demo mode time"
        private const val DEMO_MODE_TIME_DEFAULT_LENGTH = 4
    }
}
