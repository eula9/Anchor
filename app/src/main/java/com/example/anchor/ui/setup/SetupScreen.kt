package com.example.anchor.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anchor.R
import com.example.anchor.di.AppContainer
import com.example.anchor.util.Constants

/**
 * 首次身份锚点设置页。
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    appContainer: AppContainer,
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = viewModel(
        factory = SetupViewModelFactory(
            identityRepository = appContainer.identityRepository,
            taskRepository = appContainer.taskRepository,
        ),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val setupCompleted by viewModel.setupCompleted.collectAsStateWithLifecycle()

    if (setupCompleted) {
        onSetupComplete()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.setup_title)) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.setup_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value = uiState.identityInput,
                onValueChange = viewModel::onIdentityChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.setup_identity_label)) },
                placeholder = { Text(stringResource(R.string.setup_identity_hint)) },
                supportingText = {
                    Text(
                        stringResource(
                            R.string.setup_identity_counter,
                            uiState.identityInput.length,
                            Constants.MAX_ANCHOR_IDENTITY_LENGTH,
                        ),
                    )
                },
                minLines = 2,
            )

            Text(
                text = stringResource(R.string.setup_duration_label),
                style = MaterialTheme.typography.titleSmall,
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Constants.ANCHOR_PRESET_DURATIONS.forEach { days ->
                    FilterChip(
                        selected = !uiState.isCustomDuration && uiState.selectedPresetDays == days,
                        onClick = { viewModel.onPresetDurationSelected(days) },
                        label = { Text(stringResource(R.string.setup_duration_days, days)) },
                    )
                }
                FilterChip(
                    selected = uiState.isCustomDuration,
                    onClick = viewModel::onCustomDurationModeSelected,
                    label = { Text(stringResource(R.string.setup_duration_custom)) },
                )
            }

            if (uiState.isCustomDuration) {
                OutlinedTextField(
                    value = uiState.customDurationInput,
                    onValueChange = viewModel::onCustomDurationInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.setup_custom_duration_label)) },
                    placeholder = {
                        Text(
                            stringResource(
                                R.string.setup_custom_duration_hint,
                                Constants.MIN_ANCHOR_DURATION_DAYS,
                                Constants.MAX_ANCHOR_DURATION_DAYS,
                            ),
                        )
                    },
                    singleLine = true,
                )
            }

            Text(
                text = stringResource(
                    R.string.setup_fixed_tasks_label,
                    Constants.MIN_FIXED_TASKS,
                    Constants.MAX_FIXED_TASKS,
                ),
                style = MaterialTheme.typography.titleSmall,
            )

            uiState.fixedTaskInputs.forEachIndexed { index, value ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { viewModel.onFixedTaskChange(index, it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.setup_fixed_task_item, index + 1)) },
                    placeholder = { Text(stringResource(R.string.setup_fixed_task_hint)) },
                    singleLine = true,
                )
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (uiState.canAddMoreFixedTasks) {
                    TextButton(onClick = viewModel::addFixedTaskField) {
                        Text(stringResource(R.string.setup_add_fixed_task))
                    }
                }
                if (uiState.canRemoveFixedTask) {
                    TextButton(onClick = viewModel::removeFixedTaskField) {
                        Text(stringResource(R.string.setup_remove_fixed_task))
                    }
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::submitSetup,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
            ) {
                Text(
                    text = if (uiState.isSaving) {
                        stringResource(R.string.setup_saving)
                    } else {
                        stringResource(R.string.setup_confirm)
                    },
                )
            }
        }
    }
}
