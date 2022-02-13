package fi.epicbot.toster.context

import fi.epicbot.toster.TosterDslMarker
import fi.epicbot.toster.model.ReportConfig
import fi.epicbot.toster.report.Reporter

@TosterDslMarker
class ReportContext {
    internal val report = ReportConfig()

    fun disable() {
        report.enable = false
    }

    fun addCustomReporter(reporter: Reporter) {
        report.customReporters.add(reporter)
    }
}
