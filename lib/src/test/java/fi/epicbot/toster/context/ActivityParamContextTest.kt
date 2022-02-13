package fi.epicbot.toster.context

import fi.epicbot.toster.model.ActivityParam
import fi.epicbot.toster.model.toStringParams
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

private class ActivityParamContextData(
    val name: String,
    val action: ActivityParamContext.() -> Unit,
    val expectedList: List<ActivityParam>,
)

private val activityParamList = listOf(
    ActivityParamContextData(
        "Boolean true",
        {
            boolean("bool", true)
        },
        listOf(
            ActivityParam.BooleanActivityParam("bool", true),
        )
    ),
    ActivityParamContextData(
        "Boolean several values",
        {
            boolean("bool", true)
            boolean("bool2", false)
        },
        listOf(
            ActivityParam.BooleanActivityParam("bool", true),
            ActivityParam.BooleanActivityParam("bool2", false),
        )
    ),
    ActivityParamContextData(
        "Float",
        {
            float("float", 4.2f)
        },
        listOf(
            ActivityParam.FloatActivityParam("float", 4.2f),
        )
    ),
    ActivityParamContextData(
        "Integer",
        {
            integer("integer", 42)
        },
        listOf(
            ActivityParam.IntegerActivityParam("integer", 42),
        )
    ),
    ActivityParamContextData(
        "Long",
        {
            long("long", 41L)
        },
        listOf(
            ActivityParam.LongActivityParam("long", 41L),
        )
    ),
    ActivityParamContextData(
        "String",
        {
            string("string", "42")
        },
        listOf(
            ActivityParam.StringActivityParam("string", "42"),
        )
    ),
    ActivityParamContextData(
        "All params",
        {
            string("string", "42")
            boolean("bool", false)
            integer("int", 42)
            long("long", 42L)
            float("float", 4.2f)
        },
        listOf(
            ActivityParam.StringActivityParam("string", "42"),
            ActivityParam.BooleanActivityParam("bool", false),
            ActivityParam.IntegerActivityParam("int", 42),
            ActivityParam.LongActivityParam("long", 42L),
            ActivityParam.FloatActivityParam("float", 4.2f),
        )
    ),
)

internal class ActivityParamContextTest : BehaviorSpec({
    activityParamList.forEach { activityParamData ->
        Given("check ${activityParamData.name}") {
            val actionContext = ActivityParamContext()
            When("Invoke action") {
                activityParamData.action.invoke(actionContext)
                Then("Activity param list should be ${activityParamData.expectedList.toStringParams()}") {
                    val actual = actionContext.activityParams.toStringParams()
                    val expected = activityParamData.expectedList.toStringParams()
                    actual shouldBe expected
                }
            }
        }
    }
})
