package fi.epicbot.toster.report.model

import kotlinx.serialization.Serializable

@Serializable
class ReportOutput(
    val appInfo: ReportAppInfo,
    val devices: MutableList<ReportDevice>,
)

@Serializable
class ReportDevice(
    val deviceName: String,
    val reportScreens: List<ReportScreen>,
    val collage: ReportCollage,
)

@Serializable
class ReportCollage

@Serializable
class ReportScreen(
    val name: String,
    val common: MutableList<Common> = mutableListOf(),
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
data class Common(
    override val index: Long,
    override val name: String,
    override val startTime: Long,
    override val endTime: Long,
) : ReportAction()

@Serializable
data class Memory(
    override val index: Long,
    override val name: String,
    override val startTime: Long,
    override val endTime: Long,
    val measurements: Map<String, MemoryCell>,
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
data class MemoryCell(
    val memory: Long,
    val heapSize: Long,
    val heapAlloc: Long,
    val heapFree: Long,
)

@Serializable
class ReportAppInfo(
    val appName: String,
    val testTime: Long,
)
