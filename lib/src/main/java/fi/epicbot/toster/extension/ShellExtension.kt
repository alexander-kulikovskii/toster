package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.model.Apk
import fi.epicbot.toster.report.model.ReportScreen
import fi.epicbot.toster.time.TimeProvider
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

context(DescribeSpecContainerScope)
internal suspend fun ShellExecutor.runShellsForApk(
    timeProvider: TimeProvider,
    apk: Apk,
): ReportScreen {
    val apkReport = ReportScreen(name = "Before")
    apk.shellsBefore.forEach { shell ->
        shell.runShellAction(
            timeProvider,
            this,
            apkReport,
            executeCondition = shell.isNotBlank(),
        )
    }
    return apkReport
}
