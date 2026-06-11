package com.example.anchor.ui.home

import com.example.anchor.domain.model.IdentityAnchor
import com.example.anchor.domain.model.StreakInfo
import com.example.anchor.domain.model.Task
import com.example.anchor.util.Constants

/**
 * 首页 UI 状态。
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val activeAnchor: IdentityAnchor? = null,
    val fixedTasks: List<Task> = emptyList(),
    val optionalTasks: List<Task> = emptyList(),
    val tomorrowTasks: List<Task> = emptyList(),
    val streakInfo: StreakInfo = StreakInfo(),
    val showOptionalDialog: Boolean = false,
    val showTomorrowDialog: Boolean = false,
    val optionalDialogInput: String = "",
    val tomorrowDialogInput: String = "",
    val taskError: String? = null,
) {
    val canAddMoreOptionalTasks: Boolean
        get() = optionalTasks.size < Constants.MAX_OPTIONAL_TASKS

    val canAddMoreTomorrowTasks: Boolean
        get() = tomorrowTasks.size < Constants.MAX_OPTIONAL_TASKS

    val canMoveTomorrowToToday: Boolean
        get() = optionalTasks.size < Constants.MAX_OPTIONAL_TASKS
}
