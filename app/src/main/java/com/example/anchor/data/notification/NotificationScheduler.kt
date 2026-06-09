package com.example.anchor.data.notification

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.anchor.data.worker.DailyIdentityNotificationWorker
import com.example.anchor.data.worker.DailyMaintenanceWorker
import com.example.anchor.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * WorkManager 任务调度器。
 *
 * 负责每日身份通知与每日数据维护的定时调度。
 */
class NotificationScheduler(
    private val context: Context,
) {

    private val workManager = WorkManager.getInstance(context)

    /** 通用任务约束：电量充足时执行 */
    private val defaultConstraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    /**
     * 启动每日身份通知任务。
     */
    fun scheduleDailyNotification(
        hour: Int = Constants.DEFAULT_NOTIFICATION_HOUR,
        minute: Int = Constants.DEFAULT_NOTIFICATION_MINUTE,
    ) {
        val initialDelayMillis = calculateInitialDelayMillis(hour, minute)

        val workRequest = PeriodicWorkRequestBuilder<DailyIdentityNotificationWorker>(
            24, TimeUnit.HOURS,
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setConstraints(defaultConstraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .addTag(Constants.WORKER_DAILY_IDENTITY_NOTIFICATION)
            .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.WORKER_DAILY_IDENTITY_NOTIFICATION,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }

    /**
     * 启动每日数据维护任务（清理过期任务等）。
     *
     * 默认每天凌晨 0:05 执行。
     */
    fun scheduleDailyMaintenance() {
        val initialDelayMillis = calculateInitialDelayMillis(0, 5)

        val workRequest = PeriodicWorkRequestBuilder<DailyMaintenanceWorker>(
            24, TimeUnit.HOURS,
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setConstraints(defaultConstraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
            .addTag(Constants.WORKER_DAILY_MAINTENANCE)
            .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.WORKER_DAILY_MAINTENANCE,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
    }

    /** 取消每日身份通知任务 */
    fun cancelDailyNotification() {
        workManager.cancelUniqueWork(Constants.WORKER_DAILY_IDENTITY_NOTIFICATION)
    }

    /** 观察身份通知任务状态 */
    fun observeNotificationWorkStatus(): Flow<WorkInfo.State?> {
        return workManager.getWorkInfosForUniqueWorkFlow(Constants.WORKER_DAILY_IDENTITY_NOTIFICATION)
            .map { infos -> infos.firstOrNull()?.state }
    }

    /** 观察数据维护任务状态 */
    fun observeMaintenanceWorkStatus(): Flow<WorkInfo.State?> {
        return workManager.getWorkInfosForUniqueWorkFlow(Constants.WORKER_DAILY_MAINTENANCE)
            .map { infos -> infos.firstOrNull()?.state }
    }

    private fun calculateInitialDelayMillis(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(hour, minute)
        var nextRun = now.with(targetTime)
        if (!now.isBefore(nextRun)) {
            nextRun = nextRun.plusDays(1)
        }
        return Duration.between(now, nextRun).toMillis()
    }
}
