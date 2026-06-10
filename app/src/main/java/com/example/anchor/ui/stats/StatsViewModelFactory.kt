package com.example.anchor.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anchor.domain.repository.StatisticsRepository

/**
 * StatsViewModel 工厂类。
 */
class StatsViewModelFactory(
    private val statisticsRepository: StatisticsRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            return StatsViewModel(statisticsRepository = statisticsRepository) as T
        }
        throw IllegalArgumentException("未知的 ViewModel 类型: ${modelClass.name}")
    }
}
