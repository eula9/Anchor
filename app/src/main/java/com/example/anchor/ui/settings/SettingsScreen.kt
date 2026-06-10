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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
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
@Composable
fun SettingsScreen(
    appContainer: AppContainer,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            settingsRepository = appContainer.settingsRepository,
            notificationRepository = appContainer.notificationRepository,
            backupRepository = appContainer.backupRepository,
        ),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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

    LaunchedEffect(uiState.reminderSetupStep) {
        when (uiState.reminderSetupStep) {
            ReminderSetupStep.EXACT_ALARM -> {
                context.startActivity(
                    appContainer.notificationRepository.createExactAlarmSettingsIntent(),
                )
                viewModel.onExactAlarmSettingsOpened()
            }
            ReminderSetupStep.BATTERY_OPTIMIZATION -> {
                context.startActivity(
                    appContainer.notificationRepository.createBatteryOptimizationIntent(),
                )
                viewModel.onBatterySettingsOpened()
            }
            ReminderSetupStep.NOTIFICATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                viewModel.clearReminderSetupStep()
            }
            ReminderSetupStep.OEM_BACKGROUND -> {
                context.startActivity(
                    appContainer.notificationRepository.createOemBackgroundSettingsIntent(),
                )
                viewModel.onOemSettingsOpened()
            }
            null -> Unit
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refreshPermissionState()
        viewModel.ensureReminderScheduled()
        viewModel.continueReminderSetup()
        onPauseOrDispose { }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ThemeModeSelector(
            selectedMode = uiState.themeMode,
            onModeSelected = viewModel::setThemeMode,
        )

        Spacer(modifier = Modifier.height(28.dp))

        NotificationPermissionCard(
            isPermissionGranted = uiState.isNotificationPermissionGranted,
            canScheduleExactAlarms = uiState.canScheduleExactAlarms,
            isBatteryOptimizationIgnored = uiState.isBatteryOptimizationIgnored,
            oemVendor = uiState.oemVendor,
            isOemBackgroundConfirmed = uiState.isOemBackgroundConfirmed,
            awaitingOemConfirm = uiState.awaitingOemConfirm,
            isNotificationEnabled = uiState.isNotificationEnabled,
            notificationTime = uiState.notificationTime,
            onTimeChange = viewModel::setNotificationTime,
            onEnableClick = viewModel::beginEnableReminder,
            onDisableClick = viewModel::disableNotification,
            onOemBackgroundConfirm = viewModel::onOemBackgroundConfirmed,
            onOpenOemSettings = viewModel::openOemBackgroundSettingsAgain,
        )

        if (uiState.notificationError != null) {
            Text(
                text = uiState.notificationError!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        WorkManagerStatusSection(
            notificationWorkStatus = uiState.notificationWorkStatus,
            maintenanceWorkStatus = uiState.maintenanceWorkStatus,
        )

        Spacer(modifier = Modifier.height(28.dp))

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

        Spacer(modifier = Modifier.height(16.dp))
    }
}
