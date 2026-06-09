package com.example.anchor.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.anchor.ui.components.DailyTasksSection
import com.example.anchor.ui.components.IdentityCard

/**
 * 首页 Composable。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appContainer: AppContainer,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            identityRepository = appContainer.identityRepository,
            taskRepository = appContainer.taskRepository,
        ),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.nav_home)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            IdentityCard(identity = uiState.todayIdentity)

            Spacer(modifier = Modifier.height(32.dp))

            DailyTasksSection(
                tasks = uiState.todayTasks,
                inputText = uiState.taskInput,
                canAddMore = uiState.canAddMoreTasks,
                errorMessage = uiState.taskError,
                onInputChange = viewModel::onTaskInputChange,
                onAddTask = viewModel::addTask,
                onToggleComplete = viewModel::toggleTaskComplete,
            )
        }
    }
}
