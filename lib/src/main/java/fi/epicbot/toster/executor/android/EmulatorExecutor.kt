package fi.epicbot.toster.executor.android

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.parser.DumpSysParser
import fi.epicbot.toster.parser.GfxInfoParser
import fi.epicbot.toster.report.model.Device
import fi.epicbot.toster.time.TimeProvider

@Suppress("LongParameterList")
internal class EmulatorExecutor(
    private val config: Config,
    private val serialName: String,
    private val startDelayMillis: Long,
    private val shellExecutor: ShellExecutor,
    private val dumpSysParser: DumpSysParser,
    private val gfxInfoParser: GfxInfoParser,
    private val timeProvider: TimeProvider,
) : AndroidExecutor(serialName, config = config, shellExecutor, dumpSysParser, gfxInfoParser, timeProvider) {

    private val emulatorPath = config.emulatorPath

    override fun executor() = Device(type = "Emulator", name = serialName)

    override suspend fun prepareEnvironment() {
        startEmulator(serialName, EMULATOR_PORT)
    }

    override suspend fun finishEnvironment() {
        stopEmulator(EMULATOR_PORT)
    }

    private suspend fun startEmulator(name: String, port: String) {
        runShellCommand("$emulatorPath/emulator -avd $name -port $port & adb wait-for-device")
        shellExecutor.delay(startDelayMillis)
    }

    private fun stopEmulator(port: String) {
        runShellCommand("adb -s emulator-$port emu kill & wait")
    }

    private fun runShellCommand(command: String) {
        shellExecutor.runShellCommand(command, fromRootFolder = true)
    }

    private companion object {
        const val EMULATOR_PORT = "5554"
    }
}
