package fi.epicbot.toster.parser

import fi.epicbot.toster.report.model.CpuCell

internal class CpuUsageParser {

    fun parse(rawData: String, apkPackage: String): CpuCell {
        if (rawData.isBlank()) {
            return EMPTY_CPU_CELL
        }
        return try {
            val tmpIndex = rawData.indexOf(apkPackage) + apkPackage.length + 2
            val tmp = rawData.substring(tmpIndex)
            val userPercentIndexEnd = tmp.indexOf("%")

            val kernelPercentIndexStart = tmp.indexOf("+", startIndex = userPercentIndexEnd + 1) + 2
            val kernelPercentIndexEnd = tmp.indexOf("%", startIndex = userPercentIndexEnd + 1)

            CpuCell(
                user = tmp.substring(0, userPercentIndexEnd).toFloat(),
                kernel = tmp.substring(kernelPercentIndexStart, kernelPercentIndexEnd).toFloat()
            )
        } catch (_: Exception) {
            EMPTY_CPU_CELL
        }
    }

    private companion object {
        private val EMPTY_CPU_CELL = CpuCell(user = 0f, kernel = 0f)
    }
}
