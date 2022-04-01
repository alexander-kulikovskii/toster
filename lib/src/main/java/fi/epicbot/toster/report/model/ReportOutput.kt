package fi.epicbot.toster.report.model

import kotlinx.serialization.Serializable

@Serializable
class ReportOutput(
    val appInfo: ReportAppInfo,
    val devices: MutableList<ReportDevice>,
)

@Serializable
class Device(
    val type: String,
    val name: String,
) {
    override fun toString(): String = "$type <$name>"
}

@Serializable
class ReportDevice(
    val device: Device,
    val reportScreens: List<ReportScreen>,
    val collage: ReportCollage,
)

@Serializable
class ReportCollage

@Serializable
class ReportScreen(
    val name: String,
    val common: MutableList<Common> = mutableListOf(),
    val gfxInfo: MutableList<GfxInfo> = mutableListOf(),
    val cpuUsage: MutableList<CpuUsage> = mutableListOf(),
    val memory: MutableList<Memory> = mutableListOf(),
    val screenshots: MutableList<Screenshot> = mutableListOf(),
)

@Serializable
sealed class ReportAction {
    abstract val index: Long
    abstract val name: String
    abstract val startTime: Long
    abstract val endTime: Long
}

@Serializable
class Common(
    override val index: Long,
    override val name: String,
    override val startTime: Long,
    override val endTime: Long,
) : ReportAction()

@Serializable
class Memory(
    override val index: Long,
    override val name: String,
    override val startTime: Long,
    override val endTime: Long,
    val measurements: Map<String, MemoryCell>,
) : ReportAction()

@Serializable
class GfxInfo(
    override val index: Long,
    override val name: String,
    override val startTime: Long,
    override val endTime: Long,
    val measurements: Map<String, Double>,
) : ReportAction()

@Serializable
class CpuUsage(
    override val index: Long,
    override val name: String,
    override val startTime: Long,
    override val endTime: Long,
    val measurement: CpuCell,
) : ReportAction()

@Serializable
data class Screenshot(
    override val index: Long,
    override val name: String,
    override val startTime: Long,
    override val endTime: Long,
    val prefix: String,
    val pathUrl: String,
) : ReportAction()

@Serializable
class MemoryCell(
    val memory: Long,
    val heapSize: Long,
    val heapAlloc: Long,
    val heapFree: Long,
)

@Serializable
class CpuCell(
    val user: Double,
)

@Serializable
class ReportAppInfo(
    val appName: String,
    val testTime: Long,
)
