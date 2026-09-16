package com.example.deepexport.domain.model

enum class MessageRole(val label: String) {
    User("المستخدم"),
    Assistant("DeepSeek"),
    System("النظام");

    companion object {
        fun fromString(value: String): MessageRole {
            val lower = value.lowercase().trim()
            return when {
                lower.contains("user") || lower.contains("human") -> User
                lower.contains("system") -> System
                else -> Assistant
            }
        }
    }
}
