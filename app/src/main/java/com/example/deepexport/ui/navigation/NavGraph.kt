package com.example.deepexport.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * NavGraph component delegating to AppNavigation.
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    AppNavigation(navController = navController)
}
