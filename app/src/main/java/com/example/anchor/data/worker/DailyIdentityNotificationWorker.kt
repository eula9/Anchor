package com.example.anchor.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.anchor.AnchorApplication
import com.example.anchor.util.NotificationPermissionHelper

/**
 * 每日身份锚点通知 Worker。
 */
class DailyIdentityNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (!NotificationPermissionHelper.isNotificationPermissionGranted(applicationContext)) {
                return Result.success()
            }

            val container = (applicationContext as AnchorApplication).appContainer
            if (!container.userPreferencesDataStore.isNotificationEnabled()) {
                return Result.success()
            }

            val anchor = container.identityRepository.getActiveAnchor() ?: return Result.success()
            container.identityNotificationManager.showDailyIdentityNotification(anchor)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
