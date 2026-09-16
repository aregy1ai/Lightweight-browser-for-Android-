package com.example.deepexport.domain.model

enum class ExportFormat(val extension: String, val mimeType: String, val displayName: String) {
    TXT("txt", "text/plain", "نص عادي (TXT)"),
    MARKDOWN("md", "text/markdown", "ماركداون (Markdown)"),
    JSON("json", "application/json", "بيانات منظمة (JSON)")
}
