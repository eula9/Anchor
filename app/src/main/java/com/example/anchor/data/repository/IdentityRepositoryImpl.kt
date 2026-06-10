package com.example.anchor.data.repository

import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.domain.model.IdentityAnchor
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 身份锚点仓库实现（DataStore）。
 */
class IdentityRepositoryImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore,
) : IdentityRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val isSetupComplete: Flow<Boolean> =
        userPreferencesDataStore.anchorPreferencesFlow.map { it.isSetupComplete }

    override val activeAnchor: Flow<IdentityAnchor?> =
        userPreferencesDataStore.anchorPreferencesFlow.map { prefs ->
            buildAnchor(prefs)
        }

    override val fixedTaskTemplates: Flow<List<String>> =
        userPreferencesDataStore.anchorPreferencesFlow.map { it.fixedTaskTemplates }

    override suspend fun setupAnchor(
        identity: String,
        durationDays: Int,
        fixedTasks: List<String>,
    ): Result<Unit> {
        val trimmedIdentity = identity.trim()
        val trimmedTasks = fixedTasks.map { it.trim() }.filter { it.isNotEmpty() }

        if (trimmedIdentity.isEmpty()) {
            return Result.failure(InvalidAnchorException("请输入身份宣言"))
        }
        if (trimmedIdentity.length > Constants.MAX_ANCHOR_IDENTITY_LENGTH) {
            return Result.failure(
                InvalidAnchorException("身份宣言不能超过 ${Constants.MAX_ANCHOR_IDENTITY_LENGTH} 个字"),
            )
        }
        if (durationDays !in Constants.MIN_ANCHOR_DURATION_DAYS..Constants.MAX_ANCHOR_DURATION_DAYS) {
            return Result.failure(
                InvalidAnchorException(
                    "周期需在 ${Constants.MIN_ANCHOR_DURATION_DAYS}~${Constants.MAX_ANCHOR_DURATION_DAYS} 天之间",
                ),
            )
        }
        if (trimmedTasks.size !in Constants.MIN_FIXED_TASKS..Constants.MAX_FIXED_TASKS) {
            return Result.failure(
                InvalidAnchorException(
                    "请设置 ${Constants.MIN_FIXED_TASKS}~${Constants.MAX_FIXED_TASKS} 条固定任务",
                ),
            )
        }

        val today = LocalDate.now().format(dateFormatter)
        userPreferencesDataStore.saveAnchorSetup(
            identity = trimmedIdentity,
            startDate = today,
            durationDays = durationDays,
            fixedTaskTemplates = trimmedTasks,
        )
        return Result.success(Unit)
    }

    override suspend fun extendAnchor(durationDays: Int): Result<Unit> {
        if (durationDays !in Constants.MIN_ANCHOR_DURATION_DAYS..Constants.MAX_ANCHOR_DURATION_DAYS) {
            return Result.failure(
                InvalidAnchorException(
                    "周期需在 ${Constants.MIN_ANCHOR_DURATION_DAYS}~${Constants.MAX_ANCHOR_DURATION_DAYS} 天之间",
                ),
            )
        }
        val today = LocalDate.now().format(dateFormatter)
        userPreferencesDataStore.extendAnchorDuration(
            newDurationDays = durationDays,
            newStartDate = today,
        )
        return Result.success(Unit)
    }

    override suspend fun getActiveAnchor(): IdentityAnchor? {
        return buildAnchor(userPreferencesDataStore.getAnchorPreferences())
    }

    private fun buildAnchor(prefs: UserPreferencesDataStore.AnchorPreferences): IdentityAnchor? {
        val identity = prefs.identity ?: return null
        val startDate = prefs.startDate ?: return null
        val durationDays = prefs.durationDays ?: return null

        val start = LocalDate.parse(startDate, dateFormatter)
        val today = LocalDate.now()
        val daysPassed = ChronoUnit.DAYS.between(start, today).toInt()
        val currentDay = daysPassed + 1
        val daysRemaining = (durationDays - currentDay).coerceAtLeast(0)
        val isExpired = currentDay > durationDays

        return IdentityAnchor(
            statement = identity,
            startDate = startDate,
            durationDays = durationDays,
            currentDay = currentDay.coerceAtMost(durationDays),
            daysRemaining = daysRemaining,
            isExpired = isExpired,
        )
    }
}

/** 身份锚点参数无效异常 */
class InvalidAnchorException(message: String) : Exception(message)
