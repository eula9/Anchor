package com.example.anchor.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.anchor.di.AppContainer
import com.example.anchor.ui.home.HomeScreen
import com.example.anchor.ui.launch.LaunchScreen
import com.example.anchor.ui.settings.SettingsScreen
import com.example.anchor.ui.setup.SetupScreen
import com.example.anchor.ui.stats.StatsScreen

/**
 * Anchor 应用导航图（含底部导航）。
 */
@Composable
fun AnchorNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
) {
    val isSetupComplete by appContainer.identityRepository.isSetupComplete
        .collectAsStateWithLifecycle(initialValue = false)

    val startDestination = if (isSetupComplete) Routes.Launch.route else Routes.Setup.route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf(
        Routes.Home.route,
        Routes.Stats.route,
        Routes.Settings.route,
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AnchorBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding),
        ) {
            composable(route = Routes.Setup.route) {
                SetupScreen(
                    appContainer = appContainer,
                    onSetupComplete = {
                        navController.navigate(Routes.Launch.route) {
                            popUpTo(Routes.Setup.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(route = Routes.Launch.route) {
                LaunchScreen(
                    appContainer = appContainer,
                    onEnterHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Launch.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(route = Routes.Home.route) {
                HomeScreen(appContainer = appContainer)
            }

            composable(route = Routes.Stats.route) {
                StatsScreen(appContainer = appContainer)
            }

            composable(route = Routes.Settings.route) {
                SettingsScreen(appContainer = appContainer)
            }
        }
    }
}
