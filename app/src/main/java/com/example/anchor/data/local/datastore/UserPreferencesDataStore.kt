package com.example.anchor.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.anchor.domain.model.NotificationTime
import com.example.anchor.domain.model.ThemeMode
import com.example.anchor.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.PREFERENCES_NAME,
)

/**
 * 用户偏好设置 DataStore 管理器。
 */
class UserPreferencesDataStore(context: Context) {

    private val dataStore = context.dataStore

    private val identityDateKey = stringPreferencesKey(Constants.KEY_IDENTITY_DATE)
    private val identityIndexKey = intPreferencesKey(Constants.KEY_IDENTITY_INDEX)
    private val notificationEnabledKey = booleanPreferencesKey(Constants.KEY_NOTIFICATION_ENABLED)
    private val notificationHourKey = intPreferencesKey(Constants.KEY_NOTIFICATION_HOUR)
    private val notificationMinuteKey = intPreferencesKey(Constants.KEY_NOTIFICATION_MINUTE)
    private val themeModeKey = stringPreferencesKey(Constants.KEY_THEME_MODE)
    private val streakCountKey = intPreferencesKey(Constants.KEY_STREAK_COUNT)
    private val longestStreakKey = intPreferencesKey(Constants.KEY_LONGEST_STREAK)
    private val lastActiveDateKey = stringPreferencesKey(Constants.KEY_LAST_ACTIVE_DATE)

    data class IdentityPreferences(
        val date: String?,
        val index: Int?,
    )

    data class StreakPreferences(
        val currentStreak: Int,
        val longestStreak: Int,
        val lastActiveDate: String?,
    )

    data class AppPreferences(
        val identityDate: String?,
        val identityIndex: Int?,
        val notificationEnabled: Boolean,
        val notificationHour: Int,
        val notificationMinute: Int,
        val themeMode: ThemeMode,
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val lastActiveDate: String? = null,
    )

    val identityPreferencesFlow: Flow<IdentityPreferences> = dataStore.data.map { preferences ->
        IdentityPreferences(
            date = preferences[identityDateKey],
            index = preferences[identityIndexKey],
        )
    }

    val notificationEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[notificationEnabledKey] ?: false
    }

    val notificationTimeFlow: Flow<NotificationTime> = dataStore.data.map { preferences ->
        NotificationTime(
            hour = preferences[notificationHourKey] ?: Constants.DEFAULT_NOTIFICATION_HOUR,
            minute = preferences[notificationMinuteKey] ?: Constants.DEFAULT_NOTIFICATION_MINUTE,
        )
    }

    val themeModeFlow: Flow<ThemeMode> = dataStore.data.map { preferences ->
        ThemeMode.fromString(preferences[themeModeKey])
    }

    val streakPreferencesFlow: Flow<StreakPreferences> = dataStore.data.map { preferences ->
        StreakPreferences(
            currentStreak = preferences[streakCountKey] ?: 0,
            longestStreak = preferences[longestStreakKey] ?: 0,
            lastActiveDate = preferences[lastActiveDateKey],
        )
    }

    suspend fun saveTodayIdentity(date: String, index: Int) {
        dataStore.edit { preferences ->
            preferences[identityDateKey] = date
            preferences[identityIndexKey] = index
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[notificationEnabledKey] = enabled
        }
    }

    suspend fun isNotificationEnabled(): Boolean {
        return dataStore.data.first()[notificationEnabledKey] ?: false
    }

    suspend fun saveNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[notificationHourKey] = hour
            preferences[notificationMinuteKey] = minute
        }
    }

    suspend fun getNotificationTime(): NotificationTime {
        val preferences = dataStore.data.first()
        return NotificationTime(
            hour = preferences[notificationHourKey] ?: Constants.DEFAULT_NOTIFICATION_HOUR,
            minute = preferences[notificationMinuteKey] ?: Constants.DEFAULT_NOTIFICATION_MINUTE,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[themeModeKey] = mode.name
        }
    }

    suspend fun getThemeMode(): ThemeMode {
        return ThemeMode.fromString(dataStore.data.first()[themeModeKey])
    }

    suspend fun getStreakPreferences(): StreakPreferences {
        val preferences = dataStore.data.first()
        return StreakPreferences(
            currentStreak = preferences[streakCountKey] ?: 0,
            longestStreak = preferences[longestStreakKey] ?: 0,
            lastActiveDate = preferences[lastActiveDateKey],
        )
    }

    suspend fun saveStreakPreferences(
        currentStreak: Int,
        longestStreak: Int,
        lastActiveDate: String?,
    ) {
        dataStore.edit { preferences ->
            preferences[streakCountKey] = currentStreak
            preferences[longestStreakKey] = longestStreak
            if (lastActiveDate != null) {
                preferences[lastActiveDateKey] = lastActiveDate
            } else {
                preferences.remove(lastActiveDateKey)
            }
        }
    }

    /** 读取全部偏好（供备份导出） */
    suspend fun getAppPreferences(): AppPreferences {
        val preferences = dataStore.data.first()
        return AppPreferences(
            identityDate = preferences[identityDateKey],
            identityIndex = preferences[identityIndexKey],
            notificationEnabled = preferences[notificationEnabledKey] ?: false,
            notificationHour = preferences[notificationHourKey] ?: Constants.DEFAULT_NOTIFICATION_HOUR,
            notificationMinute = preferences[notificationMinuteKey] ?: Constants.DEFAULT_NOTIFICATION_MINUTE,
            themeMode = ThemeMode.fromString(preferences[themeModeKey]),
            currentStreak = preferences[streakCountKey] ?: 0,
            longestStreak = preferences[longestStreakKey] ?: 0,
            lastActiveDate = preferences[lastActiveDateKey],
        )
    }

    /** 从备份恢复偏好设置 */
    suspend fun restoreAppPreferences(preferences: AppPreferences) {
        dataStore.edit { prefs ->
            preferences.identityDate?.let { prefs[identityDateKey] = it }
            preferences.identityIndex?.let { prefs[identityIndexKey] = it }
            prefs[notificationEnabledKey] = preferences.notificationEnabled
            prefs[notificationHourKey] = preferences.notificationHour
            prefs[notificationMinuteKey] = preferences.notificationMinute
            prefs[themeModeKey] = preferences.themeMode.name
            prefs[streakCountKey] = preferences.currentStreak
            prefs[longestStreakKey] = preferences.longestStreak
            if (preferences.lastActiveDate != null) {
                prefs[lastActiveDateKey] = preferences.lastActiveDate
            } else {
                prefs.remove(lastActiveDateKey)
            }
        }
    }
}
