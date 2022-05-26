package fi.epicbot.toster.parser

internal class ParserProvider(
    internal val cpuUsageParser: CpuUsageParser = CpuUsageParser(),
    internal val dumpSysParser: DumpSysParser = DumpSysParser(),
    internal val gfxInfoParser: GfxInfoParser = GfxInfoParser(),
)
