package com.example.deepexport.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deepexport.ui.screen.browser.BrowserScreen
import com.example.deepexport.ui.screen.browser.BrowserViewModel
import com.example.deepexport.ui.screen.export.ExportScreen
import com.example.deepexport.ui.screen.export.ExportViewModel
import com.example.deepexport.ui.screen.history.HistoryScreen
import com.example.deepexport.ui.screen.history.HistoryViewModel
import com.example.deepexport.ui.screen.home.HomeScreen
import com.example.deepexport.ui.screen.home.HomeViewModel
import com.example.deepexport.ui.screen.preview.PreviewScreen
import com.example.deepexport.ui.screen.preview.PreviewViewModel

/**
 * AppNavigation component configuring NavHost and routing
 * across Home, Browser, Preview, Export, and History screens.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel(),
    browserViewModel: BrowserViewModel = viewModel(),
    previewViewModel: PreviewViewModel = viewModel(),
    exportViewModel: ExportViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val state = homeViewModel.uiState.collectAsStateWithLifecycle().value

            HomeScreen(
                state = state,
                onUrlChange = homeViewModel::updateUrl,
                onSelectPlatform = homeViewModel::selectPlatform,
                onOpenClick = {
                    browserViewModel.setUrl(state.url)
                    navController.navigate(Routes.BROWSER)
                },
                onHistoryClick = {
                    navController.navigate(Routes.HISTORY)
                },
                onResetUrlClick = homeViewModel::resetToDefaultUrl,
                onOpenSavedConversation = { convo ->
                    homeViewModel.openSavedConversation(convo) {
                        navController.navigate(Routes.PREVIEW)
                    }
                }
            )
        }

        composable(Routes.BROWSER) {
            val state = browserViewModel.uiState.collectAsStateWithLifecycle().value

            BrowserScreen(
                state = state,
                onUrlChange = browserViewModel::setUrl,
                onPageTitleChange = browserViewModel::setPageTitle,
                onToggleScrollLoader = browserViewModel::toggleLongConversationScroll,
                onExtractClick = { webView ->
                    browserViewModel.extractFromWebView(webView) {
                        navController.navigate(Routes.PREVIEW)
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.PREVIEW) {
            val state = previewViewModel.uiState.collectAsStateWithLifecycle().value

            PreviewScreen(
                state = state,
                onFormatChange = previewViewModel::setFormat,
                onExportClick = {
                    exportViewModel.setFormat(state.selectedFormat)
                    navController.navigate(Routes.EXPORT)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.EXPORT) {
            val state = exportViewModel.uiState.collectAsStateWithLifecycle().value

            ExportScreen(
                state = state,
                onFileNameChange = exportViewModel::setFileName,
                onFormatChange = exportViewModel::setFormat,
                onConfirmExport = {
                    exportViewModel.exportNow()
                },
                onViewHistoryClick = {
                    navController.navigate(Routes.HISTORY)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            val state = historyViewModel.uiState.collectAsStateWithLifecycle().value

            HistoryScreen(
                state = state,
                onClearHistory = historyViewModel::clearHistory,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
