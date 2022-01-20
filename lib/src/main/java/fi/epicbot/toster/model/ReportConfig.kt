package fi.epicbot.toster.model

import fi.epicbot.toster.report.Reporter

class ReportConfig(
    var enable: Boolean = true,
    var customReporters: MutableList<Reporter> = mutableListOf(),
)
