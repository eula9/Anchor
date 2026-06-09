package com.example.anchor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.anchor.di.AppContainer
import com.example.anchor.ui.home.HomeScreen
import com.example.anchor.ui.settings.SettingsScreen

/**
 * Anchor 应用导航图。
 */
@Composable
fun AnchorNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
    ) {
        composable(route = Routes.Home.route) {
            HomeScreen(
                appContainer = appContainer,
                onNavigateToSettings = {
                    navController.navigate(Routes.Settings.route)
                },
            )
        }

        composable(route = Routes.Settings.route) {
            SettingsScreen(
                appContainer = appContainer,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
