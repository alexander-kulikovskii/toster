package fi.epicbot.toster.executor

import com.lordcodes.turtle.ShellLocation
import com.lordcodes.turtle.shellRun
import fi.epicbot.toster.extension.saveForPath
import fi.epicbot.toster.logger.ShellLogger
import java.io.File

class ShellExecutor(projectDir: String, private val logger: ShellLogger) {

    internal var workingDir = ShellLocation.CURRENT_WORKING + projectDir.saveForPath()
    private var screenWorkingDir = workingDir

    init {
        makeDir(workingDir.toString(), clearBefore = true)
    }

    suspend fun delay(delayMillis:Long){
        kotlinx.coroutines.delay(delayMillis)
    }

    fun setScreenDirAndMakeIt(screenName: String) {
        screenWorkingDir = workingDir + "/${screenName.saveForPath()}"
        makeDir(screenWorkingDir.toString())
    }

    fun makeDir(path: String, clearBefore: Boolean = false): String =
        makeDir(path, ShellLocation.CURRENT_WORKING, clearBefore)

    fun makeDirForScreen(path: String): String = makeDir(path, screenWorkingDir)

    fun runShellCommand(command: String, fromRootFolder: Boolean = false): String {
        return shellRun(if (fromRootFolder) ShellLocation.CURRENT_WORKING else workingDir) {
            logger.logCommand("$SH_COMMAND ${listOf("-c", command).joinToString(" ")}")
            command(SH_COMMAND, listOf("-c", command))
        }
    }

    fun runCommandForScreen(command: String, arguments: String): String {
        return shellRun(screenWorkingDir) {
            logger.logCommand("$command $arguments")
            command(command, arguments.split(" "))
        }
    }

    private fun makeDir(
        path: String,
        workingDirectory: File,
        clearBefore: Boolean = false
    ): String {
        return shellRun(workingDirectory) {
            if (clearBefore) {
                logger.logCommand(
                    "$SH_COMMAND ${listOf("-c", "rm -rf $path || true").joinToString(" ")}"
                )
                command(SH_COMMAND, listOf("-c", "rm -rf $path || true"))
            }
            logger.logCommand("$MKDIR_COMMAND ${listOf("-p", path).joinToString(" ")}")
            command(MKDIR_COMMAND, listOf("-p", path))
        }
    }

    private companion object {
        private const val SH_COMMAND = "/bin/sh"
        private const val MKDIR_COMMAND = "mkdir"
    }
}

@Suppress("UnusedPrivateMember")
private operator fun File?.plus(s: String): File {
    return File(this.toString() + s)
}
