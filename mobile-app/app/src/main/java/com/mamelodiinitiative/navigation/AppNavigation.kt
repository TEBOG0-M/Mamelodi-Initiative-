package com.mamelodiinitiative.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mamelodiinitiative.ui.components.BottomNavBar
import com.mamelodiinitiative.ui.screens.AboutScreen
import com.mamelodiinitiative.ui.screens.ContactScreen
import com.mamelodiinitiative.ui.screens.DonateScreen
import com.mamelodiinitiative.ui.screens.HomeScreen
import com.mamelodiinitiative.ui.screens.MoreScreen
import com.mamelodiinitiative.ui.screens.NewsDetailScreen
import com.mamelodiinitiative.ui.screens.NewsScreen
import com.mamelodiinitiative.ui.screens.ProgrammeDetailScreen
import com.mamelodiinitiative.ui.screens.ProgrammesScreen
import com.mamelodiinitiative.ui.screens.VolunteerApplicationScreen
import com.mamelodiinitiative.ui.screens.VolunteerReviewScreen
import com.mamelodiinitiative.ui.screens.VolunteerScreen
import com.mamelodiinitiative.ui.screens.VolunteerSuccessScreen
import com.mamelodiinitiative.viewmodel.VolunteerViewModel

@Composable
fun AppNavigation(
    onLogout: () -> Unit,
    navController: NavHostController = rememberNavController(),
    volunteerViewModel: VolunteerViewModel = viewModel()
) {
    val volunteerUiState by volunteerViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController, bottomNavItems)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(onNavigate = navController::navigate)
            }

            composable(Routes.PROGRAMMES) {
                ProgrammesScreen(onNavigate = navController::navigate)
            }

            composable(
                route = Routes.PROGRAMME_DETAIL,
                arguments = listOf(
                    navArgument("programmeId") {
                        type = NavType.IntType
                    }
                )
            ) { entry ->
                ProgrammeDetailScreen(
                    programmeId = entry.arguments?.getInt("programmeId") ?: -1,
                    onBack = { navController.popBackStack() },
                    onNavigate = navController::navigate
                )
            }

            composable(Routes.VOLUNTEER) {
                VolunteerScreen(onNavigate = navController::navigate)
            }

            composable(Routes.VOLUNTEER_FORM) {
                VolunteerApplicationScreen(
                    application = volunteerUiState.application,
                    onChange = volunteerViewModel::updateApplication,
                    onBack = { navController.popBackStack() },
                    onReview = {
                        navController.navigate(Routes.VOLUNTEER_REVIEW)
                    }
                )
            }

            composable(Routes.VOLUNTEER_REVIEW) {
                VolunteerReviewScreen(
                    state = volunteerUiState,
                    onEdit = { navController.popBackStack() },
                    onSubmit = volunteerViewModel::submitApplication,
                    onSuccess = {
                        navController.navigate(Routes.VOLUNTEER_SUCCESS)
                    }
                )
            }

            composable(Routes.VOLUNTEER_SUCCESS) {
                VolunteerSuccessScreen(
                    onNavigate = { route ->
                        volunteerViewModel.resetApplication()
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.NEWS) {
                NewsScreen(onNavigate = navController::navigate)
            }

            composable(
                route = NEWS_DETAIL,
                arguments = listOf(
                    navArgument("newsId") {
                        type = NavType.IntType
                    }
                )
            ) { entry ->
                NewsDetailScreen(
                    newsId = entry.arguments?.getInt("newsId") ?: -1,
                    onBack = { navController.popBackStack() },
                    onNavigate = navController::navigate
                )
            }

            composable(Routes.MORE) {
                MoreScreen(
                    onNavigate = navController::navigate,
                    onLogout = onLogout
                )
            }

            composable(Routes.ABOUT) {
                AboutScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = navController::navigate
                )
            }

            composable(Routes.DONATE) {
                DonateScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = navController::navigate
                )
            }

            composable(Routes.CONTACT) {
                ContactScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = navController::navigate
                )
            }
        }
    }
}
private const val NEWS_DETAIL = Routes.NEWS_DETAIL
