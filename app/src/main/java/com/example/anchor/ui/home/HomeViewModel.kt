package com.example.anchor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.repository.EmptyTaskContentException
import com.example.anchor.data.repository.TaskLimitReachedException
import com.example.anchor.data.repository.TomorrowTaskLimitReachedException
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.StreakRepository
import com.example.anchor.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel。
 */
class HomeViewModel(
    private val identityRepository: IdentityRepository,
    private val taskRepository: TaskRepository,
    private val streakRepository: StreakRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            streakRepository.refreshDayBoundary()
            val templates = identityRepository.fixedTaskTemplates.first()
            taskRepository.ensureTodayFixedTasks(templates)
        }
        observeActiveAnchor()
        observeFixedTasks()
        observeOptionalTasks()
        observeTomorrowTasks()
        observeStreakInfo()
    }

    fun showAddOptionalDialog() {
        _uiState.update {
            it.copy(showOptionalDialog = true, optionalDialogInput = "", taskError = null)
        }
    }

    fun dismissOptionalDialog() {
        _uiState.update {
            it.copy(showOptionalDialog = false, optionalDialogInput = "", taskError = null)
        }
    }

    fun onOptionalDialogInputChange(text: String) {
        _uiState.update { it.copy(optionalDialogInput = text, taskError = null) }
    }

    fun confirmAddOptionalTask() {
        val content = _uiState.value.optionalDialogInput
        viewModelScope.launch {
            taskRepository.addOptionalTask(content)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showOptionalDialog = false,
                            optionalDialogInput = "",
                            taskError = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { state ->
                        state.copy(taskError = mapTaskError(error))
                    }
                }
        }
    }

    fun showAddTomorrowDialog() {
        _uiState.update {
            it.copy(showTomorrowDialog = true, tomorrowDialogInput = "", taskError = null)
        }
    }

    fun dismissTomorrowDialog() {
        _uiState.update {
            it.copy(showTomorrowDialog = false, tomorrowDialogInput = "", taskError = null)
        }
    }

    fun onTomorrowDialogInputChange(text: String) {
        _uiState.update { it.copy(tomorrowDialogInput = text, taskError = null) }
    }

    fun confirmAddTomorrowTask() {
        val content = _uiState.value.tomorrowDialogInput
        viewModelScope.launch {
            taskRepository.addTomorrowTask(content)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showTomorrowDialog = false,
                            tomorrowDialogInput = "",
                            taskError = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { state ->
                        state.copy(taskError = mapTaskError(error))
                    }
                }
        }
    }

    fun moveTomorrowTaskToToday(taskId: Long) {
        viewModelScope.launch {
            taskRepository.moveTomorrowTaskToToday(taskId)
                .onFailure { error ->
                    _uiState.update { state ->
                        state.copy(taskError = mapTaskError(error))
                    }
                }
        }
    }

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.completeTask(taskId)
        }
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

    private fun observeFixedTasks() {
        viewModelScope.launch {
            taskRepository.todayFixedTasks.collect { tasks ->
                _uiState.update { it.copy(fixedTasks = tasks, taskError = null) }
                if (tasks.isNotEmpty() && tasks.all { it.completed }) {
                    streakRepository.onAllFixedTasksCompleted()
                }
            }
        }
    }

    private fun observeOptionalTasks() {
        viewModelScope.launch {
            taskRepository.todayOptionalTasks.collect { tasks ->
                _uiState.update { it.copy(optionalTasks = tasks) }
            }
        }
    }

    private fun observeTomorrowTasks() {
        viewModelScope.launch {
            taskRepository.tomorrowTasks.collect { tasks ->
                _uiState.update { it.copy(tomorrowTasks = tasks) }
            }
        }
    }

    private fun observeStreakInfo() {
        viewModelScope.launch {
            streakRepository.streakInfo.collect { streakInfo ->
                _uiState.update { it.copy(streakInfo = streakInfo) }
            }
        }
    }

    private fun mapTaskError(error: Throwable): String = when (error) {
        is EmptyTaskContentException -> "请输入任务内容"
        is TaskLimitReachedException -> "今日可选任务已满"
        is TomorrowTaskLimitReachedException -> "明天想做的事已满"
        else -> "操作失败，请重试"
    }
}
