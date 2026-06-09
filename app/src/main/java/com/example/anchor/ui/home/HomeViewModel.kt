package com.example.anchor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.repository.EmptyTaskContentException
import com.example.anchor.data.repository.TaskLimitReachedException
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel。
 */
class HomeViewModel(
    private val identityRepository: IdentityRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeTodayIdentity()
        observeTodayTasks()
    }

    fun onTaskInputChange(text: String) {
        _uiState.update { it.copy(taskInput = text, taskError = null) }
    }

    fun addTask() {
        val content = _uiState.value.taskInput
        viewModelScope.launch {
            taskRepository.addTask(content)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(taskInput = "", taskError = null)
                    }
                }
                .onFailure { error ->
                    _uiState.update { state ->
                        state.copy(taskError = mapTaskError(error))
                    }
                }
        }
    }

    fun toggleTaskComplete(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.setTaskCompleted(taskId, completed)
        }
    }

    private fun observeTodayIdentity() {
        viewModelScope.launch {
            identityRepository.todayIdentity.collect { identity ->
                _uiState.update {
                    it.copy(isLoading = false, todayIdentity = identity)
                }
            }
        }
    }

    private fun observeTodayTasks() {
        viewModelScope.launch {
            taskRepository.todayTasks.collect { tasks ->
                _uiState.update { it.copy(todayTasks = tasks, taskError = null) }
            }
        }
    }

    private fun mapTaskError(error: Throwable): String = when (error) {
        is EmptyTaskContentException -> "请输入任务内容"
        is TaskLimitReachedException -> "今日任务已满，最多 3 件"
        else -> "操作失败，请重试"
    }
}
