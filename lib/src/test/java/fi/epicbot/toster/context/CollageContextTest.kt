package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import fi.epicbot.toster.model.Collage
import io.kotest.core.spec.style.BehaviorSpec

private class CollageContextData(
    val name: String,
    val action: CollageContext.() -> Unit,
    val expectedCollage: Collage,
)

private val collageList = listOf(
    CollageContextData(
        "Enable collage",
        {
            enable()
        },
        Collage(enabled = true, rows = 0, columns = 0)
    ),
    CollageContextData(
        "Set rows",
        {
            rows(5)
        },
        Collage(enabled = false, rows = 5, columns = 0)
    ),
    CollageContextData(
        "Set columns",
        {
            columns(8)
        },
        Collage(enabled = false, rows = 0, columns = 8)
    ),
    CollageContextData(
        "Set all",
        {
            enable()
            rows(8)
            columns(10)
        },
        Collage(enabled = true, rows = 8, columns = 10)
    ),
)

internal class CollageContextTest : BehaviorSpec({
    collageList.forEach { collageData ->
        Given("check ${collageData.name}") {
            val collageContext = CollageContext()
            When("Invoke action") {
                collageData.action.invoke(collageContext)
                val actual = collageContext.collage
                val expected = collageData.expectedCollage
                Then("Enabled should be ${expected.enabled}", actual.enabled, expected.enabled)
                Then("Columns should be ${expected.columns}", actual.columns, expected.columns)
                Then("Rows should be ${expected.rows}", actual.rows, expected.rows)
            }
        }
    }
})
