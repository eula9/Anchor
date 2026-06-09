package com.example.anchor.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anchor.R
import com.example.anchor.di.AppContainer
import com.example.anchor.ui.components.BackupSection
import com.example.anchor.ui.components.NotificationPermissionCard
import com.example.anchor.ui.components.ThemeModeSelector
import com.example.anchor.ui.components.WorkManagerStatusSection
import com.example.anchor.util.Constants
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 设置页 Composable。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appContainer: AppContainer,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            settingsRepository = appContainer.settingsRepository,
            notificationRepository = appContainer.notificationRepository,
            backupRepository = appContainer.backupRepository,
        ),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onNotificationPermissionResult(granted)
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(Constants.BACKUP_MIME_TYPE),
    ) { uri ->
        uri?.let(viewModel::exportBackup)
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let(viewModel::importBackup)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissionState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back),
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
            // 深色模式
            ThemeModeSelector(
                selectedMode = uiState.themeMode,
                onModeSelected = viewModel::setThemeMode,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 每日锁屏通知
            NotificationPermissionCard(
                isPermissionGranted = uiState.isNotificationPermissionGranted,
                isNotificationEnabled = uiState.isNotificationEnabled,
                notificationTime = uiState.notificationTime,
                onTimeChange = viewModel::setNotificationTime,
                onEnableClick = {
                    if (viewModel.requiresNotificationPermission()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        viewModel.requestEnableNotification()
                    }
                },
                onDisableClick = viewModel::disableNotification,
            )

            if (uiState.notificationError != null) {
                Text(
                    text = uiState.notificationError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // WorkManager 任务状态
            WorkManagerStatusSection(
                notificationWorkStatus = uiState.notificationWorkStatus,
                maintenanceWorkStatus = uiState.maintenanceWorkStatus,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 数据备份
            BackupSection(
                backupMessage = uiState.backupMessage,
                backupError = uiState.backupError,
                onExportClick = {
                    viewModel.clearBackupMessage()
                    val fileName = "anchor_backup_${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.json"
                    exportLauncher.launch(fileName)
                },
                onImportClick = {
                    viewModel.clearBackupMessage()
                    importLauncher.launch(arrayOf(Constants.BACKUP_MIME_TYPE, "application/*"))
                },
            )
        }
    }
}
