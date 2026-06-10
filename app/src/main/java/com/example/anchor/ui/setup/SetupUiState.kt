package com.example.anchor.ui.setup

import com.example.anchor.util.Constants

/**
 * 首次设置页 UI 状态。
 */
data class SetupUiState(
    val identityInput: String = "",
    val isCustomDuration: Boolean = false,
    val selectedPresetDays: Int = Constants.ANCHOR_PRESET_DURATIONS[1],
    val customDurationInput: String = "",
    val fixedTaskInputs: List<String> = List(Constants.MIN_FIXED_TASKS) { "" },
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
) {
    val canAddMoreFixedTasks: Boolean
        get() = fixedTaskInputs.size < Constants.MAX_FIXED_TASKS

    val canRemoveFixedTask: Boolean
        get() = fixedTaskInputs.size > Constants.MIN_FIXED_TASKS
}
