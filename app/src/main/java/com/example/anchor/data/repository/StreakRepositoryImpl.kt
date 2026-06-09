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
 * 规则：每天至少完成一件事即计为「行动」；
 * 若昨日有行动、今日也有行动，则连续天数 +1；
 * 若中断超过 1 天，则从 1 重新计数。
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
        if (isStreakBroken(prefs.lastActiveDate)) {
            userPreferencesDataStore.saveStreakPreferences(
                currentStreak = 0,
                longestStreak = prefs.longestStreak,
                lastActiveDate = prefs.lastActiveDate,
            )
        }
    }

    override suspend fun markActionToday() {
        refreshDayBoundary()

        val today = todayString()
        val prefs = userPreferencesDataStore.getStreakPreferences()
        if (prefs.lastActiveDate == today) return

        val yesterday = LocalDate.now().minusDays(1).format(dateFormatter)
        val newStreak = when (prefs.lastActiveDate) {
            yesterday -> prefs.currentStreak + 1
            else -> 1
        }
        val newLongest = maxOf(prefs.longestStreak, newStreak)

        userPreferencesDataStore.saveStreakPreferences(
            currentStreak = newStreak,
            longestStreak = newLongest,
            lastActiveDate = today,
        )
    }

    private fun toStreakInfo(prefs: UserPreferencesDataStore.StreakPreferences): StreakInfo {
        val today = LocalDate.now()
        val lastActive = prefs.lastActiveDate?.let(LocalDate::parse)

        if (lastActive == null) {
            return StreakInfo(
                currentStreak = 0,
                longestStreak = prefs.longestStreak,
                actionTakenToday = false,
            )
        }

        return when {
            lastActive == today -> StreakInfo(
                currentStreak = prefs.currentStreak,
                longestStreak = prefs.longestStreak,
                actionTakenToday = true,
            )
            lastActive == today.minusDays(1) -> StreakInfo(
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

    private fun isStreakBroken(lastActiveDate: String?): Boolean {
        val lastActive = lastActiveDate?.let(LocalDate::parse) ?: return false
        return lastActive < LocalDate.now().minusDays(1)
    }

    private fun todayString(): String = LocalDate.now().format(dateFormatter)
}
