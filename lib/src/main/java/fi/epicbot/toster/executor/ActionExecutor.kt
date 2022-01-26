package fi.epicbot.toster.executor

import fi.epicbot.toster.model.Action
import fi.epicbot.toster.report.model.Device
import fi.epicbot.toster.report.model.ReportAction

internal interface ActionExecutor {

    fun executor(): Device

    suspend fun prepareEnvironment()

    suspend fun finishEnvironment()

    suspend fun execute(action: Action, imagePrefix: String): ReportAction
}
