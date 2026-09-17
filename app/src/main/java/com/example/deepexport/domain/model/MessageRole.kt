package com.example.deepexport.domain.model

enum class MessageRole(val label: String) {
    User("المستخدم"),
    Assistant("المساعد الذكي"),
    System("النظام"),
    Unknown("غير محدد");

    companion object {
        fun fromString(value: String): MessageRole {
            val lower = value.lowercase().trim()
            return when {
                lower.contains("user") || lower.contains("human") -> User
                lower.contains("system") -> System
                lower.contains("assistant") || lower.contains("bot") || lower.contains("model") || lower.contains("deepseek") || lower.contains("chatgpt") || lower.contains("claude") || lower.contains("gemini") -> Assistant
                else -> Unknown
            }
        }
    }
}
