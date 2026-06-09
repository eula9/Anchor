package com.example.anchor.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anchor.domain.repository.BackupRepository
import com.example.anchor.domain.repository.NotificationRepository
import com.example.anchor.domain.repository.SettingsRepository

/**
 * SettingsViewModel 工厂类。
 */
class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val backupRepository: BackupRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                settingsRepository = settingsRepository,
                notificationRepository = notificationRepository,
                backupRepository = backupRepository,
            ) as T
        }
        throw IllegalArgumentException("未知的 ViewModel 类型: ${modelClass.name}")
    }
}
