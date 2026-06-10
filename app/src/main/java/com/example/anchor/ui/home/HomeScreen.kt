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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anchor.di.AppContainer
import com.example.anchor.ui.components.AnchorCard
import com.example.anchor.ui.components.FixedTasksSection
import com.example.anchor.ui.components.OptionalTaskAddDialog
import com.example.anchor.ui.components.OptionalTasksSection
import com.example.anchor.ui.components.StreakCard

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

    if (uiState.showOptionalDialog) {
        OptionalTaskAddDialog(
            inputText = uiState.optionalDialogInput,
            errorMessage = uiState.taskError,
            onInputChange = viewModel::onOptionalDialogInputChange,
            onConfirm = viewModel::confirmAddOptionalTask,
            onDismiss = viewModel::dismissOptionalDialog,
        )
    }

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

        Spacer(modifier = Modifier.height(16.dp))
    }
}
