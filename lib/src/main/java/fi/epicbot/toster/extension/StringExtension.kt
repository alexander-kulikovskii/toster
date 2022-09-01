package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.report.model.Common
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.time.TimeProvider
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

internal fun CharSequence.throwExceptionIfBlank(message: String) {
    if (this.isBlank()) {
        throw IllegalArgumentException(message)
    }
}

internal fun CharSequence.throwExceptionIfNotEmptyButBlank(message: String) {
    if (this.isNotEmpty() && this.isBlank()) {
        throw IllegalArgumentException(message)
    }
}

internal fun String.findRow(name: String): List<String> {
    return try {
        split(name)[1].split(" ").filter {
            it.isNotEmpty()
        }
    } catch (_: Exception) {
        emptyList()
    }
}

internal fun String.safeForPath(): String {
    return this.trim().replace("[.,;!@#$%^&*()+=>< ]".toRegex(), "_")
}

internal suspend fun DescribeSpecContainerScope.runShellAction(
    command: String,
    timeProvider: TimeProvider,
    shellExecutor: ShellExecutor,
    reportScreen: ReportScreen,
    executeCondition: Boolean = true,
) {
    if (executeCondition) {
        it("Run command <$command>") {
            val startTime = timeProvider.getTimeMillis()
            shellExecutor.runShellCommand(command, fromRootFolder = true)
            val endTime = timeProvider.getTimeMillis()
            reportScreen.common.add(Common(-1, "Run command <$command>", startTime, endTime))
        }
    }
}
