package com.example.anchor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.Task

/**
 * 勾选类任务卡片：左滑露出一小块「完成」，可滑回，点击后执行。
 */
@Composable
fun SwipeableCheckboxTaskCard(
    task: Task,
    colors: TaskListSectionColors,
    tagText: String,
    onComplete: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (task.completed) {
        CheckboxTaskCardContent(
            task = task,
            colors = colors,
            tagText = tagText,
            onComplete = onComplete,
            modifier = modifier,
        )
        return
    }

    RevealActionTaskCard(
        colors = colors,
        actionLabel = stringResource(R.string.swipe_action_complete),
        actionEnabled = true,
        revealWidth = TaskSwipeRevealWidth,
        onActionClick = { onComplete(task.id) },
        modifier = modifier,
    ) {
        CheckboxTaskCardContent(
            task = task,
            colors = colors,
            tagText = tagText,
            onComplete = onComplete,
        )
    }
}

@Composable
private fun CheckboxTaskCardContent(
    task: Task,
    colors: TaskListSectionColors,
    tagText: String,
    onComplete: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (task.completed) {
                    Modifier
                        .background(colors.cardContainer, TaskCardShape)
                        .border(1.dp, colors.cardBorder, TaskCardShape)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { checked ->
                if (checked && !task.completed) {
                    onComplete(task.id)
                }
            },
            enabled = !task.completed,
        )
        Text(
            text = task.content,
            style = MaterialTheme.typography.bodyLarge.copy(
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
            ),
            color = if (task.completed) {
                colors.dateLabel
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .background(colors.tagContainer, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = tagText,
                style = MaterialTheme.typography.labelSmall,
                color = colors.tagText,
            )
        }
        Text(
            text = stringResource(R.string.task_date_today),
            style = MaterialTheme.typography.labelMedium,
            color = colors.dateLabel,
            modifier = Modifier.padding(end = 8.dp),
        )
    }
}
