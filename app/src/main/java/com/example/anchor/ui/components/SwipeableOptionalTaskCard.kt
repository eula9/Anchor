package com.example.anchor.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.anchor.R
import com.example.anchor.domain.model.Task

/**
 * 可选任务卡片。
 */
@Composable
fun SwipeableOptionalTaskCard(
    task: Task,
    colors: TaskListSectionColors,
    onComplete: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    SwipeableCheckboxTaskCard(
        task = task,
        colors = colors,
        tagText = stringResource(R.string.optional_task_tag),
        onComplete = onComplete,
        modifier = modifier,
    )
}
