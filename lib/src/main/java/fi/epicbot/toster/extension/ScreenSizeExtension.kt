package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.ScreenSize
import fi.epicbot.toster.model.runAction
import fi.epicbot.toster.report.model.ReportScreen
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

internal suspend fun DescribeSpecContainerScope.apply(
    screenSize: ScreenSize?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    executeCondition: Boolean = true,
) {
    screenSize?.let { screenSize ->
        runAction(Action.SetScreenSize(screenSize), actionExecutor, reportScreen, executeCondition)
    }
}

internal suspend fun DescribeSpecContainerScope.reset(
    screenSize: ScreenSize?,
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
) {
    runAction(
        Action.ResetScreenSize,
        actionExecutor,
        reportScreen,
        executeCondition = screenSize != null
    )
}
