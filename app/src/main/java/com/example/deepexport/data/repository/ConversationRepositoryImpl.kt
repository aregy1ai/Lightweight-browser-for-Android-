package com.example.deepexport.data.repository

import com.example.deepexport.core.AppResult
import com.example.deepexport.data.local.dao.ConversationDao
import com.example.deepexport.data.local.entity.ConversationEntity
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import com.example.deepexport.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ConversationRepositoryImpl(
    private val conversationDao: ConversationDao
) : ConversationRepository {

    override suspend fun saveConversation(conversation: ChatConversation): AppResult<Unit> {
        return try {
            val jsonArray = JSONArray()
            conversation.messages.forEach { msg ->
                val obj = JSONObject()
                obj.put("role", msg.role.name)
                obj.put("content", msg.content)
                obj.put("thinking", msg.thinkingContent ?: "")
                obj.put("model", msg.model ?: "")
                obj.put("timestamp", msg.timestamp ?: 0L)
                jsonArray.put(obj)
            }

            val entity = ConversationEntity(
                id = "${conversation.title.hashCode()}_${conversation.createdAt}",
                title = conversation.title,
                sourceUrl = conversation.sourceUrl,
                rawJsonMessages = jsonArray.toString(),
                messageCount = conversation.messages.size,
                createdAt = conversation.createdAt
            )
            conversationDao.insertConversation(entity)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, "فشل حفظ المحادثة: ${e.localizedMessage}")
        }
    }

    override fun getSavedConversations(): Flow<List<ChatConversation>> {
        return conversationDao.getAllConversations().map { list ->
            list.map { entity ->
                val messages = mutableListOf<ChatMessage>()
                try {
                    val arr = JSONArray(entity.rawJsonMessages)
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val roleStr = obj.optString("role")
                        val role = try {
                            MessageRole.fromString(roleStr)
                        } catch (_: Exception) {
                            MessageRole.Assistant
                        }
                        messages.add(
                            ChatMessage(
                                role = role,
                                content = obj.optString("content"),
                                thinkingContent = obj.optString("thinking").takeIf { it.isNotBlank() },
                                model = obj.optString("model").takeIf { it.isNotBlank() },
                                timestamp = obj.optLong("timestamp").takeIf { it != 0L }
                            )
                        )
                    }
                } catch (_: Exception) {}

                ChatConversation(
                    title = entity.title,
                    sourceUrl = entity.sourceUrl,
                    messages = messages,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun deleteConversation(id: String): AppResult<Unit> {
        return try {
            conversationDao.deleteConversationById(id)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, "فشل حذف المحادثة: ${e.localizedMessage}")
        }
    }
}
