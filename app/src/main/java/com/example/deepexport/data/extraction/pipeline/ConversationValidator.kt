package com.example.deepexport.data.extraction.pipeline

import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.ChatConversation

object ConversationValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val warnings: List<String> = emptyList()
    )

    fun validate(conversation: ChatConversation): ValidationResult {
        val warnings = mutableListOf<String>()

        if (conversation.messages.isEmpty()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "لم يتم العثور على أي رسائل في الصفحة. تأكد من فتح محادثة كاملة وتسجيل الدخول."
            )
        }

        if (conversation.messages.all { it.content.isBlank() && it.thinkingContent.isNullOrBlank() }) {
            return ValidationResult(
                isValid = false,
                errorMessage = "محتوى الرسائل المستخرجة فارغ."
            )
        }

        val allSameRole = conversation.messages.map { it.role }.distinct().size == 1
        if (allSameRole && conversation.messages.size > 2) {
            warnings.add("جميع الرسائل المستخرجة لها نفس الدور (Role). قد يكون المتصفح لم يميّز بين المستخدم والنموذج.")
        }

        if (conversation.title.isBlank()) {
            warnings.add("عنوان المحادثة غير متوفر، تم وضع عنوان افتراضي.")
        }

        return ValidationResult(
            isValid = true,
            warnings = warnings
        )
    }
}
