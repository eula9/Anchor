package com.example.anchor.domain.model

/**
 * 连续行动天数信息。
 *
 * @param currentStreak 当前连续天数
 * @param longestStreak 历史最长连续天数
 * @param actionTakenToday 今日是否已完成至少一件事
 */
data class StreakInfo(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val actionTakenToday: Boolean = false,
)
