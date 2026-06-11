package com.example.anchor.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.Task

/**
 * 固定任务列表区域（图 1 卡片 + 左滑完成）。
 */
@Composable
fun FixedTasksSection(
    tasks: List<Task>,
    onCompleteTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = fixedTaskSectionColors()

    Column(modifier = modifier.fillMaxWidth()) {
        TaskSectionHeader(
            title = stringResource(R.string.fixed_tasks_title),
            trailingText = stringResource(R.string.fixed_tasks_header_trailing, tasks.size),
            colors = colors,
            useDotAccent = false,
        )

        if (tasks.isEmpty()) {
            Text(
                text = stringResource(R.string.fixed_tasks_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
            return
        }

        Column(modifier = Modifier.padding(top = 12.dp)) {
            tasks.forEach { task ->
                SwipeableCheckboxTaskCard(
                    task = task,
                    colors = colors,
                    tagText = stringResource(R.string.fixed_task_tag),
                    onComplete = onCompleteTask,
                )
            }
        }
    }
}
