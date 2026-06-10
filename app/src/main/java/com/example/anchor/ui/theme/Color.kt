package com.example.anchor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Anchor 极简配色 + 首页卡片强调色。
 */

val AnchorBlack = Color(0xFF1A1A1A)
val AnchorDarkGray = Color(0xFF4A4A4A)
val AnchorGray = Color(0xFF8A8A8A)
val AnchorLightGray = Color(0xFFE8E8E8)
val AnchorWhite = Color(0xFFFAFAFA)
val AnchorSurfaceDark = Color(0xFF121212)

// 身份锚点卡片
private val AnchorCardContainerLight = Color(0xFFD6E4F0)
private val AnchorCardBorderLight = Color(0xFFA8BDD4)
private val AnchorCardTitleLight = Color(0xFF3D5570)
private val AnchorCardBodyLight = Color(0xFF1A2B3C)

private val AnchorCardContainerDark = Color(0xFF2A3A4D)
private val AnchorCardBorderDark = Color(0xFF4A6080)
private val AnchorCardTitleDark = Color(0xFFB8CCE0)
private val AnchorCardBodyDark = Color(0xFFE8F0F8)

// 连续行动卡片
private val StreakCardContainerLight = Color(0xFFD4E8D9)
private val StreakCardBorderLight = Color(0xFFA8CDB5)
private val StreakCardTitleLight = Color(0xFF2D5A3D)
private val StreakCardAccentLight = Color(0xFF1B4D2E)

private val StreakCardContainerDark = Color(0xFF2A3D32)
private val StreakCardBorderDark = Color(0xFF4A7058)
private val StreakCardTitleDark = Color(0xFFB8DCC4)
private val StreakCardAccentDark = Color(0xFFE0F2E6)

/** 身份锚点卡片配色 */
data class AnchorCardColors(
    val container: Color,
    val border: Color,
    val title: Color,
    val body: Color,
    val subtitle: Color,
)

// 激励语卡片
private val MotivationCardContainerLight = Color(0xFFF5ECD7)
private val MotivationCardBorderLight = Color(0xFFD9C9A8)
private val MotivationCardTitleLight = Color(0xFF6B5A3E)
private val MotivationCardBodyLight = Color(0xFF3D3428)

private val MotivationCardContainerDark = Color(0xFF3D3528)
private val MotivationCardBorderDark = Color(0xFF5C5040)
private val MotivationCardTitleDark = Color(0xFFD9CDB0)
private val MotivationCardBodyDark = Color(0xFFF0E6D2)

/** 激励语卡片配色 */
data class MotivationCardColors(
    val container: Color,
    val border: Color,
    val title: Color,
    val body: Color,
)

/** 连续行动卡片配色 */
data class StreakCardColors(
    val container: Color,
    val border: Color,
    val title: Color,
    val body: Color,
    val accent: Color,
)

@Composable
fun anchorCardColors(): AnchorCardColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        AnchorCardColors(
            container = AnchorCardContainerDark,
            border = AnchorCardBorderDark,
            title = AnchorCardTitleDark,
            body = AnchorCardBodyDark,
            subtitle = AnchorCardTitleDark.copy(alpha = 0.8f),
        )
    } else {
        AnchorCardColors(
            container = AnchorCardContainerLight,
            border = AnchorCardBorderLight,
            title = AnchorCardTitleLight,
            body = AnchorCardBodyLight,
            subtitle = AnchorCardTitleLight.copy(alpha = 0.85f),
        )
    }
}

@Composable
fun motivationCardColors(): MotivationCardColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        MotivationCardColors(
            container = MotivationCardContainerDark,
            border = MotivationCardBorderDark,
            title = MotivationCardTitleDark,
            body = MotivationCardBodyDark,
        )
    } else {
        MotivationCardColors(
            container = MotivationCardContainerLight,
            border = MotivationCardBorderLight,
            title = MotivationCardTitleLight,
            body = MotivationCardBodyLight,
        )
    }
}

@Composable
fun streakCardColors(): StreakCardColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        StreakCardColors(
            container = StreakCardContainerDark,
            border = StreakCardBorderDark,
            title = StreakCardTitleDark,
            body = StreakCardTitleDark.copy(alpha = 0.85f),
            accent = StreakCardAccentDark,
        )
    } else {
        StreakCardColors(
            container = StreakCardContainerLight,
            border = StreakCardBorderLight,
            title = StreakCardTitleLight,
            body = StreakCardTitleLight.copy(alpha = 0.85f),
            accent = StreakCardAccentLight,
        )
    }
}
