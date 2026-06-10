package com.example.anchor.data.notification

import android.content.Context
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.util.ReminderPermissionHelper

/**
 * 在应用启动 / 恢复时重新写入闹钟。
 *
 * 小米等国产系统划掉后台后可能清除 AlarmManager 排期，每次打开应用时补登记提升可靠性。
 */
object ReminderRescheduleHelper {

    suspend fun ensureScheduled(context: Context) {
        if (!ReminderPermissionHelper.isNotificationPermissionGranted(context)) {
            return
        }
        if (!ReminderPermissionHelper.canScheduleExactAlarms(context)) {
            return
        }

        val dataStore = UserPreferencesDataStore(context)
        if (!dataStore.isNotificationEnabled()) {
            return
        }

        val time = dataStore.getNotificationTime()
        val scheduler = NotificationScheduler(context.applicationContext)
        scheduler.scheduleDailyNotification(time.hour, time.minute)
    }
}
