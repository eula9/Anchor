package com.example.anchor.data.repository

import androidx.work.WorkInfo
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.notification.NotificationScheduler
import com.example.anchor.domain.model.ThemeMode
import com.example.anchor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 设置仓库实现类。
 */
class SettingsRepositoryImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val notificationScheduler: NotificationScheduler,
) : SettingsRepository {

    override val themeMode: Flow<ThemeMode> = userPreferencesDataStore.themeModeFlow

    override suspend fun setThemeMode(mode: ThemeMode) {
        userPreferencesDataStore.setThemeMode(mode)
    }

    override val notificationWorkStatus: Flow<WorkInfo.State?> =
        userPreferencesDataStore.notificationEnabledFlow.map { enabled ->
            if (enabled) WorkInfo.State.ENQUEUED else null
        }

    override val maintenanceWorkStatus: Flow<WorkInfo.State?> =
        notificationScheduler.observeMaintenanceWorkStatus()
}
