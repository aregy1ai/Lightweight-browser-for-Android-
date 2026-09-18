package com.example.deepexport.data.export

import com.example.deepexport.TestData
import com.example.deepexport.domain.model.ExportFormat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TxtExporterTest {

    private val exporter = TxtExporter()

    @Test
    fun `supports txt format returns true and false for others`() {
        assertTrue(exporter.supports(ExportFormat.TXT))
        assertFalse(exporter.supports(ExportFormat.MARKDOWN))
        assertFalse(exporter.supports(ExportFormat.JSON))
        assertFalse(exporter.supports(ExportFormat.HTML))
    }

    @Test
    fun `format contains conversation title, source, and message content`() {
        val result = exporter.format(TestData.conversation)

        assertTrue(result.contains("Sample Chat"))
        assertTrue(result.contains("https://example.com/chat/123"))
        assertTrue(result.contains("Hello, how do I use deep export?"))
        assertTrue(result.contains("User needs assistance"))
    }
}
