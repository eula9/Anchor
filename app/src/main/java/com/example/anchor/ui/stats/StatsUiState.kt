package com.example.anchor.ui.stats

import com.example.anchor.domain.model.StatisticsInfo

/**
 * 统计页 UI 状态。
 */
data class StatsUiState(
    val statistics: StatisticsInfo = StatisticsInfo(),
    val isLoading: Boolean = true,
)
