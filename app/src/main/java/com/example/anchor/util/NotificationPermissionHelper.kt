package com.example.anchor.util

import android.content.Context

/**
 * 通知权限辅助工具（兼容旧调用，委托给 [ReminderPermissionHelper]）。
 */
object NotificationPermissionHelper {

    fun requiresRuntimePermission(): Boolean =
        ReminderPermissionHelper.requiresNotificationPermission()

    fun isNotificationPermissionGranted(context: Context): Boolean =
        ReminderPermissionHelper.isNotificationPermissionGranted(context)
}
