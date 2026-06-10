package com.example.anchor.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.anchor.R

/**
 * 添加可选任务弹窗。
 */
@Composable
fun OptionalTaskAddDialog(
    inputText: String,
    errorMessage: String?,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.optional_task_dialog_title))
        },
        text = {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.optional_task_input_hint)) },
                singleLine = true,
                isError = errorMessage != null,
                supportingText = errorMessage?.let { msg ->
                    { Text(text = msg, color = MaterialTheme.colorScheme.error) }
                },
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.optional_task_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.optional_task_dialog_cancel))
            }
        },
    )
}
