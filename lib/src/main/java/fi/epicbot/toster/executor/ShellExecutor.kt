package fi.epicbot.toster.executor

import com.lordcodes.turtle.ShellLocation
import com.lordcodes.turtle.shellRun
import fi.epicbot.toster.extension.saveForPath
import java.io.File

class ShellExecutor(projectDir: String) {

    internal var workingDir = ShellLocation.CURRENT_WORKING + projectDir.saveForPath()
    private var screenWorkingDir = workingDir

    init {
        makeDir(workingDir.toString(), clearBefore = true)
    }

    fun setScreenDirAndMakeIt(screenName: String) {
        screenWorkingDir = workingDir + "/${screenName.saveForPath()}"
        makeDir(screenWorkingDir.toString())
    }

    fun makeDir(path: String, clearBefore: Boolean = false): String =
        makeDir(path, ShellLocation.CURRENT_WORKING, clearBefore)

    fun makeDirForScreen(path: String): String = makeDir(path, screenWorkingDir)

    fun runShellCommand(command: String): String {
        return shellRun(workingDir) {
            command("/bin/sh", listOf("-c", command))
        }
    }

    fun runCommandForScreen(command: String, arguments: String): String {
        return shellRun(screenWorkingDir) {
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
                command("rm", listOf("-r", path))
            }
            command("mkdir", listOf("-p", path))
        }
    }
}

@Suppress("UnusedPrivateMember")
private operator fun File?.plus(s: String): File {
    return File(this.toString() + s)
}
