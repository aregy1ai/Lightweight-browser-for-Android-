package com.example.deepexport.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.deepexport.domain.model.AppSettings
import com.example.deepexport.ui.components.AppTopBar
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onThresholdChange: (Double) -> Unit,
    onFailOnNewMetricChange: (Boolean) -> Unit,
    onFailOnMissingMetricChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false,
    errorMessage: String? = null
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "إعدادات الأداء والمعايير",
                canGoBack = true,
                onBackClick = onBack
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ضبط معايير الأداء والقياس",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "إدارة عتبة التراجع (Threshold) وسياسات التحقق التلقائي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Threshold Card
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "عتبة التراجع (Regression Threshold)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(settings.threshold * 100).roundToInt()}% (${settings.threshold})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = settings.threshold.toFloat(),
                        onValueChange = { newVal ->
                            val rounded = (newVal * 100.0).roundToInt() / 100.0
                            onThresholdChange(rounded)
                        },
                        valueRange = 0.01f..0.50f,
                        steps = 49,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_threshold_slider")
                    )

                    OutlinedTextField(
                        value = settings.threshold.toString(),
                        onValueChange = { input ->
                            input.toDoubleOrNull()?.let { onThresholdChange(it) }
                        },
                        label = { Text("قيمة العتبة يدوياً (0.01 - 0.50)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_threshold_input"),
                        isError = errorMessage != null,
                        supportingText = errorMessage?.let { { Text(it) } }
                    )
                }
            }

            // Policies Card
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "سياسات فحص المقاييس (Metric Policies)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Fail on new metric
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "فشل الاختبار عند وجود مقياس جديد",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "failOnNewMetric: رفض المقاييس غير المعرفة مسبقاً",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.failOnNewMetric,
                            onCheckedChange = onFailOnNewMetricChange,
                            modifier = Modifier.testTag("settings_fail_on_new_switch")
                        )
                    }

                    // Fail on missing metric
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "فشل الاختبار عند فقدان مقياس",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "failOnMissingMetric: تنبيه عند غياب مقاييس الأداء الأساسية",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.failOnMissingMetric,
                            onCheckedChange = onFailOnMissingMetricChange,
                            modifier = Modifier.testTag("settings_fail_on_missing_switch")
                        )
                    }
                }
            }

            // Benchmark & UI Optimizations Info
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "تنقية معمارية نشطة",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تم نقل كافة الحالات إلى StateFlow بدون Scopes داخل الـ Composables، مع استهلاك Lifecycle-aware.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("settings_save_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSaved) "تم حفظ الإعدادات بنجاح" else "حفظ الإعدادات في DataStore",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
