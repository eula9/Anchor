package com.example.anchor.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.repository.InvalidAnchorException
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.TaskRepository
import com.example.anchor.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 首次设置页 ViewModel。
 */
class SetupViewModel(
    private val identityRepository: IdentityRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    private val _setupCompleted = MutableStateFlow(false)
    val setupCompleted: StateFlow<Boolean> = _setupCompleted.asStateFlow()

    fun onIdentityChange(value: String) {
        _uiState.update {
            it.copy(
                identityInput = value.take(Constants.MAX_ANCHOR_IDENTITY_LENGTH),
                errorMessage = null,
            )
        }
    }

    fun onPresetDurationSelected(days: Int) {
        _uiState.update {
            it.copy(isCustomDuration = false, selectedPresetDays = days, errorMessage = null)
        }
    }

    fun onCustomDurationModeSelected() {
        _uiState.update { it.copy(isCustomDuration = true, errorMessage = null) }
    }

    fun onCustomDurationInputChange(value: String) {
        _uiState.update {
            it.copy(
                customDurationInput = value.filter { char -> char.isDigit() },
                errorMessage = null,
            )
        }
    }

    fun onFixedTaskChange(index: Int, value: String) {
        _uiState.update { state ->
            val updated = state.fixedTaskInputs.toMutableList()
            if (index in updated.indices) {
                updated[index] = value
            }
            state.copy(fixedTaskInputs = updated, errorMessage = null)
        }
    }

    fun addFixedTaskField() {
        _uiState.update { state ->
            if (!state.canAddMoreFixedTasks) return@update state
            state.copy(fixedTaskInputs = state.fixedTaskInputs + "")
        }
    }

    fun removeFixedTaskField() {
        _uiState.update { state ->
            if (!state.canRemoveFixedTask) return@update state
            state.copy(fixedTaskInputs = state.fixedTaskInputs.dropLast(1))
        }
    }

    fun submitSetup() {
        val state = _uiState.value
        val durationDays = resolveDurationDays(state)

        if (durationDays == null) {
            _uiState.update { it.copy(errorMessage = "请输入有效的自定义天数") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            identityRepository.setupAnchor(
                identity = state.identityInput,
                durationDays = durationDays,
                fixedTasks = state.fixedTaskInputs,
            ).onSuccess {
                val templates = identityRepository.fixedTaskTemplates.first()
                taskRepository.ensureTodayFixedTasks(templates)
                _uiState.update { it.copy(isSaving = false) }
                _setupCompleted.value = true
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = when (error) {
                            is InvalidAnchorException -> error.message
                            else -> "设置失败，请重试"
                        },
                    )
                }
            }
        }
    }

    private fun resolveDurationDays(state: SetupUiState): Int? {
        return if (state.isCustomDuration) {
            state.customDurationInput.toIntOrNull()
        } else {
            state.selectedPresetDays
        }
    }
}
