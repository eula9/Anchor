package com.example.anchor.domain.repository

import android.content.Intent
import com.example.anchor.domain.model.NotificationTime
import com.example.anchor.util.OemVendor
import kotlinx.coroutines.flow.Flow

/**
 * 通知仓库接口。
 */
interface NotificationRepository {

    fun isPermissionGranted(): Boolean

    fun requiresRuntimePermission(): Boolean

    fun canScheduleExactAlarms(): Boolean

    fun needsExactAlarmPermission(): Boolean

    fun isBatteryOptimizationIgnored(): Boolean

    fun getOemVendor(): OemVendor

    suspend fun isOemBackgroundConfirmed(): Boolean

    suspend fun needsOemBackgroundSetup(): Boolean

    fun createExactAlarmSettingsIntent(): Intent

    fun createBatteryOptimizationIntent(): Intent

    fun createOemBackgroundSettingsIntent(): Intent

    fun createNotificationSettingsIntent(): Intent

    suspend fun confirmOemBackgroundSetup()

    val isNotificationEnabled: Flow<Boolean>

    val notificationTime: Flow<NotificationTime>

    suspend fun enableDailyNotification(): Result<Unit>

    suspend fun disableDailyNotification()

    suspend fun setNotificationTime(hour: Int, minute: Int)

    suspend fun ensureReminderScheduled()
}
