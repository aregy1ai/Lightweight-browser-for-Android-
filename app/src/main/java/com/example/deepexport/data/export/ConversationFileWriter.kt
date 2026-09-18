package com.example.deepexport.data.export

import java.io.File

class ConversationFileWriter {
    fun writeExport(directory: File, fileName: String, content: String): File {
        if (!directory.exists()) directory.mkdirs()
        val file = File(directory, fileName)
        file.writeText(content)
        return file
    }
}
