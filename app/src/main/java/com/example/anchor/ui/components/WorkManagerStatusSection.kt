package com.example.anchor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import com.example.anchor.R
import com.example.anchor.util.WorkStatusFormatter

/**
 * WorkManager 后台任务状态展示区块。
 */
@Composable
fun WorkManagerStatusSection(
    notificationWorkStatus: WorkInfo.State?,
    maintenanceWorkStatus: WorkInfo.State?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_workmanager_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        WorkStatusRow(
            label = stringResource(R.string.settings_workmanager_notification),
            status = WorkStatusFormatter.format(notificationWorkStatus),
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        )

        WorkStatusRow(
            label = stringResource(R.string.settings_workmanager_maintenance),
            status = WorkStatusFormatter.format(maintenanceWorkStatus),
        )
    }
}

@Composable
private fun WorkStatusRow(label: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
