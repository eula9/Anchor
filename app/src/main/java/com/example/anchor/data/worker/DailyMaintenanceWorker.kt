package com.example.anchor.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.anchor.AnchorApplication
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 每日数据维护 Worker。
 *
 * 负责清理过期任务等日常维护操作。
 */
class DailyMaintenanceWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val container = (applicationContext as AnchorApplication).appContainer
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            container.database.taskDao().deleteTasksNotOnDate(today)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
