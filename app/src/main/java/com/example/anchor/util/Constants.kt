package com.example.anchor.util

/**
 * 应用全局常量定义。
 */
object Constants {

    const val PREFERENCES_NAME = "anchor_preferences"

    const val KEY_IDENTITY_DATE = "identity_date"
    const val KEY_IDENTITY_INDEX = "identity_index"
    const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    const val KEY_NOTIFICATION_HOUR = "notification_hour"
    const val KEY_NOTIFICATION_MINUTE = "notification_minute"
    const val KEY_THEME_MODE = "theme_mode"

    const val MAX_DAILY_TASKS = 3

    const val NOTIFICATION_CHANNEL_ID = "identity_daily_channel"
    const val NOTIFICATION_ID_DAILY_IDENTITY = 1001

    const val WORKER_DAILY_IDENTITY_NOTIFICATION = "daily_identity_notification"
    const val WORKER_DAILY_MAINTENANCE = "daily_maintenance"

    const val DEFAULT_NOTIFICATION_HOUR = 8
    const val DEFAULT_NOTIFICATION_MINUTE = 0

    /** 备份文件格式版本 */
    const val BACKUP_VERSION = 1

    /** 备份文件 MIME 类型 */
    const val BACKUP_MIME_TYPE = "application/json"
}
