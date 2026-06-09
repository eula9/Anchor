package com.example.anchor

import android.app.Application
import com.example.anchor.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Anchor 应用程序入口类。
 */
class AnchorApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
        appContainer.identityNotificationManager.createNotificationChannel()

        // 始终注册每日维护任务
        appContainer.notificationScheduler.scheduleDailyMaintenance()

        restoreNotificationScheduleIfNeeded()
    }

    private fun restoreNotificationScheduleIfNeeded() {
        val container = appContainer
        if (!container.notificationRepository.isPermissionGranted()) return

        applicationScope.launch {
            if (container.userPreferencesDataStore.isNotificationEnabled()) {
                val time = container.userPreferencesDataStore.getNotificationTime()
                container.notificationScheduler.scheduleDailyNotification(time.hour, time.minute)
            }
        }
    }
}
