package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.model.Apk
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.time.TimeProvider
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

internal suspend fun DescribeSpecContainerScope.runShellsForApk(
    shellExecutor: ShellExecutor,
    timeProvider: TimeProvider,
    apk: Apk,
): ReportScreen {
    val apkReport = ReportScreen(name = "Before")
    apk.shellsBefore.forEach { shell ->
        runShellAction(
            shell,
            timeProvider,
            shellExecutor,
            apkReport,
            executeCondition = shell.isNotBlank(),
        )
    }
    return apkReport
}
