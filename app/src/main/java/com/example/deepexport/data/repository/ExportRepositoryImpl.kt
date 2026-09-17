package com.example.deepexport.data.repository

import com.example.deepexport.core.AppResult
import com.example.deepexport.core.sanitizeFileName
import com.example.deepexport.data.export.FileStore
import com.example.deepexport.data.export.HtmlExporter
import com.example.deepexport.data.export.JsonExporter
import com.example.deepexport.data.export.MarkdownExporter
import com.example.deepexport.data.export.TxtExporter
import com.example.deepexport.data.local.dao.ExportHistoryDao
import com.example.deepexport.data.local.entity.ExportHistoryEntity
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.model.ExportResult
import com.example.deepexport.domain.repository.ExportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExportRepositoryImpl(
    private val fileStore: FileStore,
    private val exportHistoryDao: ExportHistoryDao,
    private val txtExporter: TxtExporter = TxtExporter(),
    private val markdownExporter: MarkdownExporter = MarkdownExporter(),
    private val jsonExporter: JsonExporter = JsonExporter(),
    private val htmlExporter: HtmlExporter = HtmlExporter()
) : ExportRepository {

    override suspend fun export(
        conversation: ChatConversation,
        format: ExportFormat,
        fileName: String
    ): AppResult<ExportResult> = withContext(Dispatchers.IO) {
        try {
            val content = when (format) {
                ExportFormat.TXT -> txtExporter.export(conversation)
                ExportFormat.MARKDOWN -> markdownExporter.export(conversation)
                ExportFormat.JSON -> jsonExporter.export(conversation)
                ExportFormat.HTML -> htmlExporter.export(conversation)
            }

            val safeBaseName = fileName.sanitizeFileName().ifBlank { "ai_chat_export" }
            val actualFileName = "$safeBaseName.${format.extension}"

            // Save to app external storage
            val file = fileStore.saveToAppExternal(actualFileName, content)
            // Also attempt to save to Downloads directory for easy user access
            fileStore.saveToDownloads(actualFileName, content, format.mimeType)

            val exportEntity = ExportHistoryEntity(
                conversationId = conversation.id,
                title = conversation.title,
                fileName = actualFileName,
                filePath = file.absolutePath,
                format = format.name,
                messageCount = conversation.messages.size,
                exportedAt = System.currentTimeMillis(),
                status = "تم بنجاح"
            )
            exportHistoryDao.insertHistory(exportEntity)

            AppResult.Success(
                ExportResult(
                    success = true,
                    message = "تم حفظ الملف بنجاح في مجلد التنزيلات وملفات التطبيق",
                    filePath = file.absolutePath,
                    fileName = actualFileName,
                    format = format
                )
            )
        } catch (e: Exception) {
            AppResult.Error(e, "فشل تصدير الملف: ${e.localizedMessage}")
        }
    }

    override fun getExportHistory(): Flow<List<ExportResult>> {
        return exportHistoryDao.getAllHistory().map { list ->
            list.map { entity ->
                val format = try {
                    ExportFormat.valueOf(entity.format)
                } catch (_: Exception) {
                    ExportFormat.TXT
                }
                ExportResult(
                    success = entity.status == "تم بنجاح",
                    message = entity.title,
                    filePath = entity.filePath,
                    fileName = entity.fileName,
                    format = format,
                    exportedAt = entity.exportedAt
                )
            }
        }
    }

    override suspend fun deleteExportHistory(id: Long): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            exportHistoryDao.deleteHistoryById(id)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, "فشل حذف السجل")
        }
    }

    override suspend fun clearHistory(): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            exportHistoryDao.clearAll()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, "فشل مسح السجل")
        }
    }
}
