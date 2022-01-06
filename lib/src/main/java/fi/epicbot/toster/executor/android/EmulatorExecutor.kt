package fi.epicbot.toster.executor.android

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.memory.DumpSysParser
import fi.epicbot.toster.model.Config
import kotlinx.coroutines.delay

internal class EmulatorExecutor(
    private val config: Config,
    override val serialName: String,
    private val startDelayMillis: Long,
    private val shellExecutor: ShellExecutor,
    private val dumpSysParser: DumpSysParser,
) : AndroidExecutor(serialName, config = config, shellExecutor, dumpSysParser) {

    private val emulatorPath = config.emulatorPath

    override fun executorName(): String = "Emulator <$serialName>"

    override suspend fun prepareEnvironment() {
        startEmulator(serialName, EMULATOR_PORT)
    }

    override suspend fun finishEnvironment() {
        stopEmulator(EMULATOR_PORT)
    }

    private suspend fun startEmulator(name: String, port: String) {
        runShellCommand("$emulatorPath/emulator -avd $name -port $port & adb wait-for-device")
        delay(startDelayMillis)
    }

    private fun stopEmulator(port: String) {
        runShellCommand("adb -s emulator-$port emu kill & wait")
    }

    private fun runShellCommand(command: String) {
        shellExecutor.runShellCommand(command)
    }

    private companion object {
        const val EMULATOR_PORT = "5554"
    }
}
