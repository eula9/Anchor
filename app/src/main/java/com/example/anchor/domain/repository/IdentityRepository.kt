package com.example.anchor.domain.repository

import com.example.anchor.domain.model.Identity
import kotlinx.coroutines.flow.Flow

/**
 * 身份仓库接口。
 *
 * 负责提供「今日身份」数据，保证同一天内身份不变、跨天自动切换。
 */
interface IdentityRepository {

    /**
     * 观察今日身份数据流。
     */
    val todayIdentity: Flow<Identity>

    /**
     * 一次性获取今日身份（供后台 Worker 使用）。
     */
    suspend fun getTodayIdentity(): Identity
}
