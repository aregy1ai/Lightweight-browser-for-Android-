package com.example.deepexport.data.export

import com.example.deepexport.domain.model.ExportFormat

class ExportFormatterRegistry(
    private val formatters: List<ConversationExportFormatter>
) {
    fun formatter(format: ExportFormat): ConversationExportFormatter {
        return formatters.firstOrNull { it.supports(format) }
            ?: error("No formatter registered for format: $format")
    }

    fun supportedFormats(): List<ExportFormat> {
        return ExportFormat.entries.filter { has(it) }
    }

    fun has(format: ExportFormat): Boolean {
        return formatters.any { it.supports(format) }
    }
}
