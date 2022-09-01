package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.runAction
import fi.epicbot.toster.report.model.ReportScreen
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

internal suspend fun DescribeSpecContainerScope.apply(
    density: Density?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    executeCondition: Boolean = true,
) {
    density?.let { screenDensity ->
        runAction(
            Action.SetScreenDensity(screenDensity),
            actionExecutor,
            reportScreen,
            executeCondition
        )
    }
}

internal suspend fun DescribeSpecContainerScope.reset(
    density: Density?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
) {
    runAction(
        Action.ResetScreenDensity,
        actionExecutor,
        reportScreen,
        executeCondition = density != null
    )
}
