package fi.epicbot.toster.model

sealed class ActivityParam(open val name: String, open val paramValue: String) {
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
            is ActivityParam.IntegerActivityParam -> "ei"
            is ActivityParam.LongActivityParam -> "el"
            is ActivityParam.StringActivityParam -> "es"
        }
        " --$type ${param.name} ${param.paramValue}"
    }
}
