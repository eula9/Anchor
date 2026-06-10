package com.example.anchor.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.repository.BatteryOptimizationRequiredException
import com.example.anchor.data.repository.ExactAlarmPermissionRequiredException
import com.example.anchor.data.repository.NotificationPermissionDeniedException
import com.example.anchor.data.repository.OemBackgroundRequiredException
import com.example.anchor.data.repository.ReminderScheduleFailedException
import com.example.anchor.domain.model.ThemeMode
import com.example.anchor.domain.repository.BackupRepository
import com.example.anchor.domain.repository.NotificationRepository
import com.example.anchor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 设置页 ViewModel。
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val backupRepository: BackupRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var pendingEnableReminder = false
    private var hasPromptedExactAlarmSettings = false
    private var hasPromptedBatterySettings = false
    private var hasPromptedOemSettings = false

    init {
        refreshPermissionState()
        observeThemeMode()
        observeNotificationEnabled()
        observeNotificationTime()
        observeWorkStatus()
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun beginEnableReminder() {
        pendingEnableReminder = true
        resetSettingsPromptFlags()
        continueReminderSetup()
    }

    fun onExactAlarmSettingsOpened() {
        hasPromptedExactAlarmSettings = true
        clearReminderSetupStep()
    }

    fun onBatterySettingsOpened() {
        hasPromptedBatterySettings = true
        clearReminderSetupStep()
    }

    fun onOemSettingsOpened() {
        hasPromptedOemSettings = true
        _uiState.update {
            it.copy(awaitingOemConfirm = true, reminderSetupStep = null)
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        if (granted) {
            refreshPermissionState()
            continueReminderSetup()
        } else {
            pendingEnableReminder = false
            _uiState.update {
                it.copy(
                    notificationError = "需要通知权限才能开启提醒",
                    reminderSetupStep = null,
                    awaitingOemConfirm = false,
                )
            }
        }
    }

    fun continueReminderSetup() {
        if (!pendingEnableReminder) return

        viewModelScope.launch {
            refreshPermissionStateInternal()
            val state = _uiState.value

            when {
                notificationRepository.requiresRuntimePermission() &&
                    !state.isNotificationPermissionGranted -> {
                    _uiState.update {
                        it.copy(reminderSetupStep = ReminderSetupStep.NOTIFICATION, notificationError = null)
                    }
                }

                notificationRepository.needsExactAlarmPermission() &&
                    !state.canScheduleExactAlarms -> {
                    if (hasPromptedExactAlarmSettings) {
                        _uiState.update {
                            it.copy(
                                reminderSetupStep = null,
                                notificationError = "请允许精确闹钟后，再次点击「开启每日提醒」",
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                reminderSetupStep = ReminderSetupStep.EXACT_ALARM,
                                notificationError = null,
                            )
                        }
                    }
                }

                !state.isBatteryOptimizationIgnored -> {
                    if (hasPromptedBatterySettings) {
                        _uiState.update {
                            it.copy(
                                reminderSetupStep = null,
                                notificationError = "请允许忽略电池优化后，再次点击「开启每日提醒」",
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                reminderSetupStep = ReminderSetupStep.BATTERY_OPTIMIZATION,
                                notificationError = null,
                            )
                        }
                    }
                }

                notificationRepository.needsOemBackgroundSetup() -> {
                    if (hasPromptedOemSettings || state.awaitingOemConfirm) {
                        _uiState.update {
                            it.copy(
                                reminderSetupStep = null,
                                awaitingOemConfirm = true,
                                notificationError = "完成自启动设置后，请点击下方「我已完成设置」",
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                reminderSetupStep = ReminderSetupStep.OEM_BACKGROUND,
                                awaitingOemConfirm = false,
                                notificationError = null,
                            )
                        }
                    }
                }

                else -> {
                    _uiState.update {
                        it.copy(reminderSetupStep = null, awaitingOemConfirm = false)
                    }
                    enableNotification()
                }
            }
        }
    }

    fun clearReminderSetupStep() {
        _uiState.update { it.copy(reminderSetupStep = null) }
    }

    fun onOemBackgroundConfirmed() {
        viewModelScope.launch {
            notificationRepository.confirmOemBackgroundSetup()
            hasPromptedOemSettings = false
            refreshPermissionStateInternal()
            if (pendingEnableReminder) {
                continueReminderSetup()
            } else {
                notificationRepository.ensureReminderScheduled()
                _uiState.update {
                    it.copy(awaitingOemConfirm = false, notificationError = null)
                }
            }
        }
    }

    fun openOemBackgroundSettingsAgain() {
        hasPromptedOemSettings = false
        _uiState.update {
            it.copy(
                reminderSetupStep = ReminderSetupStep.OEM_BACKGROUND,
                awaitingOemConfirm = false,
            )
        }
    }

    fun disableNotification() {
        pendingEnableReminder = false
        viewModelScope.launch {
            notificationRepository.disableDailyNotification()
            _uiState.update {
                it.copy(notificationError = null, reminderSetupStep = null, awaitingOemConfirm = false)
            }
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationRepository.setNotificationTime(hour, minute)
        }
    }

    fun refreshPermissionState() {
        viewModelScope.launch {
            refreshPermissionStateInternal()
        }
    }

    fun ensureReminderScheduled() {
        viewModelScope.launch {
            notificationRepository.ensureReminderScheduled()
        }
    }

    fun requiresNotificationPermission(): Boolean =
        notificationRepository.requiresRuntimePermission() &&
            !notificationRepository.isPermissionGranted()

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            backupRepository.exportBackup(uri)
                .onSuccess {
                    _uiState.update {
                        it.copy(backupMessage = "备份导出成功", backupError = null)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(backupError = "备份导出失败", backupMessage = null)
                    }
                }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            backupRepository.importBackup(uri)
                .onSuccess {
                    _uiState.update {
                        it.copy(backupMessage = "备份恢复成功", backupError = null)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(backupError = "备份恢复失败，请检查文件格式", backupMessage = null)
                    }
                }
        }
    }

    fun clearBackupMessage() {
        _uiState.update { it.copy(backupMessage = null, backupError = null) }
    }

    private suspend fun refreshPermissionStateInternal() {
        _uiState.update {
            it.copy(
                isNotificationPermissionGranted = notificationRepository.isPermissionGranted(),
                canScheduleExactAlarms = notificationRepository.canScheduleExactAlarms(),
                isBatteryOptimizationIgnored = notificationRepository.isBatteryOptimizationIgnored(),
                oemVendor = notificationRepository.getOemVendor(),
                isOemBackgroundConfirmed = notificationRepository.isOemBackgroundConfirmed(),
            )
        }
    }

    private fun enableNotification() {
        viewModelScope.launch {
            notificationRepository.enableDailyNotification()
                .onSuccess {
                    pendingEnableReminder = false
                    resetSettingsPromptFlags()
                    _uiState.update {
                        it.copy(notificationError = null, reminderSetupStep = null, awaitingOemConfirm = false)
                    }
                }
                .onFailure { error ->
                    pendingEnableReminder = false
                    resetSettingsPromptFlags()
                    _uiState.update {
                        it.copy(
                            notificationError = when (error) {
                                is NotificationPermissionDeniedException -> "请先授予通知权限"
                                is ExactAlarmPermissionRequiredException -> "请允许精确闹钟权限"
                                is BatteryOptimizationRequiredException -> "请允许忽略电池优化"
                                is OemBackgroundRequiredException ->
                                    "请完成${notificationRepository.getOemVendor().displayName}后台设置"
                                is ReminderScheduleFailedException -> "闹钟调度失败，请检查系统权限后重试"
                                else -> "开启提醒失败，请重试"
                            },
                            reminderSetupStep = null,
                            awaitingOemConfirm = false,
                        )
                    }
                }
        }
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            settingsRepository.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    private fun observeNotificationEnabled() {
        viewModelScope.launch {
            notificationRepository.isNotificationEnabled.collect { enabled ->
                _uiState.update { it.copy(isNotificationEnabled = enabled) }
            }
        }
    }

    private fun observeNotificationTime() {
        viewModelScope.launch {
            notificationRepository.notificationTime.collect { time ->
                _uiState.update { it.copy(notificationTime = time) }
            }
        }
    }

    private fun resetSettingsPromptFlags() {
        hasPromptedExactAlarmSettings = false
        hasPromptedBatterySettings = false
        hasPromptedOemSettings = false
    }

    private fun observeWorkStatus() {
        viewModelScope.launch {
            settingsRepository.notificationWorkStatus.collect { status ->
                _uiState.update { it.copy(notificationWorkStatus = status) }
            }
        }
        viewModelScope.launch {
            settingsRepository.maintenanceWorkStatus.collect { status ->
                _uiState.update { it.copy(maintenanceWorkStatus = status) }
            }
        }
    }
}
