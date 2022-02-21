package fi.epicbot.toster.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

private val activityParamData = mapOf(
    ActivityParam.BooleanActivityParam("bool", true) to (" --ez" to "bool true"),
    ActivityParam.BooleanActivityParam("boolFalse", false) to (" --ez" to "boolFalse false"),
    ActivityParam.FloatActivityParam("float", 4.2f) to (" --ef" to "float 4.2"),
    ActivityParam.IntegerActivityParam("int", 42) to (" --ei" to "int 42"),
    ActivityParam.IntegerActivityParam("int", Int.MAX_VALUE) to (" --ei" to "int 2147483647"),
    ActivityParam.LongActivityParam("long", 42L) to (" --el" to "long 42"),
    ActivityParam.LongActivityParam(
        "long",
        Long.MAX_VALUE
    ) to (" --el" to "long 9223372036854775807"),
    ActivityParam.StringActivityParam("string", "value") to (" --es" to "string value"),
)

class ActivityParamTest : BehaviorSpec({
    activityParamData.forEach { (param, expectedParam) ->
        Given("ActivityParam ${param.name} ${param.paramValue}") {
            When("convert to string params") {
                val actualParam = listOf(param).toStringParams()
                Then("it should be ${expectedParam.first} + ${expectedParam.second}") {
                    actualParam shouldBe expectedParam.first + " " + expectedParam.second
                }
            }
        }
    }
})
