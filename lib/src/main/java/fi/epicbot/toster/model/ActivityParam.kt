package fi.epicbot.toster.model

sealed class ActivityParam(open val name: String, open val paramValue: String) {
    data class BooleanActivityParam(override val name: String, val value: Boolean) :
        ActivityParam(name, value.toString().lowercase())

    data class FloatActivityParam(override val name: String, val value: Float) :
        ActivityParam(name, value.toString())

    data class IntegerActivityParam(override val name: String, val value: Int) :
        ActivityParam(name, value.toString())

    data class LongActivityParam(override val name: String, val value: Long) :
        ActivityParam(name, value.toString())

    data class StringActivityParam(override val name: String, val value: String) :
        ActivityParam(name, value)
}

internal fun MutableList<ActivityParam>.toStringParams(): String {
    return this.joinToString(separator = "") { param ->
        val type = when (param) {
            is ActivityParam.BooleanActivityParam -> "ez"
            is ActivityParam.FloatActivityParam -> "ef"
            is ActivityParam.IntegerActivityParam -> "ei"
            is ActivityParam.LongActivityParam -> "el"
            is ActivityParam.StringActivityParam -> "es"
        }
        " --$type ${param.name} ${param.paramValue}"
    }
}
