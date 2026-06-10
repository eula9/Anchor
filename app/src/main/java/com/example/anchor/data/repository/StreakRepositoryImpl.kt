package com.example.anchor.data.repository

import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.domain.model.StreakInfo
import com.example.anchor.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 连续行动天数仓库实现。
 *
 * 基于「全部固定任务完成」统计连续天数。
 */
class StreakRepositoryImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore,
) : StreakRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val streakInfo: Flow<StreakInfo> =
        userPreferencesDataStore.streakPreferencesFlow.map { prefs ->
            toStreakInfo(prefs)
        }

    override suspend fun refreshDayBoundary() {
        val prefs = userPreferencesDataStore.getStreakPreferences()
        if (isStreakBroken(prefs.lastPerfectDate)) {
            userPreferencesDataStore.saveStreakPreferences(
                currentStreak = 0,
                longestStreak = prefs.longestStreak,
                lastPerfectDate = prefs.lastPerfectDate,
            )
        }
    }

    override suspend fun onAllFixedTasksCompleted() {
        refreshDayBoundary()

        val today = todayString()
        val prefs = userPreferencesDataStore.getStreakPreferences()
        if (prefs.lastPerfectDate == today) return

        val yesterday = LocalDate.now().minusDays(1).format(dateFormatter)
        val newStreak = when (prefs.lastPerfectDate) {
            yesterday -> prefs.currentStreak + 1
            else -> 1
        }
        val newLongest = maxOf(prefs.longestStreak, newStreak)

        userPreferencesDataStore.saveStreakPreferences(
            currentStreak = newStreak,
            longestStreak = newLongest,
            lastPerfectDate = today,
        )
    }

    private fun toStreakInfo(prefs: UserPreferencesDataStore.StreakPreferences): StreakInfo {
        val today = LocalDate.now()
        val lastPerfect = prefs.lastPerfectDate?.let(LocalDate::parse)

        if (lastPerfect == null) {
            return StreakInfo(
                currentStreak = 0,
                longestStreak = prefs.longestStreak,
                actionTakenToday = false,
            )
        }

        return when {
            lastPerfect == today -> StreakInfo(
                currentStreak = prefs.currentStreak,
                longestStreak = prefs.longestStreak,
                actionTakenToday = true,
            )
            lastPerfect == today.minusDays(1) -> StreakInfo(
                currentStreak = prefs.currentStreak,
                longestStreak = prefs.longestStreak,
                actionTakenToday = false,
            )
            else -> StreakInfo(
                currentStreak = 0,
                longestStreak = prefs.longestStreak,
                actionTakenToday = false,
            )
        }
    }

    private fun isStreakBroken(lastPerfectDate: String?): Boolean {
        val lastPerfect = lastPerfectDate?.let(LocalDate::parse) ?: return false
        return lastPerfect < LocalDate.now().minusDays(1)
    }

    private fun todayString(): String = LocalDate.now().format(dateFormatter)
}
