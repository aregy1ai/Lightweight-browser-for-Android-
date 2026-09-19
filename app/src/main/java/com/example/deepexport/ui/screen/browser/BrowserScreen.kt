package com.example.deepexport.ui.screen.browser

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.deepexport.data.web.WebViewBridge
import com.example.deepexport.ui.components.AppTopBar
import com.example.deepexport.ui.components.DeepSeekChatWebView
import com.example.deepexport.ui.components.ErrorBanner
import com.example.deepexport.ui.components.LoadingOverlay

@Composable
fun BrowserScreen(
    state: BrowserUiState,
    onUrlChange: (String) -> Unit,
    onPageTitleChange: (String) -> Unit = {},
    onToggleScrollLoader: (Boolean) -> Unit = {},
    onExtractClick: (WebView) -> Unit,
    onBackClick: () -> Unit
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var inputUrl by remember(state.url) { mutableStateOf(state.url) }

    BackHandler(enabled = webViewRef?.canGoBack() == true) {
        webViewRef?.goBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            WebViewBridge.cleanupWebView(webViewRef)
            webViewRef = null
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (state.webPageTitle.isNotBlank()) state.webPageTitle else "متصفح المحادثات",
                canGoBack = true,
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { webViewRef?.reload() },
                        modifier = Modifier.testTag("browser_reload_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث الصفحة"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 10.dp,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Smart scroll switch for lazy-loaded conversations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "التمرير الذكي لتحميل المحادثات الطويلة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = state.enableLongConversationScroll,
                            onCheckedChange = onToggleScrollLoader,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            webViewRef?.let { wv -> onExtractClick(wv) }
                        },
                        enabled = !state.isExtracting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("browser_extract_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isExtracting) "جارٍ الاستخراج..." else "استخراج محادثة ${state.detectedPlatform.displayName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // URL input bar and detected platform chip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(state.detectedPlatform.displayName) },
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = {
                            inputUrl = it
                            onUrlChange(it)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("browser_url_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val formatted = if (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://")) {
                                        "https://$inputUrl"
                                    } else inputUrl
                                    inputUrl = formatted
                                    webViewRef?.loadUrl(formatted)
                                },
                                modifier = Modifier.testTag("browser_go_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "الذهاب للرابط"
                                )
                            }
                        }
                    )
                }

                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                state.error?.let {
                    ErrorBanner(message = it)
                }

                // WebView component with JavaScript interface and DOM extraction support
                DeepSeekChatWebView(
                    url = state.url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("browser_webview"),
                    onPageTitleReceived = { title ->
                        onPageTitleChange(title)
                    },
                    onPageFinished = { _, currentUrl ->
                        onUrlChange(currentUrl)
                    },
                    onWebViewCreated = { wv ->
                        webViewRef = wv
                    }
                )
            }

            LoadingOverlay(
                visible = state.isExtracting,
                message = state.statusMessage.ifBlank { "جارٍ استخراج المحادثة وسلاسل التفكير والأكواد..." }
            )
        }
    }
}
