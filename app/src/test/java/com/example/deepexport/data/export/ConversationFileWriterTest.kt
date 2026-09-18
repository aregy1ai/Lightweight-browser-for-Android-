package com.example.deepexport.data.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ConversationFileWriterTest {

    private val writer = ConversationFileWriter()

    @Test
    fun `writeExport creates directory and file with requested content`() {
        val testDir = File("build/test-output-writer")
        val file = writer.writeExport(testDir, "sample_export.txt", "Deep Export Test Content")

        assertTrue(file.exists())
        assertEquals("Deep Export Test Content", file.readText())

        file.delete()
        testDir.delete()
    }
}
