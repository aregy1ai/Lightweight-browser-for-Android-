package com.example.deepexport.domain.repository

import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.ChatConversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    suspend fun saveConversation(conversation: ChatConversation): AppResult<Unit>
    fun getSavedConversations(): Flow<List<ChatConversation>>
    fun searchConversations(query: String): Flow<List<ChatConversation>>
    suspend fun getConversationById(id: String): AppResult<ChatConversation?>
    suspend fun deleteConversation(id: String): AppResult<Unit>
}
