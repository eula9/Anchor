package com.example.anchor.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.util.ReminderPermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 在开机、时区变化、应用更新等事件后恢复每日通知闹钟。
 */
class AlarmRescheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action !in RESCHEDULE_ACTIONS) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                restoreAlarmIfNeeded(appContext)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun restoreAlarmIfNeeded(context: Context) {
        if (!ReminderPermissionHelper.canScheduleReliableReminder(context)) {
            return
        }

        val dataStore = UserPreferencesDataStore(context)
        if (!dataStore.isNotificationEnabled()) {
            return
        }

        val time = dataStore.getNotificationTime()
        NotificationScheduler(context).scheduleDailyNotification(time.hour, time.minute)
    }

    private companion object {
        val RESCHEDULE_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
        )
    }
}
