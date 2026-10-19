package com.mamelodiinitiative.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mamelodiinitiative.navigation.BottomNavItem

@Composable
fun BottomNavBar(navController: NavHostController, items: List<BottomNavItem>) {
    val entry = navController.currentBackStackEntryAsState().value
    val route = entry?.destination?.route
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, item.title) },
                label = { Text(item.title) }
            )
        }
    }
}
