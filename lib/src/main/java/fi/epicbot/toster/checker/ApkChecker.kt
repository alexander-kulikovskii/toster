package fi.epicbot.toster.checker

import fi.epicbot.toster.extension.throwExceptionIfAllElementsNotUnique
import fi.epicbot.toster.extension.throwExceptionIfBlank
import fi.epicbot.toster.extension.throwExceptionIfNotEmptyButBlank
import fi.epicbot.toster.model.Apk

internal class ApkChecker(private val apks: List<Apk>) : Checker {

    override fun check() {
        apks.forEach { apk ->
            checkApk(apk)
        }
        apks.forEach { apk ->
            apk.prefix.throwExceptionIfBlank(EMPTY_APK_PREFIX)
        }
        apks.map { it.prefix.toString() }.throwExceptionIfAllElementsNotUnique(NOT_UNIQUE_PREFIXES)
    }

    private fun checkApk(apk: Apk) {
        apk.run {
            url.throwExceptionIfBlank(EMPTY_APK_URL)
            shellsBefore.forEach {
                it.throwExceptionIfNotEmptyButBlank(
                    BLANK_SHELL_BEFORE_SCREEN
                )
            }
        }
    }

    private companion object {
        private const val EMPTY_APK_LIST = "Apk list shouldn't be empty"
        private const val EMPTY_APK_URL = "Apk url shouldn't be empty"
        private const val EMPTY_APK_PREFIX = "Apk prefix shouldn't be empty"
        private const val NOT_UNIQUE_PREFIXES = "All prefixes should be unique"
        private const val BLANK_SHELL_BEFORE_SCREEN = "Set non empty shell before"
    }
}
