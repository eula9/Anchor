package com.example.anchor

import android.app.Application
import com.example.anchor.di.AppContainer
import com.example.anchor.util.ReminderPermissionHelper
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

        // 注册后台维护与锚点到期检查
        appContainer.notificationScheduler.scheduleDailyMaintenance()
        appContainer.notificationScheduler.scheduleAnchorExpiryCheck()

        restoreNotificationScheduleIfNeeded()
    }

    private fun restoreNotificationScheduleIfNeeded() {
        val container = appContainer
        if (!ReminderPermissionHelper.canScheduleReliableReminder(this)) return

        applicationScope.launch {
            container.notificationRepository.ensureReminderScheduled()
        }
    }
}
