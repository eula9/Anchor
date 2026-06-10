package com.example.anchor.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.anchor.MainActivity
import com.example.anchor.util.Constants
import com.example.anchor.util.ReminderPermissionHelper
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 使用 AlarmManager 调度每日身份通知。
 *
 * 优先使用 [AlarmManager.setAlarmClock]，在应用被杀后台后仍较可靠地触发。
 */
object DailyNotificationAlarmScheduler {

    private const val TAG = "DailyNotificationAlarm"

    /**
     * 调度下一次每日通知（滚动式：每次触发后再排下一天）。
     *
     * @return 是否成功写入系统闹钟
     */
    fun schedule(
        context: Context,
        hour: Int = Constants.DEFAULT_NOTIFICATION_HOUR,
        minute: Int = Constants.DEFAULT_NOTIFICATION_MINUTE,
    ): Boolean {
        if (!ReminderPermissionHelper.canScheduleExactAlarms(context)) {
            Log.w(TAG, "精确闹钟权限未授予，无法调度")
            return false
        }

        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
        val pendingIntent = createAlarmPendingIntent(context) ?: return false
        val triggerAtMillis = computeNextTriggerMillis(hour, minute)

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val showIntent = PendingIntent.getActivity(
                    context,
                    Constants.ALARM_REQUEST_DAILY_NOTIFICATION_SHOW,
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
                    pendingIntent,
                )
            } else {
                @Suppress("DEPRECATION")
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
            }
            true
        } catch (e: SecurityException) {
            Log.e(TAG, "调度闹钟失败", e)
            false
        }
    }

    /** 取消已调度的每日通知闹钟 */
    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent = createAlarmPendingIntent(context, mutable = false) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    /** 是否已存在待触发的每日通知闹钟 */
    fun isScheduled(context: Context): Boolean {
        return createAlarmPendingIntent(context, mutable = false) != null
    }

    private fun createAlarmPendingIntent(
        context: Context,
        mutable: Boolean = true,
    ): PendingIntent? {
        val intent = Intent(context, DailyNotificationReceiver::class.java).apply {
            action = Constants.ACTION_DAILY_NOTIFICATION
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(
            context,
            Constants.ALARM_REQUEST_DAILY_NOTIFICATION,
            intent,
            if (mutable) flags else flags or PendingIntent.FLAG_NO_CREATE,
        )
    }

    fun computeNextTriggerMillis(
        hour: Int,
        minute: Int,
        from: LocalDateTime = LocalDateTime.now(),
    ): Long {
        val targetTime = LocalTime.of(hour, minute)
        var nextRun = from.with(targetTime)
        if (!from.isBefore(nextRun)) {
            nextRun = nextRun.plusDays(1)
        }
        return nextRun.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
