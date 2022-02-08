package fi.epicbot.toster.parser

import fi.epicbot.toster.extension.findRow
import fi.epicbot.toster.report.model.MemoryCell

internal class DumpSysParser {

    fun parse(rawMemoryInfo: String): Map<String, MemoryCell> {
        val findParams = DEFAULT_MEMORY_PARAM_LIST
        val result: HashMap<String, MemoryCell> = hashMapOf()
        findParams.forEach { paramToFind ->
            val memoryRow = rawMemoryInfo.findRow(paramToFind)
            if (memoryRow.isNotEmpty()) {
                result[paramToFind] = memoryRow.toMemoryCell()
            }
        }
        return result
    }

    // https://developer.android.com/studio/command-line/dumpsys
    private fun List<String>.toMemoryCell(): MemoryCell {
        return MemoryCell(
            memory = this.getLongAndTrim(MEMORY_INDEX),
            heapSize = this.getLongAndTrim(HEAP_SIZE_INDEX),
            heapAlloc = this.getLongAndTrim(HEAP_ALLOC_INDEX),
            heapFree = this.getLastLongAndTrim(HEAP_FREE_INDEX),
        )
    }

    private fun List<String>.getLongAndTrim(index: Int): Long {
        return get(index).trim().toLong()
    }

    private fun List<String>.getLastLongAndTrim(index: Int): Long {
        return get(index).trim().split("\n")[0].toLong()
    }

    private companion object {
        private val DEFAULT_MEMORY_PARAM_LIST = listOf("Native Heap", "Dalvik Heap", "TOTAL")
        private const val MEMORY_INDEX = 0
        private const val HEAP_SIZE_INDEX = 4
        private const val HEAP_ALLOC_INDEX = 5
        private const val HEAP_FREE_INDEX = 6
    }
}
