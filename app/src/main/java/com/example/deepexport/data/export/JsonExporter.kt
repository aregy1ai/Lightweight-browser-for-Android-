package com.example.deepexport.data.export

import com.example.deepexport.domain.model.ChatConversation
import org.json.JSONArray
import org.json.JSONObject

class JsonExporter {
    fun export(conversation: ChatConversation): String {
        val root = JSONObject()
        root.put("title", conversation.title)
        root.put("sourceUrl", conversation.sourceUrl)
        root.put("createdAt", conversation.createdAt)
        root.put("totalMessages", conversation.messages.size)

        val messagesArray = JSONArray()
        conversation.messages.forEachIndexed { index, msg ->
            val msgObj = JSONObject()
            msgObj.put("index", index)
            msgObj.put("role", msg.role.name)
            msgObj.put("content", msg.content)
            if (msg.thinkingContent != null) {
                msgObj.put("thinkingContent", msg.thinkingContent)
            }
            if (msg.timestamp != null) {
                msgObj.put("timestamp", msg.timestamp)
            }
            messagesArray.put(msgObj)
        }
        root.put("messages", messagesArray)

        return root.toString(2)
    }
}
