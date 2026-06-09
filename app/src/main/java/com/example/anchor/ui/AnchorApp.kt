package com.example.anchor.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.anchor.di.AppContainer
import com.example.anchor.domain.model.ThemeMode
import com.example.anchor.ui.navigation.AnchorNavGraph
import com.example.anchor.ui.theme.AnchorTheme

/**
 * Anchor 应用根 Composable。
 */
@Composable
fun AnchorApp(appContainer: AppContainer) {
    val navController = rememberNavController()
    val themeMode by appContainer.settingsRepository.themeMode
        .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    AnchorTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AnchorNavGraph(
                navController = navController,
                appContainer = appContainer,
            )
        }
    }
}
