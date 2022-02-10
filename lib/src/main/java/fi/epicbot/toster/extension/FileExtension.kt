package fi.epicbot.toster.extension

import java.io.File

@Suppress("UnusedPrivateMember")
internal operator fun File?.plus(s: String): File {
    return File(this.toString() + s)
}
