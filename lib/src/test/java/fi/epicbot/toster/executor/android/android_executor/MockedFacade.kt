package fi.epicbot.toster.executor.android.android_executor

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.executor.android.AndroidExecutor
import fi.epicbot.toster.memory.DumpSysParser
import fi.epicbot.toster.memory.GfxInfoParser
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.time.TimeProvider
import io.mockk.every
import io.mockk.mockk

internal const val SERIAL_NAME = "12345678"
internal const val IMAGE_PREFIX = "prefix"
internal const val PACKAGE_NAME = "fi.test"
internal const val SYSTEM_UI_COMMAND = "am broadcast -a com.android.systemui.demo -e command"

internal class MockedFacade {
    val config: Config
    val timeProvider: TimeProvider
    val shellExecutor: ShellExecutor
    val dumpSysParser: DumpSysParser
    val gfxInfoParser: GfxInfoParser

    init {
        config = mockk(relaxed = true)
        every { config.applicationPackageName }.returns(PACKAGE_NAME)
        timeProvider = mockk(relaxed = true)
        shellExecutor = mockk(relaxed = true)
        dumpSysParser = mockk(relaxed = true)
        gfxInfoParser = mockk(relaxed = true)
    }

    fun adb(command: String): String = shellExecutor.runCommandForScreen(
        "adb",
        command
    )

    fun adbShell(command: String): String = shellExecutor.runCommandForScreen(
        "adb",
        "shell $command"
    )
}

internal fun provideExecutor(facade: MockedFacade): ActionExecutor {
    return AndroidExecutor(
        SERIAL_NAME,
        facade.config,
        facade.shellExecutor,
        facade.dumpSysParser,
        facade.gfxInfoParser,
        facade.timeProvider,
    )
}
