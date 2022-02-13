package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Collage

@TosterDslMarker
class CollageContext {
    internal val collage = Collage()

    fun enable() {
        collage.enabled = true
    }

    fun rows(rows: Int) {
        collage.rows = rows
    }

    fun columns(columns: Int) {
        collage.columns = columns
    }
}
