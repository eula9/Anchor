package com.example.anchor.data.repository

import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.source.DefaultMotivations
import com.example.anchor.domain.repository.MotivationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * 每日激励语仓库实现。
 */
class MotivationRepositoryImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore,
) : MotivationRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val todayMotivation: Flow<String> =
        userPreferencesDataStore.motivationPreferencesFlow.map { prefs ->
            resolveQuote(prefs.date, prefs.index)
        }

    override suspend fun ensureTodayMotivation() {
        val today = LocalDate.now().format(dateFormatter)
        val current = userPreferencesDataStore.getMotivationPreferences()
        if (current.date == today) return

        val previousIndex = current.index
        val newIndex = pickRandomIndex(previousIndex)
        userPreferencesDataStore.saveMotivation(today, newIndex)
    }

    private fun resolveQuote(date: String?, index: Int?): String {
        val today = LocalDate.now().format(dateFormatter)
        if (date != today || index == null) {
            return ""
        }
        return DefaultMotivations.quotes.getOrElse(index) {
            DefaultMotivations.quotes.first()
        }
    }

    private fun pickRandomIndex(previousIndex: Int?): Int {
        val size = DefaultMotivations.count
        if (size <= 1) return 0

        var candidate = Random.nextInt(size)
        if (previousIndex != null && previousIndex in 0 until size) {
            repeat(8) {
                if (candidate != previousIndex) return candidate
                candidate = Random.nextInt(size)
            }
        }
        return candidate
    }
}
