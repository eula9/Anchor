package com.example.anchor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** 深色模式配色 */
private val DarkColorScheme = darkColorScheme(
    primary = AnchorWhite,
    onPrimary = AnchorBlack,
    secondary = AnchorGray,
    onSecondary = AnchorWhite,
    background = AnchorSurfaceDark,
    onBackground = AnchorWhite,
    surface = AnchorBlack,
    onSurface = AnchorWhite,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = AnchorLightGray,
)

/** 浅色模式配色 */
private val LightColorScheme = lightColorScheme(
    primary = AnchorBlack,
    onPrimary = AnchorWhite,
    secondary = AnchorDarkGray,
    onSecondary = AnchorWhite,
    background = AnchorWhite,
    onBackground = AnchorBlack,
    surface = AnchorWhite,
    onSurface = AnchorBlack,
    surfaceVariant = AnchorLightGray,
    onSurfaceVariant = AnchorDarkGray,
)

/**
 * Anchor Material3 主题（极简黑白灰，禁用动态取色）。
 */
@Composable
fun AnchorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
