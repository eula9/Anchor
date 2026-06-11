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
 * 明天想做的事列表区域（图 1 卡片 + 左滑移至今天）。
 */
@Composable
fun TomorrowTasksSection(
    tasks: List<Task>,
    canAddMore: Boolean,
    canMoveToToday: Boolean,
    onAddClick: () -> Unit,
    onMoveToToday: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = tomorrowTaskSectionColors()

    Column(modifier = modifier.fillMaxWidth()) {
        TaskSectionHeader(
            title = stringResource(R.string.tomorrow_tasks_title),
            trailingText = stringResource(R.string.tomorrow_tasks_header_trailing),
            colors = colors,
            useDotAccent = true,
            badgeCount = tasks.size.takeIf { it > 0 },
        )

        if (tasks.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                tasks.forEach { task ->
                    SwipeableTomorrowTaskCard(
                        task = task,
                        colors = colors,
                        canMoveToToday = canMoveToToday,
                        onMoveToToday = onMoveToToday,
                    )
                }
            }
        }

        if (canAddMore) {
            TaskAddPillButton(
                text = stringResource(R.string.tomorrow_task_add_button),
                onClick = onAddClick,
            )
        } else {
            Text(
                text = stringResource(R.string.tomorrow_tasks_full),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
