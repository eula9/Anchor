package com.example.anchor.domain.repository

import com.example.anchor.domain.model.StatisticsInfo
import kotlinx.coroutines.flow.Flow

/**
 * 统计数据仓库接口。
 */
interface StatisticsRepository {

    val statistics: Flow<StatisticsInfo>
}
