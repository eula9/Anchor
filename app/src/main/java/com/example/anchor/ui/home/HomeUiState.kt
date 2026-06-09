package com.example.anchor.ui.home

import com.example.anchor.domain.model.Identity
import com.example.anchor.domain.model.StreakInfo
import com.example.anchor.domain.model.Task
import com.example.anchor.util.Constants

/**
 * 首页 UI 状态数据类。
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val todayIdentity: Identity? = null,
    val todayTasks: List<Task> = emptyList(),
    val streakInfo: StreakInfo = StreakInfo(),
    val taskInput: String = "",
    val taskError: String? = null,
) {
    val canAddMoreTasks: Boolean
        get() = todayTasks.size < Constants.MAX_DAILY_TASKS
}
