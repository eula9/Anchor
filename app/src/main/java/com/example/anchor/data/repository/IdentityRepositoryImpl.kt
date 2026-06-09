package com.example.anchor.data.repository

import com.example.anchor.data.local.datastore.UserPreferencesDataStore
import com.example.anchor.data.source.DefaultIdentities
import com.example.anchor.domain.model.Identity
import com.example.anchor.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * 身份仓库实现类。
 */
class IdentityRepositoryImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore,
) : IdentityRepository {

    /** ISO 本地日期格式化器（yyyy-MM-dd） */
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val todayIdentity: Flow<Identity> =
        userPreferencesDataStore.identityPreferencesFlow.mapLatest { preferences ->
            resolveTodayIdentity(preferences)
        }

    override suspend fun getTodayIdentity(): Identity {
        val preferences = userPreferencesDataStore.identityPreferencesFlow.first()
        return resolveTodayIdentity(preferences)
    }

    /**
     * 解析并返回今日身份。
     */
    private suspend fun resolveTodayIdentity(
        preferences: UserPreferencesDataStore.IdentityPreferences,
    ): Identity {
        val today = LocalDate.now().format(dateFormatter)

        if (preferences.date == today && preferences.index != null) {
            return Identity(
                statement = DefaultIdentities.getStatement(preferences.index),
                date = today,
            )
        }

        val newIndex = Random.nextInt(DefaultIdentities.statements.size)
        userPreferencesDataStore.saveTodayIdentity(
            date = today,
            index = newIndex,
        )

        return Identity(
            statement = DefaultIdentities.getStatement(newIndex),
            date = today,
        )
    }
}
