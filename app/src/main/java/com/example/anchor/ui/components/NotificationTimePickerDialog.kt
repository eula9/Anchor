package com.example.anchor.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.anchor.R

/**
 * 通知时刻选择对话框（Material3 TimePicker）。
 *
 * @param hour 当前小时
 * @param minute 当前分钟
 * @param onDismiss 取消回调
 * @param onConfirm 确认回调，返回用户选择的小时与分钟
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTimePickerDialog(
    hour: Int,
    minute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                    onDismiss()
                },
            ) {
                Text(text = stringResource(R.string.notification_time_picker_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.notification_time_picker_cancel))
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}
