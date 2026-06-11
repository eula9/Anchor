package com.example.anchor.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anchor.R
import com.example.anchor.di.AppContainer
import com.example.anchor.ui.components.AnchorCard
import com.example.anchor.ui.components.FixedTasksSection
import com.example.anchor.ui.components.OptionalTasksSection
import com.example.anchor.ui.components.StreakCard
import com.example.anchor.ui.components.TaskAddBottomSheet
import com.example.anchor.ui.components.TomorrowTasksSection

/**
 * 首页 Composable。
 */
@Composable
fun HomeScreen(
    appContainer: AppContainer,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            identityRepository = appContainer.identityRepository,
            taskRepository = appContainer.taskRepository,
            streakRepository = appContainer.streakRepository,
        ),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskAddBottomSheet(
        visible = uiState.showOptionalDialog,
        title = stringResource(R.string.optional_task_dialog_title),
        inputHint = stringResource(R.string.optional_task_input_hint),
        description = null,
        inputText = uiState.optionalDialogInput,
        errorMessage = if (uiState.showOptionalDialog) uiState.taskError else null,
        confirmLabel = stringResource(R.string.optional_task_dialog_confirm),
        cancelLabel = stringResource(R.string.optional_task_dialog_cancel),
        onInputChange = viewModel::onOptionalDialogInputChange,
        onConfirm = viewModel::confirmAddOptionalTask,
        onDismiss = viewModel::dismissOptionalDialog,
    )

    TaskAddBottomSheet(
        visible = uiState.showTomorrowDialog,
        title = stringResource(R.string.tomorrow_task_dialog_title),
        inputHint = stringResource(R.string.tomorrow_task_input_hint),
        description = stringResource(R.string.tomorrow_task_dialog_description),
        inputText = uiState.tomorrowDialogInput,
        errorMessage = if (uiState.showTomorrowDialog) uiState.taskError else null,
        confirmLabel = stringResource(R.string.tomorrow_task_dialog_confirm),
        cancelLabel = stringResource(R.string.tomorrow_task_dialog_cancel),
        onInputChange = viewModel::onTomorrowDialogInputChange,
        onConfirm = viewModel::confirmAddTomorrowTask,
        onDismiss = viewModel::dismissTomorrowDialog,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        AnchorCard(anchor = uiState.activeAnchor)

        Spacer(modifier = Modifier.height(16.dp))

        StreakCard(streakInfo = uiState.streakInfo)

        Spacer(modifier = Modifier.height(28.dp))

        FixedTasksSection(
            tasks = uiState.fixedTasks,
            onCompleteTask = viewModel::completeTask,
        )

        Spacer(modifier = Modifier.height(28.dp))

        OptionalTasksSection(
            tasks = uiState.optionalTasks,
            canAddMore = uiState.canAddMoreOptionalTasks,
            onAddClick = viewModel::showAddOptionalDialog,
            onCompleteTask = viewModel::completeTask,
        )

        Spacer(modifier = Modifier.height(28.dp))

        TomorrowTasksSection(
            tasks = uiState.tomorrowTasks,
            canAddMore = uiState.canAddMoreTomorrowTasks,
            canMoveToToday = uiState.canMoveTomorrowToToday,
            onAddClick = viewModel::showAddTomorrowDialog,
            onMoveToToday = viewModel::moveTomorrowTaskToToday,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
