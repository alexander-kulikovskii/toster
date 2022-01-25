package fi.epicbot.toster.memory

import fi.epicbot.toster.report.model.MemoryCell
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

private val rawData = """
** MEMINFO in pid 18227 [com.google.android.apps.maps] **
                   Pss  Private  Private  Swapped     Heap     Heap     Heap
                 Total    Dirty    Clean    Dirty     Size    Alloc     Free
                ------   ------   ------   ------   ------   ------   ------
  Native Heap    10468    10408        0        0    20480    14462     6017
  Dalvik Heap    34340    33816        0        0    62436    53883     8553
 Dalvik Other      972      972        0        0
        Stack     1144     1144        0        0
      Gfx dev    35300    35300        0        0
    Other dev        5        0        4        0
     .so mmap     1943      504      188        0
    .apk mmap      598        0      136        0
    .ttf mmap      134        0       68        0
    .dex mmap     3908        0     3904        0
    .oat mmap     1344        0       56        0
    .art mmap     2037     1784       28        0
   Other mmap       30        4        0        0
   EGL mtrack    73072    73072        0        0
    GL mtrack    51044    51044        0        0
      Unknown      185      184        0        0
        TOTAL   216524   208232     4384        0    82916    68345    14570

 Dalvik Details
        .Heap     6568     6568        0        0
         .LOS    24771    24404        0        0
          .GC      500      500        0        0
    .JITCache      428      428        0        0
      .Zygote     1093      936        0        0
   .NonMoving     1908     1908        0        0
 .IndirectRef       44       44        0        0

 Objects
               Views:       90         ViewRootImpl:        1
         AppContexts:        4           Activities:        1
              Assets:        2        AssetManagers:        2
       Local Binders:       21        Proxy Binders:       28
       Parcel memory:       18         Parcel count:       74
    Death Recipients:        2      OpenSSL Sockets:        2
""".trimIndent()

class DumpSysParserTest : BehaviorSpec({
    Given("DumpSysParser") {
        val parser = DumpSysParser()
        When("Parse empty memory info") {
            val map = parser.parse("")
            Then("map size should be 0") {
                map.size shouldBe 0
            }
        }
        When("parse data") {
            val map = parser.parse(rawData)
            Then("map size should be 3") {
                map.size shouldBe 3
            }
            Then("Check Native Heap") {
                map["Native Heap"] cellShouldBe MemoryCell(
                    memory = 10468,
                    heapSize = 20480,
                    heapAlloc = 14462,
                    heapFree = 6017,
                )
            }
            Then("Check Dalvik Heap") {
                map["Dalvik Heap"] cellShouldBe MemoryCell(
                    memory = 34340,
                    heapSize = 62436,
                    heapAlloc = 53883,
                    heapFree = 8553,
                )
            }
            Then("Check TOTAL") {
                map["TOTAL"] cellShouldBe MemoryCell(
                    memory = 216524,
                    heapSize = 82916,
                    heapAlloc = 68345,
                    heapFree = 14570,
                )
            }
        }
    }
})
