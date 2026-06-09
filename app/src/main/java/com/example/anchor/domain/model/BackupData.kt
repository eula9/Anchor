package com.example.anchor.domain.model

/**
 * 应用数据备份模型。
 *
 * 用于 JSON 导出/导入，包含身份、通知、主题与任务数据。
 */
data class BackupData(
    val version: Int,
    val exportTime: String,
    val identityDate: String?,
    val identityIndex: Int?,
    val notificationEnabled: Boolean,
    val notificationHour: Int,
    val notificationMinute: Int,
    val themeMode: String,
    val tasks: List<BackupTask>,
)

/**
 * 备份中的任务条目（不含自增 id，导入时重新生成）。
 */
data class BackupTask(
    val content: String,
    val completed: Boolean,
    val date: String,
)
