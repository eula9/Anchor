package com.example.anchor.domain.repository

import com.example.anchor.domain.model.NotificationTime
import kotlinx.coroutines.flow.Flow

/**
 * 通知仓库接口。
 */
interface NotificationRepository {

    fun isPermissionGranted(): Boolean

    fun requiresRuntimePermission(): Boolean

    val isNotificationEnabled: Flow<Boolean>

    /** 观察用户设定的通知推送时刻 */
    val notificationTime: Flow<NotificationTime>

    suspend fun enableDailyNotification(): Result<Unit>

    suspend fun disableDailyNotification()

    /**
     * 更新通知推送时刻。
     *
     * 若通知已开启，会立即按新时刻重新调度。
     */
    suspend fun setNotificationTime(hour: Int, minute: Int)
}
