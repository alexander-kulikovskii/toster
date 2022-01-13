package fi.epicbot.toster.executor

import fi.epicbot.toster.model.Action
import fi.epicbot.toster.report.model.ReportAction

internal interface ActionExecutor {

    fun executorName(): String

    suspend fun prepareEnvironment()

    suspend fun finishEnvironment()

    suspend fun execute(action: Action, imagePrefix: String): ReportAction
}
