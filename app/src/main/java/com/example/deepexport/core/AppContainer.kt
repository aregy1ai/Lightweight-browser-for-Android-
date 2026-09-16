package com.example.deepexport.core

import android.content.Context
import com.example.deepexport.data.export.FileStore
import com.example.deepexport.data.export.JsonExporter
import com.example.deepexport.data.export.MarkdownExporter
import com.example.deepexport.data.export.TxtExporter
import com.example.deepexport.data.local.AppDatabase
import com.example.deepexport.data.repository.ConversationRepositoryImpl
import com.example.deepexport.data.repository.ExportRepositoryImpl
import com.example.deepexport.data.web.DeepSeekWebRepository
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.repository.ConversationRepository
import com.example.deepexport.domain.repository.ExportRepository
import com.example.deepexport.domain.usecase.ExportConversationUseCase
import com.example.deepexport.domain.usecase.ExtractConversationUseCase
import com.example.deepexport.domain.usecase.LoadHistoryUseCase
import com.example.deepexport.domain.usecase.SaveConversationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppContainer(private val context: Context) {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val fileStore: FileStore by lazy {
        FileStore(context)
    }

    val txtExporter: TxtExporter by lazy { TxtExporter() }
    val markdownExporter: MarkdownExporter by lazy { MarkdownExporter() }
    val jsonExporter: JsonExporter by lazy { JsonExporter() }

    val extractConversationUseCase: ExtractConversationUseCase by lazy {
        ExtractConversationUseCase()
    }

    val deepSeekWebRepository: DeepSeekWebRepository by lazy {
        DeepSeekWebRepository()
    }

    val conversationRepository: ConversationRepository by lazy {
        ConversationRepositoryImpl(database.conversationDao())
    }

    val exportRepository: ExportRepository by lazy {
        ExportRepositoryImpl(
            fileStore = fileStore,
            exportHistoryDao = database.exportHistoryDao(),
            txtExporter = txtExporter,
            markdownExporter = markdownExporter,
            jsonExporter = jsonExporter
        )
    }

    val exportConversationUseCase: ExportConversationUseCase by lazy {
        ExportConversationUseCase(exportRepository)
    }

    val saveConversationUseCase: SaveConversationUseCase by lazy {
        SaveConversationUseCase(conversationRepository)
    }

    val loadHistoryUseCase: LoadHistoryUseCase by lazy {
        LoadHistoryUseCase(exportRepository)
    }

    // Shared state for the active extracted conversation across screens
    private val _activeConversation = MutableStateFlow<ChatConversation?>(null)
    val activeConversation = _activeConversation.asStateFlow()

    fun setActiveConversation(conversation: ChatConversation?) {
        _activeConversation.value = conversation
    }
}
