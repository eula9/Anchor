package com.example.anchor.domain.model

import com.example.anchor.util.Constants

/**
 * 每日通知推送时刻。
 *
 * @property hour 小时（24 小时制，0–23）
 * @property minute 分钟（0–59）
 */
data class NotificationTime(
    val hour: Int = Constants.DEFAULT_NOTIFICATION_HOUR,
    val minute: Int = Constants.DEFAULT_NOTIFICATION_MINUTE,
) {
    /** 格式化为 HH:mm 显示文本 */
    fun formatted(): String = "%02d:%02d".format(hour, minute)
}
