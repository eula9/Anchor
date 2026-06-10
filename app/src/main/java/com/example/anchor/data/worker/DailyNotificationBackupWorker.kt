package com.example.anchor.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.notification.IdentityNotificationManager
import com.example.anchor.data.notification.NotificationScheduler
import com.example.anchor.data.repository.IdentityRepositoryImpl
import com.example.anchor.util.NotificationPermissionHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 闹钟备用 Worker：在预定时刻稍后触发，防止小米等系统清除 AlarmManager 排期。
 *
 * 若当日通知已由闹钟发出则跳过，避免重复推送。
 */
class DailyNotificationBackupWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun doWork(): Result {
        return try {
            if (!NotificationPermissionHelper.isNotificationPermissionGranted(applicationContext)) {
                return Result.success()
            }

            val dataStore = UserPreferencesDataStore(applicationContext)
            if (!dataStore.isNotificationEnabled()) {
                return Result.success()
            }

            val today = LocalDate.now().format(dateFormatter)
            if (dataStore.getLastNotificationDate() == today) {
                scheduleNextBackup(dataStore)
                return Result.success()
            }

            val identityRepository = IdentityRepositoryImpl(userPreferencesDataStore = dataStore)
            val anchor = identityRepository.getActiveAnchor()
            if (anchor != null) {
                IdentityNotificationManager(applicationContext).showDailyIdentityNotification(anchor)
                dataStore.saveLastNotificationDate(today)
            }

            val scheduler = NotificationScheduler(applicationContext)
            val time = dataStore.getNotificationTime()
            scheduler.scheduleDailyNotification(time.hour, time.minute)
            scheduleNextBackup(dataStore)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }

    private suspend fun scheduleNextBackup(dataStore: UserPreferencesDataStore) {
        val time = dataStore.getNotificationTime()
        NotificationScheduler(applicationContext).scheduleDailyNotificationBackup(time.hour, time.minute)
    }
}
