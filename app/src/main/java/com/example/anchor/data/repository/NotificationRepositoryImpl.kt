package com.example.anchor.data.repository

import android.content.Context
import android.content.Intent
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.notification.NotificationScheduler
import com.example.anchor.domain.model.NotificationTime
import com.example.anchor.domain.repository.NotificationRepository
import com.example.anchor.util.OemPermissionHelper
import com.example.anchor.util.OemVendor
import com.example.anchor.util.ReminderPermissionHelper
import kotlinx.coroutines.flow.Flow

/**
 * 通知仓库实现类。
 */
class NotificationRepositoryImpl(
    private val appContext: Context,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val notificationScheduler: NotificationScheduler,
) : NotificationRepository {

    override fun isPermissionGranted(): Boolean =
        ReminderPermissionHelper.isNotificationPermissionGranted(appContext)

    override fun requiresRuntimePermission(): Boolean =
        ReminderPermissionHelper.requiresNotificationPermission()

    override fun canScheduleExactAlarms(): Boolean =
        ReminderPermissionHelper.canScheduleExactAlarms(appContext)

    override fun needsExactAlarmPermission(): Boolean =
        ReminderPermissionHelper.needsExactAlarmPermission()

    override fun isBatteryOptimizationIgnored(): Boolean =
        ReminderPermissionHelper.isBatteryOptimizationIgnored(appContext)

    override fun createExactAlarmSettingsIntent(): Intent =
        ReminderPermissionHelper.createExactAlarmSettingsIntent(appContext)

    override fun createBatteryOptimizationIntent(): Intent =
        ReminderPermissionHelper.createBatteryOptimizationIntent(appContext)

    override fun getOemVendor(): OemVendor = OemPermissionHelper.detectOemVendor()

    override suspend fun isOemBackgroundConfirmed(): Boolean =
        userPreferencesDataStore.isOemBackgroundConfirmed()

    override suspend fun needsOemBackgroundSetup(): Boolean {
        return OemPermissionHelper.needsOemBackgroundSetup(
            isOemBackgroundConfirmed = userPreferencesDataStore.isOemBackgroundConfirmed(),
        )
    }

    override fun createOemBackgroundSettingsIntent(): Intent =
        OemPermissionHelper.createOemBackgroundSettingsIntent(appContext)

    override fun createNotificationSettingsIntent(): Intent =
        OemPermissionHelper.createNotificationSettingsIntent(appContext)

    override suspend fun confirmOemBackgroundSetup() {
        userPreferencesDataStore.setOemBackgroundConfirmed(true)
    }

    override val isNotificationEnabled: Flow<Boolean> =
        userPreferencesDataStore.notificationEnabledFlow

    override val notificationTime: Flow<NotificationTime> =
        userPreferencesDataStore.notificationTimeFlow

    override suspend fun enableDailyNotification(): Result<Unit> {
        if (!isPermissionGranted()) {
            return Result.failure(NotificationPermissionDeniedException())
        }
        if (needsExactAlarmPermission() && !canScheduleExactAlarms()) {
            return Result.failure(ExactAlarmPermissionRequiredException())
        }
        if (!isBatteryOptimizationIgnored()) {
            return Result.failure(BatteryOptimizationRequiredException())
        }
        if (OemPermissionHelper.needsOemBackgroundSetup(userPreferencesDataStore.isOemBackgroundConfirmed())) {
            return Result.failure(OemBackgroundRequiredException())
        }

        val time = userPreferencesDataStore.getNotificationTime()
        val scheduled = notificationScheduler.scheduleDailyNotification(time.hour, time.minute)
        if (!scheduled) {
            return Result.failure(ReminderScheduleFailedException())
        }

        userPreferencesDataStore.setNotificationEnabled(true)
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

    override suspend fun ensureReminderScheduled() {
        if (!userPreferencesDataStore.isNotificationEnabled()) return
        if (!ReminderPermissionHelper.canScheduleExactAlarms(appContext)) return
        val time = userPreferencesDataStore.getNotificationTime()
        notificationScheduler.scheduleDailyNotification(time.hour, time.minute)
    }
}

/** 通知权限未授予异常 */
class NotificationPermissionDeniedException : Exception("通知权限未授予")

/** 精确闹钟权限未授予异常 */
class ExactAlarmPermissionRequiredException : Exception("精确闹钟权限未授予")

/** 电池优化未豁免异常 */
class BatteryOptimizationRequiredException : Exception("电池优化未豁免")

/** 闹钟调度失败异常 */
class ReminderScheduleFailedException : Exception("闹钟调度失败")

/** 国产机后台设置未完成异常 */
class OemBackgroundRequiredException : Exception("厂商后台设置未完成")
