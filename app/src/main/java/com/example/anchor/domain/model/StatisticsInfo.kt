package com.example.anchor.domain.model

/**
 * 统计数据领域模型。
 */
data class StatisticsInfo(
    val actionRate: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalFixedCompleted: Int = 0,
    val totalOptionalCompleted: Int = 0,
    val weeklyFixedCounts: List<Int> = emptyList(),
    val weeklyOptionalCounts: List<Int> = emptyList(),
    val weeklyLabels: List<String> = emptyList(),
)
