package fi.epicbot.toster.checker

import fi.epicbot.toster.WhenWithException
import fi.epicbot.toster.WhenWithoutException
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.Screen
import io.kotest.core.spec.style.BehaviorSpec

class ScreensCheckerPositiveTest : BehaviorSpec({
    correctData.forEach() { inputConfig ->
        Given("ScreensChecker with $inputConfig") {
            val screensChecker = ScreensChecker(inputConfig)
            WhenWithoutException("Check config without any exception") {
                screensChecker.check()
            }
        }
    }
})

class ScreensCheckerNegativeTest : BehaviorSpec({
    exceptionData.forEach() { (inputConfig, expectedMessage) ->
        Given("ScreensChecker with $inputConfig") {
            val screensChecker = ScreensChecker(inputConfig)
            WhenWithException<IllegalArgumentException>(
                "Check config with exception",
                expectedMessage
            ) {
                screensChecker.check()
            }
        }
    }
})

private const val EMPTY_SCREEN_NAME = "Screen name shouldn't be empty"
private const val EMPTY_URL_NAME = "Screen url shouldn't be empty"
private const val URL_AND_SHORT_URL_ARE_SET = "Choose only <url> or <shortUrl>"
private const val BLANK_SHELL_BEFORE_SCREEN = "Set non empty shell before"
private const val BLANK_SHELL_AFTER_SCREEN = "Set non empty shell after"
private const val DELAY_AFTER_TOO_SMALL = "Delay after too small"
private const val EMPTY_GRANTED_PERMISSIONS = "All granted permissions shouldn't be empty"
private const val EMPTY_REVOKED_PERMISSIONS = "All revoked permissions shouldn't be empty"
private const val ALL_SCREEN_NAME_UNIQUE = "All screen names should be unique"
private const val NOT_EMPTY_FIELD = "Test"

private val correctData = listOf(
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
        )
    )
)

private val exceptionData = mapOf(
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
        ),
        Screen(
            name = NOT_EMPTY_FIELD,
        ),
    ) to "$ALL_SCREEN_NAME_UNIQUE: <$NOT_EMPTY_FIELD> - 2 times",
    listOf(
        Screen(
            name = "",
        ),
    ) to EMPTY_SCREEN_NAME,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
        ),
    ) to EMPTY_URL_NAME,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = "",
        ),
    ) to EMPTY_URL_NAME,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            shortUrl = ""
        ),
    ) to EMPTY_URL_NAME,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            shortUrl = NOT_EMPTY_FIELD,
        ),
    ) to URL_AND_SHORT_URL_ARE_SET,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            shellBefore = " ",
        ),
    ) to BLANK_SHELL_BEFORE_SCREEN,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            shellAfter = " ",
        ),
    ) to BLANK_SHELL_AFTER_SCREEN,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            shellAfter = NOT_EMPTY_FIELD,
            permissions = Permissions(granted = listOf(NOT_EMPTY_FIELD, " "))
        ),
    ) to EMPTY_GRANTED_PERMISSIONS,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            shellAfter = NOT_EMPTY_FIELD,
            permissions = Permissions(revoked = listOf(NOT_EMPTY_FIELD, " "))
        ),
    ) to EMPTY_REVOKED_PERMISSIONS,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            delayAfterOpenMillis = 0,
        ),
    ) to DELAY_AFTER_TOO_SMALL,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            delayAfterOpenMillis = -1,
        ),
    ) to DELAY_AFTER_TOO_SMALL,
    listOf(
        Screen(
            name = NOT_EMPTY_FIELD + "1",
            url = NOT_EMPTY_FIELD,
        ),
        Screen(
            name = NOT_EMPTY_FIELD,
            url = NOT_EMPTY_FIELD,
            shellAfter = " ",
        ),
    ) to BLANK_SHELL_AFTER_SCREEN,
)
