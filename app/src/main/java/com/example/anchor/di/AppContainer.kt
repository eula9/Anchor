package com.example.anchor.di

import android.content.Context
import androidx.room.Room
import com.example.anchor.data.local.AnchorDatabase
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.notification.IdentityNotificationManager
import com.example.anchor.data.notification.NotificationScheduler
import com.example.anchor.data.repository.BackupRepositoryImpl
import com.example.anchor.data.repository.IdentityRepositoryImpl
import com.example.anchor.data.repository.NotificationRepositoryImpl
import com.example.anchor.data.repository.SettingsRepositoryImpl
import com.example.anchor.data.repository.TaskRepositoryImpl
import com.example.anchor.domain.repository.BackupRepository
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.NotificationRepository
import com.example.anchor.domain.repository.SettingsRepository
import com.example.anchor.domain.repository.TaskRepository

/**
 * 应用级依赖容器。
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: AnchorDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            AnchorDatabase::class.java,
            AnchorDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val userPreferencesDataStore: UserPreferencesDataStore by lazy {
        UserPreferencesDataStore(appContext)
    }

    val identityNotificationManager: IdentityNotificationManager by lazy {
        IdentityNotificationManager(appContext)
    }

    val notificationScheduler: NotificationScheduler by lazy {
        NotificationScheduler(appContext)
    }

    val identityRepository: IdentityRepository by lazy {
        IdentityRepositoryImpl(userPreferencesDataStore = userPreferencesDataStore)
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(taskDao = database.taskDao())
    }

    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(
            appContext = appContext,
            userPreferencesDataStore = userPreferencesDataStore,
            identityRepository = identityRepository,
            notificationManager = identityNotificationManager,
            notificationScheduler = notificationScheduler,
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(
            userPreferencesDataStore = userPreferencesDataStore,
            notificationScheduler = notificationScheduler,
        )
    }

    val backupRepository: BackupRepository by lazy {
        BackupRepositoryImpl(
            appContext = appContext,
            userPreferencesDataStore = userPreferencesDataStore,
            taskDao = database.taskDao(),
            notificationScheduler = notificationScheduler,
        )
    }
}
