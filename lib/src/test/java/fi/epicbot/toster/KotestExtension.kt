package fi.epicbot.toster

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerContext
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerContext
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.mockk.MockKVerificationScope
import io.mockk.Ordering
import io.mockk.verify

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

suspend fun BehaviorSpecWhenContainerContext.Then(
    name: String,
    expectedValue: Any?,
    actualValue: Any?,
) = Then(name) {
    expectedValue shouldBe actualValue
}

suspend inline fun <reified T : Any> BehaviorSpecWhenContainerContext.ThenInstanseOf(
    name: String,
    expectedValue: Any?,
) = Then(name) {
    expectedValue.shouldBeInstanceOf<T>()
}

@Suppress("LongParameterList")
suspend fun BehaviorSpecWhenContainerContext.Verify(
    name: String,
    ordering: Ordering = Ordering.UNORDERED,
    inverse: Boolean = false,
    atLeast: Int = 1,
    atMost: Int = Int.MAX_VALUE,
    exactly: Int = -1,
    timeoutMillis: Long = 0,
    verifyBlock: MockKVerificationScope.() -> Unit,
) = Then(name) {
    verify(
        ordering = ordering,
        inverse = inverse,
        atLeast = atLeast,
        atMost = atMost,
        exactly = exactly,
        timeout = timeoutMillis,
        verifyBlock = verifyBlock,
    )
}
