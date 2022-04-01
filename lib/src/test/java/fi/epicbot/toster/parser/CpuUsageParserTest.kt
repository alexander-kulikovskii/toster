package fi.epicbot.toster.parser

import fi.epicbot.toster.report.model.CpuCell
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CpuUsageParserTest : BehaviorSpec({
    Given("CpuUsageParser") {
        val parser = CpuUsageParser()
        testMap.forEach { (inputModel, expectedAnswer) ->
            When("Parse raw data <${inputModel.rawData}>") {
                val actual = parser.parse(
                    inputModel.rawData,
                    coreNumber = inputModel.coreNumber,
                    sampleNumber = inputModel.sampleNumber
                )
                Then("user should be (${expectedAnswer.user})") {
                    actual.user shouldBe expectedAnswer.user
                }
            }
        }
    }
})

private class CpuTestData(
    val rawData: String,
    val coreNumber: Int,
    val sampleNumber: Int,
)

private val EMPTY_CELL = CpuCell(0.0)
private val ERROR_CELL = CpuCell(-1.0)

private val OneLineRawData = """
[H[JTasks: 1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
  Mem:  7647796K total,  7561192K used,    86604K free,  4045060K buffers
 Swap:  3145724K total,   366244K used,  2779480K free,   539376K cached
800%cpu 150%user   0%nice 150%sys 490%idle   0%iow   0%irq  10%sirq   0%host
[7m  PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS            [0m
 9273 u0_a248      10 -10  16G 282M 131M S  0.0   3.7  72:01.28 fi.epicbot.text
""".trimIndent()

private val WrongRawData = """
[H[JTasks: 1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
  Mem:  7647796K total,  7561192K used,    86604K free,  4045060K buffers
 Swap:  3145724K total,   366244K used,  2779480K free,   539376K cached
800%cpu 150user   0%nice 150%sys 490%idle   0%iow   0%irq  10%sirq   0%host
[7m  PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS            [0m
 9273 u0_a248      10 -10  16G 282M 131M S  0.0   3.7  72:01.28 fi.epicbot.text
""".trimIndent()

@Suppress("MaxLineLength")
private val FullRawData = """
[s[999C[999B[6n[u[H[J[?25l[H[J[s[999C[999B[6n[uTasks: 1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
  Mem:  7647796K total,  7559944K used,    87852K free,  4043864K buffers
 Swap:  3145724K total,   366244K used,  2779480K free,   539432K cached
800%cpu 119%user   4%nice  56%sys 615%idle   4%iow   4%irq   0%sirq   0%host
[7m  PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS            [0m
 9273 u0_a248      10 -10  16G 282M 131M S 11.1   3.7  72:01.25 fi.epicbot.text
[H[JTasks: 1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
  Mem:  7647796K total,  7560156K used,    87640K free,  4043876K buffers
 Swap:  3145724K total,   366244K used,  2779480K free,   539420K cached
800%cpu 100%user   0%nice  80%sys 620%idle   0%iow   0%irq   0%sirq   0%host
[7m  PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS            [0m
 9273 u0_a248      10 -10  16G 282M 131M S  0.0   3.7  72:01.28 fi.epicbot.text
[H[JTasks: 1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
  Mem:  7647796K total,  7560160K used,    87636K free,  4043916K buffers
 Swap:  3145724K total,   366244K used,  2779480K free,   539380K cached
800%cpu 120%user   0%nice  50%sys 620%idle   0%iow   0%irq  10%sirq   0%host
[7m  PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS            [0m
 9273 u0_a248      10 -10  16G 282M 131M S  0.0   3.7  72:01.28 fi.epicbot.text
[H[JTasks: 1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
  Mem:  7647796K total,  7561144K used,    86652K free,  4045060K buffers
 Swap:  3145724K total,   366244K used,  2779480K free,   539376K cached
800%cpu 150%user   0%nice  50%sys 600%idle   0%iow   0%irq   0%sirq   0%host
[7m  PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS            [0m
 9273 u0_a248      10 -10  16G 282M 131M S  0.0   3.7  72:01.28 fi.epicbot.text
[H[JTasks: 1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
  Mem:  7647796K total,  7561144K used,    86652K free,  4045060K buffers
 Swap:  3145724K total,   366244K used,  2779480K free,   539376K cached
800%cpu 1000%user   0%nice 100%sys 570%idle   0%iow  10%irq   0%sirq   0%host
[7m  PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS            [0m
 9273 u0_a248      10 -10  16G 282M 131M S  0.0   3.7  72:01.28 fi.epicbot.text
[?25h[0m[1000;1H[K[?25h[?25h[0m[1000;1H[K
""".trimIndent()

private val testMap = mapOf(
    CpuTestData("", 1, 5) to EMPTY_CELL,
    CpuTestData(OneLineRawData, 1, 5) to ERROR_CELL,
    CpuTestData(WrongRawData, 2, 1) to ERROR_CELL,
    CpuTestData(FullRawData, 8, 5) to CpuCell(37.225),
)
