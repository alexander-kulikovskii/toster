package fi.epicbot.toster.executor

import fi.epicbot.toster.model.Action
import fi.epicbot.toster.report.model.ReportAction

internal abstract class ActionExecutor(open val serialName: String) {

    abstract fun executorName(): String

    abstract suspend fun prepareEnvironment()

    abstract suspend fun finishEnvironment()

    abstract suspend fun execute(action: Action, imagePrefix: String): ReportAction
}
