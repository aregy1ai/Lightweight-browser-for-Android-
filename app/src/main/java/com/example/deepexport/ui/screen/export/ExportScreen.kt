package com.example.deepexport.ui.screen.export

import android.content.Intent
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.deepexport.core.ShareHelper
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.ui.components.AppTopBar
import com.example.deepexport.ui.components.ErrorBanner
import com.example.deepexport.ui.components.ExportFormatChips
import com.example.deepexport.ui.components.LoadingOverlay
import java.io.File

@Composable
fun ExportScreen(
    state: ExportUiState,
    onFileNameChange: (String) -> Unit,
    onFormatChange: (ExportFormat) -> Unit,
    onConfirmExport: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = "تصدير الملف",
                canGoBack = true,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.error?.let {
                    ErrorBanner(message = it)
                }

                // File Name configuration
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "اسم الملف",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.fileName,
                            onValueChange = onFileNameChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_file_name_input"),
                            label = { Text("اسم الملف بدون الامتداد") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null)
                            },
                            trailingIcon = {
                                Text(
                                    text = ".${state.format.extension}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            singleLine = true
                        )
                    }
                }

                // Format selection card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "صيغة التصدير",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExportFormatChips(
                            selectedFormat = state.format,
                            onFormatSelected = onFormatChange
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val explanation = when (state.format) {
                            ExportFormat.TXT -> "نص بسيط ومتوافق مع أي جهاز أو محرر نصوص دون الحاجة لبرامج مخصصة."
                            ExportFormat.MARKDOWN -> "الصيغة الموصى بها لمحررات Obsidian وNotion وGitHub؛ تحافظ على تنسيق الكود والروابط وعناوين الأسئلة."
                            ExportFormat.JSON -> "تصدير منظم للرسائل وسلاسل التفكير مناسب للمطورين والتحليل البرمجي."
                            ExportFormat.HTML -> "صفحة ويب مصممة وجاهزة للعرض الفوري والمشاركة في أي متصفح مع الحفاظ على الألوان والأكواد وسلاسل التفكير."
                        }
                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Export Action Button
                Button(
                    onClick = onConfirmExport,
                    enabled = !state.isExporting && state.fileName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("export_confirm_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isExporting) "جارٍ حفظ وتصدير الملف..." else "حفظ الملف وتصديره",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // If exported successfully
                state.exportedResult?.let { result ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تم التصدير بنجاح!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "الملف: ${result.fileName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        result.filePath?.let { path ->
                                            val file = File(path)
                                            ShareHelper.shareFile(
                                                context = context,
                                                file = file,
                                                format = result.format ?: ExportFormat.TXT,
                                                chooserTitle = "مشاركة المحادثة المصدّرة"
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("export_share_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مشاركة")
                                }

                                OutlinedButton(
                                    onClick = onViewHistoryClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("export_go_history_button")
                                ) {
                                    Icon(imageVector = Icons.Default.History, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("السجل")
                                }
                            }
                        }
                    }
                }
            }

            LoadingOverlay(
                visible = state.isExporting,
                message = "جارٍ حفظ وتوليد الملف..."
            )
        }
    }
}
