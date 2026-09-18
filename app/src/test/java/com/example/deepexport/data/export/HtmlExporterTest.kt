package com.example.deepexport.data.export

import com.example.deepexport.TestData
import com.example.deepexport.domain.model.ExportFormat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HtmlExporterTest {

    private val exporter = HtmlExporter()

    @Test
    fun `supports html format returns true and false for others`() {
        assertTrue(exporter.supports(ExportFormat.HTML))
        assertFalse(exporter.supports(ExportFormat.TXT))
        assertFalse(exporter.supports(ExportFormat.MARKDOWN))
        assertFalse(exporter.supports(ExportFormat.JSON))
    }

    @Test
    fun `format contains html structure and styled metadata`() {
        val result = exporter.format(TestData.conversation)

        assertTrue(result.contains("<!DOCTYPE html>"))
        assertTrue(result.contains("<html"))
        assertTrue(result.contains("Sample Chat"))
        assertTrue(result.contains("https://example.com/chat/123"))
    }
}
