package com.example.anchor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.NotificationTime

/**
 * 通知权限与每日提醒设置卡片（极简风格）。
 *
 * @param isPermissionGranted 通知权限是否已授予
 * @param isNotificationEnabled 每日提醒是否已开启
 * @param notificationTime 用户设定的推送时刻
 * @param onEnableClick 点击开启提醒
 * @param onDisableClick 点击关闭提醒
 * @param onTimeChange 用户修改推送时刻
 * @param modifier 外部修饰符
 */
@Composable
fun NotificationPermissionCard(
    isPermissionGranted: Boolean,
    isNotificationEnabled: Boolean,
    notificationTime: NotificationTime,
    onEnableClick: () -> Unit,
    onDisableClick: () -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.notification_card_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // 提醒时间设置行（始终可见，开启前也可预设）
        NotificationTimeRow(
            notificationTime = notificationTime,
            onClick = { showTimePicker = true },
        )

        if (isNotificationEnabled) {
            Text(
                text = stringResource(
                    R.string.notification_card_enabled_with_time,
                    notificationTime.formatted(),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            TextButton(onClick = onDisableClick) {
                Text(text = stringResource(R.string.notification_disable_button))
            }
        } else {
            Text(
                text = stringResource(R.string.notification_card_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )

            if (!isPermissionGranted) {
                Text(
                    text = stringResource(R.string.notification_permission_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(onClick = onEnableClick) {
                    Text(text = stringResource(R.string.notification_enable_button))
                }
            }
        }
    }

    if (showTimePicker) {
        NotificationTimePickerDialog(
            hour = notificationTime.hour,
            minute = notificationTime.minute,
            onDismiss = { showTimePicker = false },
            onConfirm = onTimeChange,
        )
    }
}

/**
 * 提醒时间展示行，点击弹出时间选择器。
 */
@Composable
private fun NotificationTimeRow(
    notificationTime: NotificationTime,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.notification_time_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        Text(
            text = notificationTime.formatted(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
