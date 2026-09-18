package com.example.deepexport.ui.screen.preview

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.ui.components.AppTopBar
import com.example.deepexport.ui.components.ConversationTextViewer
import com.example.deepexport.ui.components.ExportFormatChips
import com.example.deepexport.ui.components.HtmlPreviewCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

@Composable
fun PreviewScreen(
    state: PreviewUiState,
    onFormatChange: (ExportFormat) -> Unit,
    onExportClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var htmlViewMode by remember { mutableIntStateOf(0) }
    val tabs = listOf("عرض الرسائل", "معاينة التنسيق (${state.selectedFormat.extension.uppercase()})")

    Scaffold(
        topBar = {
            AppTopBar(
                title = "معاينة المحادثة المستخرجة",
                canGoBack = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onExportClick,
                        enabled = state.conversation != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("preview_proceed_export_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "متابعة لتحديد اسم الملف والتصدير",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val conv = state.conversation
            if (conv == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد محادثة محددة حالياً.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Tab Row
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                            modifier = Modifier.testTag("preview_tab_$index")
                        )
                    }
                }

                if (selectedTab == 0) {
                    // Visual structured cards
                    ConversationTextViewer(
                        conversation = conv,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Raw formatted export preview
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "اختر صيغة المعاينة:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ExportFormatChips(
                            selectedFormat = state.selectedFormat,
                            onFormatSelected = onFormatChange
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (state.selectedFormat == ExportFormat.HTML) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                FilterChip(
                                    selected = htmlViewMode == 0,
                                    onClick = { htmlViewMode = 0 },
                                    label = { Text("معاينة منسقة (Formatted)") }
                                )
                                FilterChip(
                                    selected = htmlViewMode == 1,
                                    onClick = { htmlViewMode = 1 },
                                    label = { Text("كود المصدر (Source Code)") }
                                )
                            }
                        }

                        if (state.selectedFormat == ExportFormat.HTML && htmlViewMode == 0) {
                            HtmlPreviewCard(
                                html = state.formattedPreviewText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                        .verticalScroll(rememberScrollState())
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = state.formattedPreviewText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        ),
                                        modifier = Modifier.testTag("formatted_preview_text")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
