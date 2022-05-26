package fi.epicbot.toster.executor.android.android_executor

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.executor.android.AndroidExecutor
import fi.epicbot.toster.executor.android.EmulatorExecutor
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.parser.ParserProvider
import fi.epicbot.toster.time.TimeProvider
import io.mockk.every
import io.mockk.mockk

internal const val SERIAL_NAME = "12345678"
internal const val IMAGE_PREFIX = "prefix"
internal const val PACKAGE_NAME = "fi.test"
internal const val SYSTEM_UI_COMMAND = "am broadcast -a com.android.systemui.demo -e command"
internal const val EMULATOR_PATH = "path"

internal class MockedFacade {
    val config: Config
    val timeProvider: TimeProvider
    val shellExecutor: ShellExecutor
    val parserProvider: ParserProvider

    init {
        config = mockk(relaxed = true)
        every { config.emulatorPath }.returns(EMULATOR_PATH)
        every { config.applicationPackageName }.returns(PACKAGE_NAME)
        timeProvider = mockk(relaxed = true)
        shellExecutor = mockk(relaxed = true)
        parserProvider = mockk(relaxed = true)
    }

    fun adb(command: String): String = shellExecutor.runCommandForScreen(
        "adb",
        command
    )

    fun shell(command: String, fromRootFolder: Boolean = true): String =
        shellExecutor.runShellCommand(
            command,
            fromRootFolder,
        )

    fun adbShell(command: String): String = shellExecutor.runCommandForScreen(
        "adb",
        "shell $command"
    )
}

internal fun provideAndroidExecutor(facade: MockedFacade): ActionExecutor {
    return AndroidExecutor(
        SERIAL_NAME,
        facade.config,
        facade.shellExecutor,
        facade.parserProvider,
        facade.timeProvider,
    )
}

internal fun provideEmulatorExecutor(
    facade: MockedFacade,
    startDelayMillis: Long = 2000L
): ActionExecutor {
    return EmulatorExecutor(
        facade.config,
        SERIAL_NAME,
        startDelayMillis,
        facade.shellExecutor,
        facade.parserProvider,
        facade.timeProvider,
    )
}
