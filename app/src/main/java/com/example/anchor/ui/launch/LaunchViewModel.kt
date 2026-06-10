package com.example.anchor.ui.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.MotivationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 启动页 ViewModel。
 */
class LaunchViewModel(
    private val identityRepository: IdentityRepository,
    private val motivationRepository: MotivationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LaunchUiState())
    val uiState: StateFlow<LaunchUiState> = _uiState.asStateFlow()

    init {
        observeActiveAnchor()
        observeMotivation()
    }

    private fun observeActiveAnchor() {
        viewModelScope.launch {
            identityRepository.activeAnchor.collect { anchor ->
                _uiState.update {
                    it.copy(isLoading = false, activeAnchor = anchor)
                }
            }
        }
    }

    private fun observeMotivation() {
        viewModelScope.launch {
            motivationRepository.ensureTodayMotivation()
            motivationRepository.todayMotivation.collect { quote ->
                _uiState.update { it.copy(motivationQuote = quote) }
            }
        }
    }
}
