package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.ScreenSize
import fi.epicbot.toster.model.runAction
import fi.epicbot.toster.report.model.ReportScreen
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

context (DescribeSpecContainerScope)
internal suspend fun ScreenSize?.apply(
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    executeCondition: Boolean = true,
) {
    this?.let { screenSize ->
        Action.SetScreenSize(screenSize)
            .runAction(actionExecutor, reportScreen, executeCondition)
    }
}

context (DescribeSpecContainerScope)
internal suspend fun ScreenSize?.reset(
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
) {
    Action.ResetScreenSize.runAction(
        actionExecutor,
        reportScreen,
        executeCondition = this != null
    )
}
