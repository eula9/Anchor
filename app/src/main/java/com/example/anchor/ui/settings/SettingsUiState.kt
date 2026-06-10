package com.example.anchor.ui.settings

import androidx.work.WorkInfo
import com.example.anchor.domain.model.NotificationTime
import com.example.anchor.domain.model.ThemeMode
import com.example.anchor.util.OemVendor

/**
 * 开启提醒前的权限引导步骤。
 */
enum class ReminderSetupStep {
    NOTIFICATION,
    EXACT_ALARM,
    BATTERY_OPTIMIZATION,
    OEM_BACKGROUND,
}

/**
 * 设置页 UI 状态。
 */
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isNotificationPermissionGranted: Boolean = false,
    val canScheduleExactAlarms: Boolean = true,
    val isBatteryOptimizationIgnored: Boolean = true,
    val oemVendor: OemVendor = OemVendor.STANDARD,
    val isOemBackgroundConfirmed: Boolean = false,
    val awaitingOemConfirm: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val notificationTime: NotificationTime = NotificationTime(),
    val notificationWorkStatus: WorkInfo.State? = null,
    val maintenanceWorkStatus: WorkInfo.State? = null,
    val backupMessage: String? = null,
    val backupError: String? = null,
    val notificationError: String? = null,
    val reminderSetupStep: ReminderSetupStep? = null,
)
