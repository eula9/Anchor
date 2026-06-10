package com.example.anchor.data.notification



import android.content.Context

import androidx.work.BackoffPolicy

import androidx.work.Constraints

import androidx.work.ExistingPeriodicWorkPolicy

import androidx.work.ExistingWorkPolicy

import androidx.work.OneTimeWorkRequestBuilder

import androidx.work.PeriodicWorkRequestBuilder

import androidx.work.WorkInfo

import androidx.work.WorkManager

import com.example.anchor.data.worker.AnchorExpiryWorker

import com.example.anchor.data.worker.DailyMaintenanceWorker

import com.example.anchor.data.worker.DailyNotificationBackupWorker

import com.example.anchor.util.Constants

import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.map

import java.time.Duration

import java.time.LocalDateTime

import java.time.LocalTime

import java.util.concurrent.TimeUnit



/**

 * 后台任务调度器。

 *

 * 每日身份通知使用 AlarmManager（进程结束后仍可触发）；

 * 数据维护与锚点到期检查仍使用 WorkManager。

 */

class NotificationScheduler(

    private val context: Context,

) {



    private val workManager = WorkManager.getInstance(context)



    /** 维护类任务约束：电量充足时执行 */

    private val maintenanceConstraints = Constraints.Builder()

        .setRequiresBatteryNotLow(true)

        .build()



    /**

     * 启动每日身份通知（AlarmManager 精确闹钟 + 滚动重调度）。

     */

    fun scheduleDailyNotification(

        hour: Int = Constants.DEFAULT_NOTIFICATION_HOUR,

        minute: Int = Constants.DEFAULT_NOTIFICATION_MINUTE,

    ): Boolean {

        cancelLegacyDailyNotificationWork()

        val scheduled = DailyNotificationAlarmScheduler.schedule(context, hour, minute)

        if (scheduled) {

            scheduleDailyNotificationBackup(hour, minute)

        }

        return scheduled

    }



    /**

     * 在闹钟时刻之后延迟触发备用 Worker（小米等系统闹钟被清时的兜底）。

     */

    fun scheduleDailyNotificationBackup(

        hour: Int = Constants.DEFAULT_NOTIFICATION_HOUR,

        minute: Int = Constants.DEFAULT_NOTIFICATION_MINUTE,

    ) {

        val alarmTriggerMillis = DailyNotificationAlarmScheduler.computeNextTriggerMillis(hour, minute)

        val backupDelayMillis = alarmTriggerMillis - System.currentTimeMillis() +

            TimeUnit.MINUTES.toMillis(Constants.NOTIFICATION_BACKUP_DELAY_MINUTES)

        if (backupDelayMillis <= 0) {

            return

        }



        val workRequest = OneTimeWorkRequestBuilder<DailyNotificationBackupWorker>()

            .setInitialDelay(backupDelayMillis, TimeUnit.MILLISECONDS)

            .addTag(Constants.WORKER_DAILY_NOTIFICATION_BACKUP)

            .build()



        workManager.enqueueUniqueWork(

            Constants.WORKER_DAILY_NOTIFICATION_BACKUP,

            ExistingWorkPolicy.REPLACE,

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

            .setConstraints(maintenanceConstraints)

            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)

            .addTag(Constants.WORKER_DAILY_MAINTENANCE)

            .build()



        workManager.enqueueUniquePeriodicWork(

            Constants.WORKER_DAILY_MAINTENANCE,

            ExistingPeriodicWorkPolicy.KEEP,

            workRequest,

        )

    }



    /** 启动锚点到期检查任务（每天 20:00） */

    fun scheduleAnchorExpiryCheck() {

        val initialDelayMillis = calculateInitialDelayMillis(20, 0)



        val workRequest = PeriodicWorkRequestBuilder<AnchorExpiryWorker>(

            24, TimeUnit.HOURS,

        )

            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)

            .setConstraints(maintenanceConstraints)

            .addTag(Constants.WORKER_ANCHOR_EXPIRY_CHECK)

            .build()



        workManager.enqueueUniquePeriodicWork(

            Constants.WORKER_ANCHOR_EXPIRY_CHECK,

            ExistingPeriodicWorkPolicy.KEEP,

            workRequest,

        )

    }



    /** 取消每日身份通知 */

    fun cancelDailyNotification() {

        cancelLegacyDailyNotificationWork()

        DailyNotificationAlarmScheduler.cancel(context)

        workManager.cancelUniqueWork(Constants.WORKER_DAILY_NOTIFICATION_BACKUP)

    }



    /** 观察身份通知调度状态（AlarmManager 已排期则视为 ENQUEUED） */

    fun observeNotificationWorkStatus(): Flow<WorkInfo.State?> {

        return kotlinx.coroutines.flow.flow {

            emit(

                if (DailyNotificationAlarmScheduler.isScheduled(context)) {

                    WorkInfo.State.ENQUEUED

                } else {

                    null

                },

            )

        }

    }



    /** 观察数据维护任务状态 */

    fun observeMaintenanceWorkStatus(): Flow<WorkInfo.State?> {

        return workManager.getWorkInfosForUniqueWorkFlow(Constants.WORKER_DAILY_MAINTENANCE)

            .map { infos -> infos.firstOrNull()?.state }

    }



    /** 取消旧版 WorkManager 每日通知任务（从 1.0 升级时清理） */

    private fun cancelLegacyDailyNotificationWork() {

        workManager.cancelUniqueWork(Constants.WORKER_DAILY_IDENTITY_NOTIFICATION)

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

