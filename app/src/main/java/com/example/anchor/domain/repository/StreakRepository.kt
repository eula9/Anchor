package com.example.anchor.domain.repository

import com.example.anchor.domain.model.StreakInfo
import kotlinx.coroutines.flow.Flow

/**
 * 连续行动天数仓库接口。
 *
 * 连续规则：连续完成全部固定任务的天数。
 */
interface StreakRepository {

    val streakInfo: Flow<StreakInfo>

    /** 跨日时结算连续天数 */
    suspend fun refreshDayBoundary()

    /** 今日完成全部固定任务后更新连续天数 */
    suspend fun onAllFixedTasksCompleted()
}
