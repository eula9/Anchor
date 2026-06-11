package com.example.anchor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.Task

/**
 * 明天任务卡片：左滑露出一小块「移至今天」，可滑回，点击后执行。
 */
@Composable
fun SwipeableTomorrowTaskCard(
    task: Task,
    colors: TaskListSectionColors,
    canMoveToToday: Boolean,
    onMoveToToday: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    RevealActionTaskCard(
        colors = colors,
        actionLabel = stringResource(R.string.tomorrow_task_move_to_today),
        actionEnabled = canMoveToToday,
        revealWidth = TaskSwipeRevealWidthWide,
        onActionClick = { onMoveToToday(task.id) },
        modifier = modifier,
    ) {
        TomorrowTaskCardContent(task = task, colors = colors)
    }
}

@Composable
private fun TomorrowTaskCardContent(
    task: Task,
    colors: TaskListSectionColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, colors.accent.copy(alpha = 0.7f), CircleShape),
        )
        Text(
            text = task.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.task_date_tomorrow),
            style = MaterialTheme.typography.labelMedium,
            color = colors.dateLabel,
        )
    }
}
