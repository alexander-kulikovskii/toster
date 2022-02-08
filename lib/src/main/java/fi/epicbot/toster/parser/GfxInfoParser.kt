package fi.epicbot.toster.parser

internal class GfxInfoParser {

    fun parse(rawMemoryInfo: String): Map<String, Double> {
        val findParams = GFX_PARAM_LIST
        val result: HashMap<String, Double> = hashMapOf()
        findParams.forEach { paramToFind ->
            addMeasurement(result, rawMemoryInfo, paramToFind)
        }
        return result
    }

    private fun addMeasurement(
        result: HashMap<String, Double>,
        rawMemoryInfo: String,
        paramToFind: String,
    ) {
        val line = rawMemoryInfo.findLine(paramToFind) ?: return

        val infoName = paramToFind.prepareName()

        if (paramToFind in GFX_PARAM_LIST_WITH_PERCENT) {
            val combinedData = line.second.split(" (")
            result[infoName] = combinedData[0].toDouble()
            result[infoName + PERCENT_TITLE] = combinedData[1].replace("%)", "").toDouble()
        } else {
            val (key, value) = if (MS_TITLE in line.second) {
                infoName + MS_UPPER_TITLE to line.second.replace(MS_TITLE, "")
            } else {
                infoName to line.second
            }
            result[key] = value.toDouble()
        }
    }

    private fun String.prepareName(): String {
        val rawInfoName = this
            .replace("(", " ")
            .replace(")", "")
            .capitalizeWords()
        return rawInfoName[0].toLowerCase() + rawInfoName.substring(1)
    }

    private fun String.findLine(name: String): Pair<String, String>? {
        return try {
            val start = this.indexOf(name)
            val end = this.indexOf("\n", start + 1)
            val line = this.substring(start, end).split(": ")
            line[0] to line[1]
        } catch (_: Exception) {
            null
        }
    }

    private fun String.capitalizeWords() =
        this.toLowerCase().split(" ").joinToString("") { it.capitalize() }

    private companion object {
        private const val PERCENT_TITLE = "Percent"
        private const val MS_TITLE = "ms"
        private const val MS_UPPER_TITLE = "Ms"

        private val GFX_PARAM_LIST_WITH_PERCENT = setOf(
            "Janky frames",
            "Janky frames (legacy)",
        )

        private val GFX_PARAM_LIST = listOf(
            "Total frames rendered",
            "Janky frames",
            "Janky frames (legacy)",
            "50th percentile",
            "90th percentile",
            "95th percentile",
            "99th percentile",
            "Number Missed Vsync",
            "Number High input latency",
            "Number Slow UI thread",
            "Number Slow bitmap uploads",
            "Number Slow issue draw commands",
            "Number Frame deadline missed",
            "Number Frame deadline missed (legacy)",
        )
    }
}
