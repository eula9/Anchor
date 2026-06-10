package com.example.anchor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.Task
import com.example.anchor.util.Constants

/**
 * 可选任务列表区域。
 */
@Composable
fun OptionalTasksSection(
    tasks: List<Task>,
    canAddMore: Boolean,
    onAddClick: () -> Unit,
    onCompleteTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val completedCount = tasks.count { it.completed }
    val remainingQuota = Constants.MAX_OPTIONAL_TASKS - tasks.size

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.optional_tasks_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(
                    R.string.optional_tasks_quota,
                    completedCount,
                    tasks.size,
                    remainingQuota,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (tasks.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )
            tasks.forEach { task ->
                TaskItemRow(
                    task = task,
                    onComplete = onCompleteTask,
                )
            }
        }

        if (canAddMore) {
            OutlinedButton(
                onClick = onAddClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(stringResource(R.string.optional_task_add_button))
            }
        } else {
            Text(
                text = stringResource(R.string.optional_tasks_full),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}
