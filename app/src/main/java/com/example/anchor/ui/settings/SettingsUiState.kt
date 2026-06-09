package com.example.anchor.ui.settings

import androidx.work.WorkInfo
import com.example.anchor.domain.model.NotificationTime
import com.example.anchor.domain.model.ThemeMode

/**
 * 设置页 UI 状态。
 */
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isNotificationPermissionGranted: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val notificationTime: NotificationTime = NotificationTime(),
    val notificationWorkStatus: WorkInfo.State? = null,
    val maintenanceWorkStatus: WorkInfo.State? = null,
    val backupMessage: String? = null,
    val backupError: String? = null,
    val notificationError: String? = null,
)
