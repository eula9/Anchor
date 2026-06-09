package com.example.anchor.domain.repository

import com.example.anchor.domain.model.StreakInfo
import kotlinx.coroutines.flow.Flow

/**
 * 连续行动天数仓库接口。
 *
 * 行动定义：今日至少完成一件事。
 */
interface StreakRepository {

    /** 观察连续行动天数状态 */
    val streakInfo: Flow<StreakInfo>

    /** 跨日时结算连续天数（断档则重置） */
    suspend fun refreshDayBoundary()

    /** 记录今日已完成行动 */
    suspend fun markActionToday()
}
