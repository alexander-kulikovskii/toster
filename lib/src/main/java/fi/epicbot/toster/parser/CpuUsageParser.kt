package fi.epicbot.toster.parser

import fi.epicbot.toster.report.model.CpuCell

internal class CpuUsageParser {

    @Suppress("ReturnCount")
    fun parse(rawData: String, sampleNumber: Int, coreNumber: Int): CpuCell {
        if (rawData.isBlank()) {
            return EMPTY_CPU_CELL
        }
        return try {
            val cpuLines = rawData
                .split("\n")
                .filter { it.contains("cpu") && it.contains("user") }

            if (cpuLines.size != sampleNumber) {
                return ERROR_CPU_CELL
            }

            val sumCpu = cpuLines.sumOf {
                it.split("\\s+".toRegex())[1].replace("%user", "").toDouble()
            }

            CpuCell(user = sumCpu / (sampleNumber * coreNumber))
        } catch (_: Exception) {
            ERROR_CPU_CELL
        }
    }

    private companion object {
        private val EMPTY_CPU_CELL = CpuCell(user = 0.0)
        private val ERROR_CPU_CELL = CpuCell(user = -1.0)
    }
}
