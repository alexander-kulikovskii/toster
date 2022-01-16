package fi.epicbot.toster

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerContext
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerContext
import io.kotlintest.shouldBe

@Suppress("FunctionName")
internal suspend inline fun <reified T : Throwable> BehaviorSpecGivenContainerContext.WhenWithException(
    name: String,
    expectedMessage: String,
    crossinline test: suspend BehaviorSpecWhenContainerContext.() -> Unit
) = When(name) {
    val exception = shouldThrow<T> {
        test.invoke(this)
    }
    Then("It should throw exception with message $expectedMessage") {
        exception.message shouldBe expectedMessage
    }
}

@Suppress("FunctionName")
internal suspend inline fun BehaviorSpecGivenContainerContext.WhenWithoutException(
    name: String,
    crossinline test: suspend BehaviorSpecWhenContainerContext.() -> Unit
) = When(name) {
    shouldNotThrow<Throwable> {
        test.invoke(this)
    }
    Then("It shouldn't throw any exception") {
    }
}
