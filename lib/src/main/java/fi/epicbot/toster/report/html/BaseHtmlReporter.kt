package fi.epicbot.toster.report.html

import fi.epicbot.toster.executor.ShellExecutor
import fi.epicbot.toster.report.Reporter
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import java.io.InputStream
import java.net.URL

internal abstract class BaseHtmlReporter : Reporter {

    internal fun getGenerateWithHtml() = createHTML().div {
        text("generated with ")
        a("https://github.com/alexander-kulikovskii/toster") {
            text("toster version 0.2.9") // TODO get from constants
        }
    }

    internal fun getTemplate(filePath: String): String {
        return javaClass.getResource("/$filePath")!!
            .openSafeStream()
            .bufferedReader()
            .use { it.readText() }
    }

    internal fun ShellExecutor.makeFileForChart(
        path: String = "",
        fileName: String,
        content: String
    ) {
        this.makeFile("chart/$path", fileName, content)
    }

    private fun URL.openSafeStream(): InputStream {
        return openConnection()
            .apply { useCaches = false }
            .getInputStream()
    }

    internal companion object {

        internal const val DEVICES_PLACEHOLDER = "@@devices@@"
        internal const val APP_NAME_PLACEHOLDER = "@@app_name@@"
        internal const val DEVICE_NAME_PLACEHOLDER = "@@device_name@@"
        internal const val GENERATED_WITH_PLACEHOLDER = "@@generated_with@@"

        internal const val MAIN_TEMPLATE = "main_index.html"
        internal const val DEVICE_TEMPLATE = "device_index.html"
        internal const val STYLE_TEMPLATE = "styles.css"

        internal const val METRICS_HOLDER_VERSION = "@@metrics@@"
        internal const val COLLAGE_HOLDER_VERSION = "@@collage@@"
        internal const val CPU_TEMPLATE = "cpu_index.html"
        internal const val COLLAGE_TEMPLATE = "collage_index.html"
        internal const val MEMORY_TEMPLATE = "memory_index.html"
        internal const val CHART_BUILDER_NAME = "chart_builder.js"
    }
}
