package com.mamelodiinitiative.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Default.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.PROGRAMMES, "Learn", Icons.Default.School, Icons.Outlined.School),
    BottomNavItem(Routes.VOLUNTEER, "Join", Icons.Default.People, Icons.Outlined.People),
    BottomNavItem(Routes.NEWS, "News", Icons.Default.Article, Icons.Outlined.Article),
    BottomNavItem(Routes.MORE, "More", Icons.Default.MoreHoriz, Icons.Outlined.MoreHoriz)
)
