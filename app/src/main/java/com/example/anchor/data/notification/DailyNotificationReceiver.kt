package com.example.anchor.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.repository.IdentityRepositoryImpl
import com.example.anchor.util.Constants
import com.example.anchor.util.ReminderPermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 每日身份通知闹钟接收器。
 *
 * 不依赖应用进程是否存活，冷启动时直接读取 DataStore 并发送通知。
 */
class DailyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Constants.ACTION_DAILY_NOTIFICATION) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                handleDailyNotification(appContext)
            } catch (e: Exception) {
                Log.e(TAG, "处理每日通知失败", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleDailyNotification(context: Context) {
        if (!ReminderPermissionHelper.isNotificationPermissionGranted(context)) {
            return
        }

        val dataStore = UserPreferencesDataStore(context)
        if (!dataStore.isNotificationEnabled()) {
            DailyNotificationAlarmScheduler.cancel(context)
            return
        }

        val identityRepository = IdentityRepositoryImpl(userPreferencesDataStore = dataStore)
        val notificationManager = IdentityNotificationManager(context)
        notificationManager.createNotificationChannel()

        val anchor = identityRepository.getActiveAnchor()
        if (anchor != null) {
            notificationManager.showDailyIdentityNotification(anchor)
            dataStore.saveLastNotificationDate(
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE),
            )
        }

        val time = dataStore.getNotificationTime()
        NotificationScheduler(context).scheduleDailyNotification(time.hour, time.minute)
    }

    private companion object {
        const val TAG = "DailyNotificationReceiver"
    }
}
