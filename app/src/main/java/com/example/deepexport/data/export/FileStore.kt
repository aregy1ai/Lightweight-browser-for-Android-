package com.example.deepexport.data.export

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class FileStore(private val context: Context) {

    fun saveInternal(fileName: String, content: String): File {
        val exportDir = File(context.filesDir, "exports").apply { mkdirs() }
        val file = File(exportDir, fileName)
        file.writeText(content)
        return file
    }

    fun saveToAppExternal(fileName: String, content: String): File {
        val baseDir = context.getExternalFilesDir("exports") ?: File(context.filesDir, "exports")
        baseDir.mkdirs()
        val file = File(baseDir, fileName)
        file.writeText(content)
        return file
    }

    fun saveToDownloads(fileName: String, content: String, mimeType: String): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/DeepSeekExports")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { stream ->
                        stream.write(content.toByteArray())
                    }
                }
                uri
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetDir = File(downloadsDir, "DeepSeekExports").apply { mkdirs() }
                val file = File(targetDir, fileName)
                file.writeText(content)
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            // Fallback to internal storage
            val file = saveToAppExternal(fileName, content)
            getShareableUri(file)
        }
    }

    fun getShareableUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
