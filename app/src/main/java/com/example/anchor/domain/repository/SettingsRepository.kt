package com.example.anchor.domain.repository

import androidx.work.WorkInfo
import com.example.anchor.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * 设置仓库接口。
 */
interface SettingsRepository {

    /** 观察主题模式 */
    val themeMode: Flow<ThemeMode>

    /** 设置主题模式 */
    suspend fun setThemeMode(mode: ThemeMode)

    /** 观察身份通知 WorkManager 任务状态 */
    val notificationWorkStatus: Flow<WorkInfo.State?>

    /** 观察数据维护 WorkManager 任务状态 */
    val maintenanceWorkStatus: Flow<WorkInfo.State?>
}
