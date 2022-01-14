package fi.epicbot.toster.checker

import fi.epicbot.toster.extension.throwExceptionIfAllBlank
import fi.epicbot.toster.extension.throwExceptionIfAllElementsNotUnique
import fi.epicbot.toster.extension.throwExceptionIfAllNotBlank
import fi.epicbot.toster.extension.throwExceptionIfBlank
import fi.epicbot.toster.extension.throwExceptionIfNotEmptyButBlank
import fi.epicbot.toster.extension.throwExceptionIfOneBlank
import fi.epicbot.toster.model.Screen

internal class ScreensChecker(private val screens: List<Screen>) : Checker {

    override fun check() {
        screens.map { it.name.trim() }.throwExceptionIfAllElementsNotUnique(ALL_SCREEN_NAME_UNIQUE)
        screens.forEach {
            checkScreen(it)
        }
    }

    private fun checkScreen(screen: Screen) {
        screen.run {
            name.throwExceptionIfBlank(EMPTY_SCREEN_NAME)
            listOf(url, shortUrl).run {
                throwExceptionIfAllBlank(EMPTY_URL_NAME)
                throwExceptionIfAllNotBlank(URL_AND_SHORT_URL_ARE_SET)
            }
            shellBefore.throwExceptionIfNotEmptyButBlank(
                BLANK_SHELL_BEFORE_SCREEN
            )
            shellAfter.throwExceptionIfNotEmptyButBlank(
                BLANK_SHELL_AFTER_SCREEN
            )
            permissions.granted.throwExceptionIfOneBlank(EMPTY_GRANTED_PERMISSIONS)
            permissions.revoked.throwExceptionIfOneBlank(EMPTY_REVOKED_PERMISSIONS)
            if (delayAfterOpenMillis <= 0) {
                throw IllegalArgumentException(DELAY_AFTER_TOO_SMALL)
            }
        }
    }

    private companion object {
        private const val EMPTY_SCREEN_NAME = "Screen name shouldn't be empty"
        private const val EMPTY_URL_NAME = "Screen url shouldn't be empty"
        private const val URL_AND_SHORT_URL_ARE_SET = "Choose only <url> or <shortUrl>"
        private const val BLANK_SHELL_BEFORE_SCREEN = "Set non empty shell before"
        private const val BLANK_SHELL_AFTER_SCREEN = "Set non empty shell after"
        private const val DELAY_AFTER_TOO_SMALL = "Delay after too small"
        private const val EMPTY_GRANTED_PERMISSIONS = "All granted permissions shouldn't be empty"
        private const val EMPTY_REVOKED_PERMISSIONS = "All revoked permissions shouldn't be empty"
        private const val ALL_SCREEN_NAME_UNIQUE = "All screen names should be unique"
    }
}
