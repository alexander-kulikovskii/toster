package fi.epicbot.toster.checker

import fi.epicbot.toster.WhenWithException
import fi.epicbot.toster.WhenWithoutException
import fi.epicbot.toster.model.Apk
import io.kotest.core.spec.style.BehaviorSpec

class ApkCheckerPositiveTest : BehaviorSpec({
    correctData.forEach() { inputConfig ->
        Given("ApkChecker with $inputConfig") {
            val configChecker = ApkChecker(inputConfig)
            WhenWithoutException("Check config without any exception") {
                configChecker.check()
            }
        }
    }
})

class ApkCheckerNegativeTest : BehaviorSpec({
    exceptionData.forEach() { (inputConfig, expectedMessage) ->
        Given("ApkChecker with $inputConfig") {
            val configChecker = ApkChecker(inputConfig)
            WhenWithException<IllegalArgumentException>(
                "Check config with exception",
                expectedMessage
            ) {
                configChecker.check()
            }
        }
    }
})

private const val NOT_EMPTY_FIELD = "Test"
private const val EMPTY_APK_LIST = "Apk list shouldn't be empty"
private const val EMPTY_APK_URL = "Apk url shouldn't be empty"
private const val EMPTY_APK_PREFIX = "Apk prefix shouldn't be empty"
private const val NOT_UNIQUE_PREFIXES = "All prefixes should be unique: <Test> - 2 times"
private const val BLANK_SHELL_BEFORE_SCREEN = "Set non empty shell before"

private val correctData = listOf(
    listOf(
        Apk(
            url = NOT_EMPTY_FIELD,
        )
    ),
    listOf(
        Apk(
            url = NOT_EMPTY_FIELD,
            shellsBefore = arrayOf(NOT_EMPTY_FIELD),
        )
    ),
    listOf(
        Apk(
            url = NOT_EMPTY_FIELD,
            shellsBefore = arrayOf(NOT_EMPTY_FIELD),
            prefix = NOT_EMPTY_FIELD,
        )
    ),
    listOf(
        Apk(
            url = NOT_EMPTY_FIELD,
            shellsBefore = arrayOf(NOT_EMPTY_FIELD),
            prefix = NOT_EMPTY_FIELD,
        ),
        Apk(
            url = NOT_EMPTY_FIELD,
            shellsBefore = arrayOf(NOT_EMPTY_FIELD),
            prefix = NOT_EMPTY_FIELD + "2",
        )
    ),
)

private val exceptionData = mapOf(
//    listOf<Apk>() to EMPTY_APK_LIST,
    listOf(
        Apk()
    ) to EMPTY_APK_URL,
    listOf(
        Apk(
            url = NOT_EMPTY_FIELD,
            prefix = "",
        ),
    ) to EMPTY_APK_PREFIX,
    listOf(
        Apk(
            url = NOT_EMPTY_FIELD,
            prefix = NOT_EMPTY_FIELD,
            shellsBefore = arrayOf(" ")
        ),
    ) to BLANK_SHELL_BEFORE_SCREEN,
    listOf(
        Apk(
            url = NOT_EMPTY_FIELD,
            shellsBefore = arrayOf(NOT_EMPTY_FIELD),
            prefix = NOT_EMPTY_FIELD,
        ),
        Apk(
            url = NOT_EMPTY_FIELD,
            shellsBefore = arrayOf(NOT_EMPTY_FIELD),
            prefix = NOT_EMPTY_FIELD,
        )
    ) to NOT_UNIQUE_PREFIXES
)
