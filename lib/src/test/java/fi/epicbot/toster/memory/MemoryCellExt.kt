package fi.epicbot.toster.memory

import fi.epicbot.toster.report.model.MemoryCell
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.eq.eq
import io.kotest.assertions.errorCollector

infix fun MemoryCell?.cellShouldBe(expected: MemoryCell?) {
    eq(this!!.memory, expected!!.memory)?.let(errorCollector::collectOrThrow)
    eq(this.heapAlloc, expected.heapAlloc)?.let(errorCollector::collectOrThrow)
    eq(this.heapFree, expected.heapFree)?.let(errorCollector::collectOrThrow)
    eq(this.heapSize, expected.heapSize)?.let(errorCollector::collectOrThrow)
}
