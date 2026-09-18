package com.example.deepexport.data.export

import com.example.deepexport.domain.model.ExportFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportFormatterRegistryTest {

    private val registry = ExportFormatterRegistry(
        listOf(TxtExporter(), MarkdownExporter(), JsonExporter(), HtmlExporter())
    )

    @Test
    fun `formatter for each format returns corresponding exporter`() {
        assertTrue(registry.formatter(ExportFormat.TXT) is TxtExporter)
        assertTrue(registry.formatter(ExportFormat.MARKDOWN) is MarkdownExporter)
        assertTrue(registry.formatter(ExportFormat.JSON) is JsonExporter)
        assertTrue(registry.formatter(ExportFormat.HTML) is HtmlExporter)
    }

    @Test
    fun `supportedFormats returns all registered formats`() {
        val formats = registry.supportedFormats()
        assertEquals(4, formats.size)
        assertTrue(formats.contains(ExportFormat.TXT))
        assertTrue(formats.contains(ExportFormat.MARKDOWN))
        assertTrue(formats.contains(ExportFormat.JSON))
        assertTrue(formats.contains(ExportFormat.HTML))
    }
}
