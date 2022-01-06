package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.ActivityParam

@TosterDslMarker
class ActivityParamContext {
    internal val activityParams: MutableList<ActivityParam> = mutableListOf()

    fun integer(name: String, value: Int) {
        activityParams.add(ActivityParam.IntegerActivityParam(name, value))
    }

    fun long(name: String, value: Long) {
        activityParams.add(ActivityParam.LongActivityParam(name, value))
    }

    fun string(name: String, value: String) {
        activityParams.add(ActivityParam.StringActivityParam(name, value))
    }
}
