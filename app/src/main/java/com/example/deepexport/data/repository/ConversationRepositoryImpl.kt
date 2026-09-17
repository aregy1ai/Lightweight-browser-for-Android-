package com.example.deepexport.data.repository

import com.example.deepexport.core.AppResult
import com.example.deepexport.data.local.dao.ConversationDao
import com.example.deepexport.data.local.entity.ConversationEntity
import com.example.deepexport.data.local.entity.MessageEntity
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import com.example.deepexport.domain.model.Platform
import com.example.deepexport.domain.repository.ConversationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ConversationRepositoryImpl(
    private val conversationDao: ConversationDao
) : ConversationRepository {

    override suspend fun saveConversation(conversation: ChatConversation): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val jsonArray = JSONArray()
            val messageEntities = mutableListOf<MessageEntity>()

            conversation.messages.forEachIndexed { index, msg ->
                val obj = JSONObject()
                obj.put("id", msg.id)
                obj.put("role", msg.role.name)
                obj.put("content", msg.content)
                obj.put("thinking", msg.thinkingContent ?: "")
                obj.put("model", msg.model ?: "")
                obj.put("timestamp", msg.timestamp ?: 0L)
                jsonArray.put(obj)

                messageEntities.add(
                    MessageEntity(
                        id = msg.id,
                        conversationId = conversation.id,
                        role = msg.role.name,
                        content = msg.content,
                        thinkingContent = msg.thinkingContent,
                        model = msg.model,
                        timestamp = msg.timestamp,
                        orderIndex = index
                    )
                )
            }

            val entity = ConversationEntity(
                id = conversation.id,
                title = conversation.title,
                sourceUrl = conversation.sourceUrl,
                platform = conversation.platform.name,
                messageCount = conversation.messages.size,
                rawJsonMessages = jsonArray.toString(),
                createdAt = conversation.createdAt,
                updatedAt = System.currentTimeMillis()
            )

            conversationDao.insertConversation(entity)
            conversationDao.deleteMessagesForConversation(conversation.id)
            conversationDao.insertMessages(messageEntities)

            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, "فشل حفظ المحادثة في قاعدة البيانات: ${e.localizedMessage}")
        }
    }

    override fun getSavedConversations(): Flow<List<ChatConversation>> {
        return conversationDao.getAllConversations().map { list ->
            list.map { mapEntityToConversation(it) }
        }
    }

    override fun searchConversations(query: String): Flow<List<ChatConversation>> {
        return conversationDao.searchConversations(query).map { list ->
            list.map { mapEntityToConversation(it) }
        }
    }

    override suspend fun getConversationById(id: String): AppResult<ChatConversation?> = withContext(Dispatchers.IO) {
        try {
            val entity = conversationDao.getConversationById(id)
            if (entity == null) {
                AppResult.Success(null)
            } else {
                AppResult.Success(mapEntityToConversation(entity))
            }
        } catch (e: Exception) {
            AppResult.Error(e, "فشل استرجاع المحادثة: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteConversation(id: String): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            conversationDao.deleteConversationById(id)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, "فشل حذف المحادثة: ${e.localizedMessage}")
        }
    }

    private fun mapEntityToConversation(entity: ConversationEntity): ChatConversation {
        val messages = mutableListOf<ChatMessage>()
        try {
            val arr = JSONArray(entity.rawJsonMessages)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val roleStr = obj.optString("role")
                val role = MessageRole.fromString(roleStr)
                messages.add(
                    ChatMessage(
                        id = obj.optString("id", "${entity.id}_msg_$i"),
                        role = role,
                        content = obj.optString("content"),
                        thinkingContent = obj.optString("thinking").takeIf { it.isNotBlank() },
                        model = obj.optString("model").takeIf { it.isNotBlank() },
                        timestamp = obj.optLong("timestamp").takeIf { it != 0L }
                    )
                )
            }
        } catch (_: Exception) {}

        val platform = try {
            Platform.valueOf(entity.platform)
        } catch (_: Exception) {
            Platform.fromUrl(entity.sourceUrl)
        }

        return ChatConversation(
            id = entity.id,
            title = entity.title,
            sourceUrl = entity.sourceUrl,
            platform = platform,
            messages = messages,
            createdAt = entity.createdAt
        )
    }
}
