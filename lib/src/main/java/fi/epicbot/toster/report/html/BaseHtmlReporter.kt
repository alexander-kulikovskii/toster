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
            text("toster version $LIB_VERSION") // TODO get from constants
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

    @Suppress("MagicNumber", "ComplexMethod")
    internal fun getColorByIndex(index: Int, transparent: Boolean = false): String {
        val color = when (index % COLOR_SIZE) {
            0 -> "255, 95, 95"
            6 -> "143, 71, 71"
            12 -> "255, 168, 168"

            1 -> "255, 177, 86"
            7 -> "182, 144, 101"
            13 -> "255, 199, 0"

            2 -> "103, 237, 116"
            8 -> "35, 189, 97"
            14 -> "82, 143, 88"

            3 -> "19, 211, 200"
            9 -> "122, 174, 171"
            15 -> "58, 143, 138"

            4 -> "90, 117, 255"
            10 -> "118, 124, 160"
            16 -> "127, 38, 216"

            5 -> "219, 56, 203"
            11 -> "136, 87, 131"
            17 -> "184, 32, 114"
            else -> "73, 128, 135"
        }
        return if (transparent) {
            "rgba($color, $CHART_TRANSPARENT_VALUE)"
        } else {
            "rgb($color)"
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

        internal const val LIB_VERSION = "0.3.2"
        internal const val CHART_VERSION = "3.9.1"

        private const val COLOR_SIZE = 18
        private const val CHART_TRANSPARENT_VALUE = 0.8
    }
}
