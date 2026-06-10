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
import com.example.anchor.domain.model.IdentityAnchor
import com.example.anchor.util.Constants

/**
 * 每日身份锚点通知管理器。
 */
class IdentityNotificationManager(
    private val context: Context,
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /** 创建通知渠道 */
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
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)

        val expiryChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_EXPIRY_ID,
            context.getString(R.string.notification_expiry_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_expiry_channel_description)
        }
        notificationManager.createNotificationChannel(expiryChannel)
    }

    /** 发送每日身份锚点通知 */
    fun showDailyIdentityNotification(anchor: IdentityAnchor) {
        createNotificationChannel()

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
        val body = context.getString(R.string.notification_identity_body, anchor.statement)

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body),
            )
            .setColor(ContextCompat.getColor(context, R.color.notification_accent))
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_DAILY_IDENTITY, notification)
    }

    /** 锚点周期到期提醒 */
    fun showAnchorExpiryNotification(anchor: IdentityAnchor) {
        createNotificationChannel()

        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = context.getString(R.string.notification_expiry_title)
        val body = context.getString(R.string.notification_expiry_body, anchor.durationDays)

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_EXPIRY_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_ANCHOR_EXPIRY, notification)
    }
}
