package com.example.deepexport.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deepexport.ui.screen.browser.BrowserViewModel
import com.example.deepexport.ui.screen.export.ExportViewModel
import com.example.deepexport.ui.screen.history.HistoryViewModel
import com.example.deepexport.ui.screen.home.HomeViewModel
import com.example.deepexport.ui.screen.preview.PreviewViewModel

/**
 * AppNavigation component delegating to AppNavHost.
 * Provides clean architectural boundary with StateFlow and lifecycle-aware state collection.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    browserViewModel: BrowserViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    previewViewModel: PreviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    exportViewModel: ExportViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    historyViewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    AppNavHost(
        modifier = modifier,
        navController = navController,
        homeViewModel = homeViewModel,
        browserViewModel = browserViewModel,
        previewViewModel = previewViewModel,
        exportViewModel = exportViewModel,
        historyViewModel = historyViewModel
    )
}
