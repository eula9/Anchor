package com.example.anchor.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.repository.NotificationPermissionDeniedException
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

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isNotificationPermissionGranted = notificationRepository.isPermissionGranted(),
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
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

    fun requestEnableNotification() {
        if (notificationRepository.requiresRuntimePermission() &&
            !notificationRepository.isPermissionGranted()
        ) {
            return
        }
        enableNotification()
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                isNotificationPermissionGranted = granted,
                notificationError = if (granted) null else "需要通知权限才能开启提醒",
            )
        }
        if (granted) enableNotification()
    }

    fun disableNotification() {
        viewModelScope.launch {
            notificationRepository.disableDailyNotification()
            _uiState.update { it.copy(notificationError = null) }
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationRepository.setNotificationTime(hour, minute)
        }
    }

    fun refreshPermissionState() {
        _uiState.update {
            it.copy(isNotificationPermissionGranted = notificationRepository.isPermissionGranted())
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

    private fun enableNotification() {
        viewModelScope.launch {
            notificationRepository.enableDailyNotification()
                .onSuccess {
                    _uiState.update { it.copy(notificationError = null) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            notificationError = when (error) {
                                is NotificationPermissionDeniedException -> "请先授予通知权限"
                                else -> "开启提醒失败，请重试"
                            },
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
