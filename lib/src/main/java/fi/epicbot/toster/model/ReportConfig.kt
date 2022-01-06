package fi.epicbot.toster.model

import fi.epicbot.toster.report.DefaultReporter
import fi.epicbot.toster.report.Reporter

class ReportConfig(
    var enable: Boolean = true,
    var reporters: MutableList<Reporter> = mutableListOf(DefaultReporter()),
)
