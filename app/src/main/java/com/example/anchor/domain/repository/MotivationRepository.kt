package com.example.anchor.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * 每日激励语仓库接口。
 */
interface MotivationRepository {

    /** 今日激励语（随日期更新） */
    val todayMotivation: Flow<String>

    /** 确保今日激励语已生成并持久化 */
    suspend fun ensureTodayMotivation()
}
