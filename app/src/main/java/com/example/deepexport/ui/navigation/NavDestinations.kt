package com.example.deepexport.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Browser : Screen("browser")
    data object Preview : Screen("preview")
    data object Export : Screen("export")
    data object History : Screen("history")
}
