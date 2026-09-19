package com.example.deepexport.core

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.deepexport.data.export.ChatExportConversionService
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import java.io.File

/**
 * Utility to share exported files or conversation contents directly using the Android system share sheet.
 */
object ShareHelper {

    /**
     * Shares an existing exported file via Android system share sheet.
     */
    fun shareFile(
        context: Context,
        file: File,
        format: ExportFormat,
        chooserTitle: String = "مشاركة المحادثة"
    ) {
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = format.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Quickly exports and shares a [ChatConversation] directly into a temporary cache file in TXT or Markdown format,
     * opening the Android system share sheet immediately.
     */
    fun quickShareConversation(
        context: Context,
        conversation: ChatConversation,
        format: ExportFormat = ExportFormat.MARKDOWN,
        conversionService: ChatExportConversionService = ChatExportConversionService()
    ) {
        val sanitizedTitle = conversation.title
            .replace(Regex("[^a-zA-Z0-9_\\u0600-\\u06FF-]"), "_")
            .take(40)
            .ifBlank { "deepseek_chat" }

        val fileName = "${sanitizedTitle}_${System.currentTimeMillis()}.${format.extension}"
        val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val targetFile = File(cacheDir, fileName)

        val content = conversionService.convert(conversation, format)
        targetFile.writeText(content, Charsets.UTF_8)

        shareFile(context, targetFile, format, "مشاركة المحادثة (${format.extension.uppercase()})")
    }
}
