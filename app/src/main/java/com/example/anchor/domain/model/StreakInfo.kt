package com.example.anchor.domain.model

/**
 * 连续行动天数信息。
 *
 * @param currentStreak 当前连续完成固定任务的天数
 * @param longestStreak 历史最长连续天数
 * @param actionTakenToday 今日是否已完成全部固定任务
 */
data class StreakInfo(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val actionTakenToday: Boolean = false,
)
