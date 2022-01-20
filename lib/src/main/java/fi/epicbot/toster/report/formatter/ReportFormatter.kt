package fi.epicbot.toster.report.formatter

import fi.epicbot.toster.report.model.ReportOutput

interface ReportFormatter {

    fun format(reportOutput: ReportOutput): String
}
