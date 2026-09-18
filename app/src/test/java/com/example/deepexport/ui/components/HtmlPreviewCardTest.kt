package com.example.deepexport.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HtmlPreviewCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `html preview card renders content and triggers copy and share callbacks`() {
        var copiedText: String? = null
        var sharedText: String? = null

        val sampleHtml = """
            <h2>تقرير محادثة</h2>
            <p>مرحباً <b>بك</b> في نظام التصدير <a href="https://example.com">رابط</a>.</p>
        """.trimIndent()

        composeRule.setContent {
            HtmlPreviewCard(
                html = sampleHtml,
                onCopyClick = { copiedText = it },
                onShareClick = { sharedText = it }
            )
        }

        composeRule.onNodeWithTag("html_preview_card").assertExists()
        composeRule.onNodeWithTag("html_rendered_text").assertExists()

        composeRule.onNodeWithTag("html_copy_button").performClick()
        assertEquals(sampleHtml, copiedText)

        composeRule.onNodeWithTag("html_share_button").performClick()
        assertEquals(sampleHtml, sharedText)
    }
}
