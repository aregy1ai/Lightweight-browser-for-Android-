package com.example.deepexport.data.export

import com.example.deepexport.TestData
import com.example.deepexport.domain.model.ExportFormat
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JsonExporterTest {

    private val exporter = JsonExporter()

    @Test
    fun `supports json format returns true and false for others`() {
        assertTrue(exporter.supports(ExportFormat.JSON))
        assertFalse(exporter.supports(ExportFormat.TXT))
        assertFalse(exporter.supports(ExportFormat.MARKDOWN))
        assertFalse(exporter.supports(ExportFormat.HTML))
    }

    @Test
    fun `format returns valid json matching conversation`() {
        val result = exporter.format(TestData.conversation)
        val json = JSONObject(result)

        assertEquals("Sample Chat", json.getString("title"))
        assertEquals("https://example.com/chat/123", json.getString("sourceUrl"))
        assertEquals(2, json.getJSONArray("messages").length())
    }
}
