package fi.epicbot.toster.extension

import fi.epicbot.toster.executor.ActionExecutor
import fi.epicbot.toster.model.Action
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.runAction
import fi.epicbot.toster.report.model.ReportScreen
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

context (DescribeSpecContainerScope)
internal suspend fun Density?.apply(
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    imagePrefix: String = "",
    executeCondition: Boolean = true,
) {
    this?.let { screenDensity ->
        Action.SetScreenDensity(screenDensity)
            .runAction(actionExecutor, reportScreen, imagePrefix, executeCondition)
    }
}

context (DescribeSpecContainerScope)
internal suspend fun Density?.reset(
    actionExecutor: ActionExecutor,
    reportScreen: ReportScreen,
    imagePrefix: String = "",
) {
    Action.ResetScreenDensity.runAction(
        actionExecutor,
        reportScreen,
        imagePrefix,
        executeCondition = this != null
    )
}
