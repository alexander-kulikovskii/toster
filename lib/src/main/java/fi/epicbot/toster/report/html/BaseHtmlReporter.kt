package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.report.Reporter
import fi.epicbot.toster.report.model.ReportDevice
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.stream.createHTML

internal abstract class BaseHtmlReporter : Reporter {

    internal fun getGenerateWithHtml() = createHTML().div {
        text("generated with ")
        a("https://github.com/alexander-kulikovskii/toster") {
            text("toster version 0.2.9") // TODO get from constants
        }
    }

    internal fun getTemplate(filePath: String): String {
        return when (filePath) {
            MAIN_TEMPLATE_NAME -> MAIN_TEMPLATE
            DEVICE_TEMPLATE_NAME -> DEVICE_TEMPLATE
            COLLAGE_TEMPLATE_NAME -> COLLAGE_TEMPLATE
            STYLE_TEMPLATE_NAME -> STYLE_TEMPLATE
            CPU_TEMPLATE_NAME -> CPU_TEMPLATE
            MEMORY_TEMPLATE_NAME -> MEMORY_TEMPLATE
            else -> throw IllegalArgumentException("Unsupported template $filePath")
        }
    }

    internal fun ShellExecutor.makeFileForChart(
        path: String = "",
        fileName: String,
        content: String
    ) {
        this.makeFile("chart/$path", fileName, content)
    }

    @Suppress("MagicNumber")
    internal fun getColorByIndex(index: Int, transparent: Boolean = false): String {
        val color = when (index % COLOR_SIZE) {
            0 -> "73, 128, 135"
            1 -> "148, 203, 170"
            2 -> "42, 53, 10"
            3 -> "148, 253, 50"
            else -> "73, 128, 135"
        }
        return if (transparent) {
            "rgb($color)"
        } else {
            "rgba($color, $CHART_TRANSPARENT_VALUE)"
        }
    }

    internal fun ReportDevice.userScreens() = reportScreens.dropLast(1).drop(1)

    internal companion object {

        internal const val DEVICES_PLACEHOLDER = "@@devices@@"
        internal const val APP_NAME_PLACEHOLDER = "@@app_name@@"
        internal const val DEVICE_NAME_PLACEHOLDER = "@@device_name@@"
        internal const val GENERATED_WITH_PLACEHOLDER = "@@generated_with@@"

        internal const val MAIN_TEMPLATE_NAME = "main_index.html"
        internal const val DEVICE_TEMPLATE_NAME = "device_index.html"
        internal const val STYLE_TEMPLATE_NAME = "styles.css"

        internal const val METRICS_HOLDER_VERSION = "@@metrics@@"
        internal const val COLLAGE_HOLDER_VERSION = "@@collage@@"
        internal const val CPU_TEMPLATE_NAME = "cpu_index.html"
        internal const val COLLAGE_TEMPLATE_NAME = "collage_index.html"
        internal const val MEMORY_TEMPLATE_NAME = "memory_index.html"
        internal const val CHART_BUILDER_NAME = "chart_builder.js"

        internal const val FILL_CHART = false
        internal const val CHART_HEIGHT = "80"
        internal const val DEFAULT_SCREENSHOT_HEIGHT = "500"
        internal const val MILLIS_IN_SECOND = 1000.0

        private const val COLOR_SIZE = 20
        private const val CHART_TRANSPARENT_VALUE = 0.8
    }
}
