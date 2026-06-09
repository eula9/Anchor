package com.example.anchor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.Task

/**
 * 今日三件事模块（极简风格）。
 *
 * 展示任务列表、勾选完成状态，以及添加新任务的输入框。
 *
 * @param tasks 今日任务列表
 * @param inputText 新任务输入框文本
 * @param canAddMore 是否还能添加更多任务（未满 3 条）
 * @param errorMessage 错误提示信息，无错误时为 null
 * @param onInputChange 输入框文本变更回调
 * @param onAddTask 添加任务回调
 * @param onToggleComplete 切换任务完成状态回调
 * @param modifier 外部修饰符
 */
@Composable
fun DailyTasksSection(
    tasks: List<Task>,
    inputText: String,
    canAddMore: Boolean,
    errorMessage: String?,
    onInputChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onToggleComplete: (taskId: Long, completed: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 模块标题
        Text(
            text = stringResource(R.string.daily_tasks_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // 任务列表
        if (tasks.isEmpty()) {
            Text(
                text = stringResource(R.string.task_empty_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
        } else {
            tasks.forEach { task ->
                TaskItem(
                    task = task,
                    onToggleComplete = onToggleComplete,
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                )
            }
        }

        // 添加任务输入框（未满 3 条时显示）
        if (canAddMore) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.task_input_hint),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddTask() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                ),
            )
        } else {
            Text(
                text = stringResource(R.string.task_limit_reached),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
        }

        // 错误提示
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

/**
 * 单条任务列表项（极简风格）。
 *
 * @param task 任务数据
 * @param onToggleComplete 切换完成状态回调
 */
@Composable
private fun TaskItem(
    task: Task,
    onToggleComplete: (taskId: Long, completed: Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 完成状态勾选框
        Checkbox(
            checked = task.completed,
            onCheckedChange = { checked -> onToggleComplete(task.id, checked) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
            ),
        )

        // 任务内容（已完成时显示删除线）
        Text(
            text = task.content,
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.completed) {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            textDecoration = if (task.completed) TextDecoration.LineThrough else null,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}
