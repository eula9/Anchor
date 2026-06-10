package com.example.anchor.domain.model

/**
 * 应用数据备份模型（JSON 导出/导入）。
 */
data class BackupData(
    val version: Int,
    val exportTime: String,
    val isSetupComplete: Boolean = false,
    val identity: String? = null,
    val startDate: String? = null,
    val durationDays: Int? = null,
    val fixedTaskTemplates: List<String> = emptyList(),
    val notificationEnabled: Boolean,
    val notificationHour: Int,
    val notificationMinute: Int,
    val themeMode: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastPerfectDate: String? = null,
    val tasks: List<BackupTask>,
)

/** 备份中的任务条目 */
data class BackupTask(
    val content: String,
    val completed: Boolean,
    val date: String,
    val type: Int = 0,
    val orderIndex: Int = 0,
)
