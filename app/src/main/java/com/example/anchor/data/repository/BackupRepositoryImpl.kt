package com.example.anchor.data.repository

import android.content.Context
import android.net.Uri
import com.example.anchor.data.backup.BackupJsonSerializer
import com.example.anchor.data.local.dao.TaskDao
import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.local.entity.TaskEntity
import com.example.anchor.data.notification.NotificationScheduler
import com.example.anchor.domain.model.BackupData
import com.example.anchor.domain.model.BackupTask
import com.example.anchor.domain.model.ThemeMode
import com.example.anchor.domain.repository.BackupRepository
import com.example.anchor.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 数据备份仓库实现类。
 */
class BackupRepositoryImpl(
    private val appContext: Context,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val taskDao: TaskDao,
    private val notificationScheduler: NotificationScheduler,
) : BackupRepository {

    override suspend fun exportBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val prefs = userPreferencesDataStore.getAppPreferences()
            val tasks = taskDao.getAllTasks()

            val backup = BackupData(
                version = Constants.BACKUP_VERSION,
                exportTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                identityDate = prefs.identityDate,
                identityIndex = prefs.identityIndex,
                notificationEnabled = prefs.notificationEnabled,
                notificationHour = prefs.notificationHour,
                notificationMinute = prefs.notificationMinute,
                themeMode = prefs.themeMode.name,
                tasks = tasks.map { entity ->
                    BackupTask(
                        content = entity.content,
                        completed = entity.completed,
                        date = entity.date,
                    )
                },
            )

            val json = BackupJsonSerializer.toJson(backup)
            appContext.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(json.toByteArray(Charsets.UTF_8))
            } ?: error("无法写入备份文件")
        }
    }

    override suspend fun importBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val json = appContext.contentResolver.openInputStream(uri)?.use { stream ->
                stream.readBytes().toString(Charsets.UTF_8)
            } ?: error("无法读取备份文件")

            val backup = BackupJsonSerializer.fromJson(json)

            userPreferencesDataStore.restoreAppPreferences(
                UserPreferencesDataStore.AppPreferences(
                    identityDate = backup.identityDate,
                    identityIndex = backup.identityIndex,
                    notificationEnabled = backup.notificationEnabled,
                    notificationHour = backup.notificationHour,
                    notificationMinute = backup.notificationMinute,
                    themeMode = ThemeMode.fromString(backup.themeMode),
                ),
            )

            taskDao.replaceAllTasks(
                backup.tasks.map { task ->
                    TaskEntity(
                        content = task.content,
                        completed = task.completed,
                        date = task.date,
                    )
                },
            )

            // 根据恢复后的通知设置重新调度 WorkManager
            if (backup.notificationEnabled) {
                notificationScheduler.scheduleDailyNotification(
                    backup.notificationHour,
                    backup.notificationMinute,
                )
            } else {
                notificationScheduler.cancelDailyNotification()
            }
        }
    }
}
