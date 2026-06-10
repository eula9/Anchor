package com.example.anchor.util

/**
 * 应用全局常量定义。
 */
object Constants {

    const val PREFERENCES_NAME = "anchor_preferences"

    // --- 身份锚点（DataStore）---
    const val KEY_SETUP_COMPLETE = "setup_complete"
    const val KEY_ANCHOR_IDENTITY = "anchor_identity"
    const val KEY_ANCHOR_START_DATE = "anchor_start_date"
    const val KEY_ANCHOR_DURATION_DAYS = "anchor_duration_days"
    const val KEY_FIXED_TASK_TEMPLATES = "fixed_task_templates"

    // --- 旧版兼容键（保留避免迁移异常）---
    const val KEY_IDENTITY_DATE = "identity_date"
    const val KEY_IDENTITY_INDEX = "identity_index"

    // --- 每日激励语 ---
    const val KEY_MOTIVATION_DATE = "motivation_date"
    const val KEY_MOTIVATION_INDEX = "motivation_index"

    // --- 通知与主题 ---
    const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    const val KEY_NOTIFICATION_HOUR = "notification_hour"
    const val KEY_NOTIFICATION_MINUTE = "notification_minute"
    const val KEY_OEM_BACKGROUND_CONFIRMED = "oem_background_confirmed"
    const val KEY_LAST_NOTIFICATION_DATE = "last_notification_date"
    const val KEY_THEME_MODE = "theme_mode"

    // --- 连续天数（DataStore 缓存）---
    const val KEY_STREAK_COUNT = "streak_count"
    const val KEY_LONGEST_STREAK = "longest_streak"
    const val KEY_LAST_PERFECT_DATE = "last_perfect_date"

    // --- 任务限制 ---
    const val MIN_FIXED_TASKS = 3
    const val MAX_FIXED_TASKS = 6
    const val MAX_OPTIONAL_TASKS = 3

    /** 预设锚点周期（天） */
    val ANCHOR_PRESET_DURATIONS = listOf(7, 14, 30)

    /** 自定义周期允许范围 */
    const val MIN_ANCHOR_DURATION_DAYS = 1
    const val MAX_ANCHOR_DURATION_DAYS = 365

    /** 身份宣言最大字数 */
    const val MAX_ANCHOR_IDENTITY_LENGTH = 30

    const val NOTIFICATION_CHANNEL_ID = "identity_daily_channel"
    const val NOTIFICATION_CHANNEL_EXPIRY_ID = "anchor_expiry_channel"
    const val NOTIFICATION_ID_DAILY_IDENTITY = 1001
    const val NOTIFICATION_ID_ANCHOR_EXPIRY = 1002

    const val ACTION_DAILY_NOTIFICATION = "com.example.anchor.ACTION_DAILY_NOTIFICATION"
    const val ALARM_REQUEST_DAILY_NOTIFICATION = 2001
    const val ALARM_REQUEST_DAILY_NOTIFICATION_SHOW = 2002

    const val WORKER_DAILY_IDENTITY_NOTIFICATION = "daily_identity_notification"
    const val WORKER_DAILY_NOTIFICATION_BACKUP = "daily_notification_backup"

    /** 闹钟触发后备用 Worker 延迟（分钟），用于小米等清除闹钟的系统 */
    const val NOTIFICATION_BACKUP_DELAY_MINUTES = 5L
    const val WORKER_DAILY_MAINTENANCE = "daily_maintenance"
    const val WORKER_ANCHOR_EXPIRY_CHECK = "anchor_expiry_check"

    const val DEFAULT_NOTIFICATION_HOUR = 8
    const val DEFAULT_NOTIFICATION_MINUTE = 0

    const val BACKUP_VERSION = 3
    const val BACKUP_MIME_TYPE = "application/json"
}
