package com.example.anchor.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** 任务列表区块配色（对齐图 1 风格） */
data class TaskListSectionColors(
    val accent: Color,
    val badgeContainer: Color,
    val badgeText: Color,
    val tagContainer: Color,
    val tagText: Color,
    val dateLabel: Color,
    val cardContainer: Color,
    val cardBorder: Color,
    val swipeActionBackground: Color,
    val swipeActionText: Color,
)

@Composable
fun fixedTaskSectionColors(): TaskListSectionColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        TaskListSectionColors(
            accent = Color(0xFF7AA3C4),
            badgeContainer = Color(0xFF2A3A4D),
            badgeText = Color(0xFFB8CCE0),
            tagContainer = Color(0xFF2A3A4D),
            tagText = Color(0xFF9BB8D4),
            dateLabel = Color(0xFF8A95A0),
            cardContainer = Color(0xFF1E1E1E),
            cardBorder = Color(0xFF3A4550),
            swipeActionBackground = Color(0xFF8B5E3C),
            swipeActionText = Color(0xFFFFF8F0),
        )
    } else {
        TaskListSectionColors(
            accent = Color(0xFF5B7A9A),
            badgeContainer = Color(0xFFE8F0F8),
            badgeText = Color(0xFF3D5570),
            tagContainer = Color(0xFFE8F0F8),
            tagText = Color(0xFF3D5570),
            dateLabel = Color(0xFF9AA5B0),
            cardContainer = Color(0xFFFFFFFF),
            cardBorder = Color(0xFFE0E8F0),
            swipeActionBackground = Color(0xFF8B5E3C),
            swipeActionText = Color(0xFFFFF8F0),
        )
    }
}

@Composable
fun optionalTaskSectionColors(): TaskListSectionColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        TaskListSectionColors(
            accent = Color(0xFF6BAF82),
            badgeContainer = Color(0xFF2A3D32),
            badgeText = Color(0xFFB8DCC4),
            tagContainer = Color(0xFF2A3D32),
            tagText = Color(0xFF8FD4A8),
            dateLabel = Color(0xFF8A9A8E),
            cardContainer = Color(0xFF1E1E1E),
            cardBorder = Color(0xFF3A4A40),
            swipeActionBackground = Color(0xFF8B5E3C),
            swipeActionText = Color(0xFFFFF8F0),
        )
    } else {
        TaskListSectionColors(
            accent = Color(0xFF5B9A6F),
            badgeContainer = Color(0xFFE8F5EC),
            badgeText = Color(0xFF3D6B4F),
            tagContainer = Color(0xFFE8F5EC),
            tagText = Color(0xFF3D6B4F),
            dateLabel = Color(0xFF9AA89E),
            cardContainer = Color(0xFFFFFFFF),
            cardBorder = Color(0xFFE8EDE9),
            swipeActionBackground = Color(0xFF8B5E3C),
            swipeActionText = Color(0xFFFFF8F0),
        )
    }
}

@Composable
fun tomorrowTaskSectionColors(): TaskListSectionColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        TaskListSectionColors(
            accent = Color(0xFFE8A87C),
            badgeContainer = Color(0xFF4A3528),
            badgeText = Color(0xFFF0D4B8),
            tagContainer = Color(0xFF4A3528),
            tagText = Color(0xFFE8A87C),
            dateLabel = Color(0xFF9A8A7E),
            cardContainer = Color(0xFF1E1E1E),
            cardBorder = Color(0xFF4A4038),
            swipeActionBackground = Color(0xFFE8B48A),
            swipeActionText = Color(0xFF4A3020),
        )
    } else {
        TaskListSectionColors(
            accent = Color(0xFFE8A87C),
            badgeContainer = Color(0xFFFFF0E6),
            badgeText = Color(0xFF8B5A3C),
            tagContainer = Color(0xFFFFF0E6),
            tagText = Color(0xFF8B5A3C),
            dateLabel = Color(0xFF9A8A7E),
            cardContainer = Color(0xFFFFFFFF),
            cardBorder = Color(0xFFF0E6DC),
            swipeActionBackground = Color(0xFFF5D4B8),
            swipeActionText = Color(0xFF6B4A30),
        )
    }
}

val TaskCardShape = RoundedCornerShape(14.dp)
val TaskAddButtonShape = RoundedCornerShape(28.dp)
val TaskSwipeRevealWidth = 72.dp
val TaskSwipeRevealWidthWide = 88.dp
