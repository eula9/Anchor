package com.example.anchor.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 统计页 ViewModel。
 */
class StatsViewModel(
    private val statisticsRepository: StatisticsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            statisticsRepository.statistics.collect { statistics ->
                _uiState.update {
                    it.copy(statistics = statistics, isLoading = false)
                }
            }
        }
    }
}
