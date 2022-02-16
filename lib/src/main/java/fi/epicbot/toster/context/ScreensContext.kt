package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.Screen

@TosterDslMarker
class ScreensContext {

    internal val screens = mutableListOf<Screen>()

    fun screen(init: ScreenContext.() -> Unit) {
        val context = ScreenContext().apply(init)
        screens.add(context.screen)
    }
}
