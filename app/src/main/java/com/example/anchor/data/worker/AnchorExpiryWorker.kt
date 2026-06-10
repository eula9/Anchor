package com.example.anchor.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.anchor.AnchorApplication
import com.example.anchor.util.NotificationPermissionHelper

/**
 * 身份锚点到期检查 Worker。
 */
class AnchorExpiryWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (!NotificationPermissionHelper.isNotificationPermissionGranted(applicationContext)) {
                return Result.success()
            }

            val container = (applicationContext as AnchorApplication).appContainer
            val anchor = container.identityRepository.getActiveAnchor() ?: return Result.success()

            if (anchor.isExpired) {
                container.identityNotificationManager.showAnchorExpiryNotification(anchor)
            }

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
