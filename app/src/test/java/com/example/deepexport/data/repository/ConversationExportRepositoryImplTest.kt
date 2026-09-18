package com.example.deepexport.data.repository

import com.example.deepexport.TestData
import com.example.deepexport.data.export.ConversationFileWriter
import com.example.deepexport.data.export.ExportFormatterRegistry
import com.example.deepexport.data.export.TxtExporter
import com.example.deepexport.domain.model.ExportFormat
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ConversationExportRepositoryImplTest {

    private val registry = ExportFormatterRegistry(listOf(TxtExporter()))
    private val fileWriter = ConversationFileWriter()
    private val repository = ConversationExportRepositoryImpl(registry, fileWriter)

    @Test
    fun `export creates file ending with appropriate extension and formatted content`() {
        val testDir = File("build/test-export-repo")
        val file = repository.export(TestData.conversation, ExportFormat.TXT, testDir)

        assertTrue(file.exists())
        assertTrue(file.name.endsWith(".txt"))
        assertTrue(file.readText().contains("Sample Chat"))

        file.delete()
        testDir.delete()
    }
}
