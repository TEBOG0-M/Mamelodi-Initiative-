package com.mamelodiinitiative.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(val route: String, val title: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Default.Home),
    BottomNavItem(Routes.PROGRAMMES, "Programmes", Icons.Default.School),
    BottomNavItem(Routes.VOLUNTEER, "Volunteer", Icons.Default.People),
    BottomNavItem(Routes.NEWS, "News", Icons.Default.Article),
    BottomNavItem(Routes.MORE, "More", Icons.Default.MoreHoriz)
)
