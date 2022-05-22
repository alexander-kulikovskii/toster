package fi.epicbot.toster.executor

import com.lordcodes.turtle.ShellLocation
import com.lordcodes.turtle.shellRun
import fi.epicbot.toster.extension.plus
import fi.epicbot.toster.extension.safeForPath
import fi.epicbot.toster.logger.ShellLogger
import java.io.File

class ShellExecutor(
    projectDir: String,
    private val apkPrefix: String,
    private val logger: ShellLogger,
    clearBefore: Boolean,
) {

    internal var workingDir = ShellLocation.CURRENT_WORKING + projectDir.safeForPath()
        private set

    private var screenWorkingDir = workingDir

    init {
        makeDir(workingDir.toString(), clearBefore = clearBefore)
    }

    suspend fun delay(delayMillis: Long) {
        kotlinx.coroutines.delay(delayMillis)
    }

    fun setScreenDirAndMakeIt(screenName: String) {
        screenWorkingDir = workingDir + "/$apkPrefix/${screenName.safeForPath()}"
        makeDir(screenWorkingDir.toString())
    }

    fun makeDir(path: String, clearBefore: Boolean = false): String =
        makeDir(path, ShellLocation.CURRENT_WORKING, clearBefore)

    fun makeDirForScreen(path: String): String = makeDir(path, screenWorkingDir)

    fun makeFile(path: String, fileName: String, content: String) {
        logger.logCommand("$MKDIR_COMMAND -p ${listOf(workingDir, path, fileName).joinToString("/")}")
        val dir = File("$workingDir/$path")
        if (dir.exists().not()) {
            dir.mkdirs()
        }
        File("$workingDir/$path/$fileName").bufferedWriter().use { out ->
            out.write(content)
        }
    }

    fun runShellCommand(command: String, fromRootFolder: Boolean = false): String {
        logger.logCommand("$SH_COMMAND ${listOf("-c", command).joinToString(" ")}")
        return shellRun(
            command = SH_COMMAND,
            arguments = listOf("-c", command),
            workingDirectory = if (fromRootFolder) ShellLocation.CURRENT_WORKING else workingDir
        )
    }

    fun runCommandForScreen(command: String, arguments: String): String {
        logger.logCommand("$command $arguments")
        return shellRun(
            command = command,
            arguments = arguments.split(" "),
            workingDirectory = screenWorkingDir
        )
    }

    private fun makeDir(
        path: String,
        workingDirectory: File,
        clearBefore: Boolean = false
    ): String {
        if (clearBefore) {
            logger.logCommand(
                "$SH_COMMAND ${listOf("-c", "rm -rf $path || true").joinToString(" ")}"
            )
            shellRun(
                command = SH_COMMAND,
                arguments = listOf("-c", "rm -rf $path || true"),
                workingDirectory = workingDirectory
            )
        }

        logger.logCommand("$MKDIR_COMMAND ${listOf("-p", path).joinToString(" ")}")
        return shellRun(
            command = MKDIR_COMMAND,
            arguments = listOf("-p", path),
            workingDirectory = workingDirectory
        )
    }

    private companion object {
        private const val SH_COMMAND = "/bin/sh"
        private const val MKDIR_COMMAND = "mkdir"
    }
}
