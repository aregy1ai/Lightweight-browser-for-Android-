package com.example.deepexport.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}

fun String.sanitizeFileName(): String {
    val invalidChars = Regex("[\\\\/:*?\"<>|]")
    val cleaned = this.replace(invalidChars, "_").trim()
    return if (cleaned.isBlank()) "deepseek_chat" else cleaned.take(60)
}
