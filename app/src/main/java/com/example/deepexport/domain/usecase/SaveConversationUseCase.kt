package com.example.deepexport.domain.usecase

import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.repository.ConversationRepository

class SaveConversationUseCase(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversation: ChatConversation): AppResult<Unit> {
        return conversationRepository.saveConversation(conversation)
    }
}
