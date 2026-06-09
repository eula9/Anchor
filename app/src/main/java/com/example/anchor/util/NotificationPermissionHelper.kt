package com.example.anchor.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * 通知权限辅助工具。
 *
 * Android 13（API 33）及以上需要运行时申请 POST_NOTIFICATIONS 权限。
 */
object NotificationPermissionHelper {

    /**
     * 当前系统是否需要运行时申请通知权限。
     */
    fun requiresRuntimePermission(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    /**
     * 检查通知权限是否已授予。
     *
     * API 33 以下默认返回 true（无需运行时权限）。
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        if (!requiresRuntimePermission()) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
