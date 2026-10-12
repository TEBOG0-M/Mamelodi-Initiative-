package com.mamelodiinitiative.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mamelodiinitiative.ui.components.BottomNavBar
import com.mamelodiinitiative.ui.screens.SimplePlaceholderScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    Scaffold(bottomBar = { BottomNavBar(navController, bottomNavItems) }) { padding ->
        NavHost(navController, Routes.HOME, Modifier.padding(padding)) {
            composable(Routes.HOME) { SimplePlaceholderScreen("Home") }
            composable(Routes.PROGRAMMES) { SimplePlaceholderScreen("Programmes") }
            composable(Routes.PROGRAMME_DETAIL) { SimplePlaceholderScreen("Programme Details") }
            composable(Routes.VOLUNTEER) { SimplePlaceholderScreen("Volunteer") }
            composable(Routes.VOLUNTEER_FORM) { SimplePlaceholderScreen("Volunteer Application") }
            composable(Routes.VOLUNTEER_SUCCESS) { SimplePlaceholderScreen("Application Confirmation") }
            composable(Routes.NEWS) { SimplePlaceholderScreen("News") }
            composable(Routes.NEWS_DETAIL) { SimplePlaceholderScreen("News Details") }
            composable(Routes.MORE) { SimplePlaceholderScreen("More") }
            composable(Routes.ABOUT) { SimplePlaceholderScreen("About") }
            composable(Routes.DONATE) { SimplePlaceholderScreen("Donate") }
            composable(Routes.CONTACT) { SimplePlaceholderScreen("Contact") }
        }
    }
}
