package com.example.anchor.data.repository

import android.content.Context
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.notification.IdentityNotificationManager
import com.example.anchor.data.notification.NotificationScheduler
import com.example.anchor.domain.model.NotificationTime
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.NotificationRepository
import com.example.anchor.util.NotificationPermissionHelper
import kotlinx.coroutines.flow.Flow

/**
 * 通知仓库实现类。
 */
class NotificationRepositoryImpl(
    private val appContext: Context,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val identityRepository: IdentityRepository,
    private val notificationManager: IdentityNotificationManager,
    private val notificationScheduler: NotificationScheduler,
) : NotificationRepository {

    override fun isPermissionGranted(): Boolean =
        NotificationPermissionHelper.isNotificationPermissionGranted(appContext)

    override fun requiresRuntimePermission(): Boolean =
        NotificationPermissionHelper.requiresRuntimePermission()

    override val isNotificationEnabled: Flow<Boolean> =
        userPreferencesDataStore.notificationEnabledFlow

    override val notificationTime: Flow<NotificationTime> =
        userPreferencesDataStore.notificationTimeFlow

    override suspend fun enableDailyNotification(): Result<Unit> {
        if (!isPermissionGranted()) {
            return Result.failure(NotificationPermissionDeniedException())
        }

        val time = userPreferencesDataStore.getNotificationTime()
        userPreferencesDataStore.setNotificationEnabled(true)
        notificationScheduler.scheduleDailyNotification(time.hour, time.minute)

        val identity = identityRepository.getTodayIdentity()
        notificationManager.showDailyIdentityNotification(identity)

        return Result.success(Unit)
    }

    override suspend fun disableDailyNotification() {
        userPreferencesDataStore.setNotificationEnabled(false)
        notificationScheduler.cancelDailyNotification()
    }

    override suspend fun setNotificationTime(hour: Int, minute: Int) {
        userPreferencesDataStore.saveNotificationTime(hour, minute)
        if (userPreferencesDataStore.isNotificationEnabled()) {
            notificationScheduler.scheduleDailyNotification(hour, minute)
        }
    }
}

/** 通知权限未授予异常 */
class NotificationPermissionDeniedException : Exception("通知权限未授予")
