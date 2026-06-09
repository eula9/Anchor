package com.example.anchor.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.anchor.MainActivity
import com.example.anchor.R
import com.example.anchor.domain.model.Identity
import com.example.anchor.util.Constants

/**
 * 每日身份通知管理器。
 *
 * 负责创建高优先级通知渠道，并在锁屏界面展示简洁醒目的今日身份。
 */
class IdentityNotificationManager(
    private val context: Context,
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * 创建通知渠道（Android 8.0+ 必须）。
     *
     * 使用 IMPORTANCE_HIGH 确保锁屏可见且伴有提示音。
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notification_channel_description)
            enableLights(true)
            lightColor = Color.WHITE
            enableVibration(true)
            // 锁屏界面完整展示通知内容
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    /**
     * 发送每日身份通知到锁屏。
     *
     * @param identity 今日身份数据
     */
    fun showDailyIdentityNotification(identity: Identity) {
        createNotificationChannel()

        // 点击通知打开应用
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = context.getString(R.string.notification_identity_title)
        // 格式：身份 · 我是一个行动的人
        val body = context.getString(
            R.string.notification_identity_body,
            identity.statement,
        )

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            // BigText 样式：锁屏展开后更醒目
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body),
            )
            .setColor(ContextCompat.getColor(context, R.color.notification_accent))
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            // 锁屏公开可见
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_DAILY_IDENTITY, notification)
    }
}
