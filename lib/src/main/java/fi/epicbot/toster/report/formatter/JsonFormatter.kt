package fi.epicbot.toster.report.formatter

import fi.epicbot.toster.report.model.ReportOutput
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonFormatter(private val prettyPrintJson: Boolean) : ReportFormatter {

    override fun format(reportOutput: ReportOutput): String {
        val formatter = Json {
            prettyPrint = prettyPrintJson
            encodeDefaults = true
        }
        return formatter.encodeToString(reportOutput)
    }
}
