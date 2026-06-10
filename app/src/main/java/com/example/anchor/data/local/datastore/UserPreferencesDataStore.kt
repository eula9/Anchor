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
import org.json.JSONArray

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.PREFERENCES_NAME,
)

/**
 * 用户偏好与身份锚点 DataStore 管理器。
 */
class UserPreferencesDataStore(context: Context) {

    private val dataStore = context.dataStore

    private val setupCompleteKey = booleanPreferencesKey(Constants.KEY_SETUP_COMPLETE)
    private val anchorIdentityKey = stringPreferencesKey(Constants.KEY_ANCHOR_IDENTITY)
    private val anchorStartDateKey = stringPreferencesKey(Constants.KEY_ANCHOR_START_DATE)
    private val anchorDurationDaysKey = intPreferencesKey(Constants.KEY_ANCHOR_DURATION_DAYS)
    private val fixedTaskTemplatesKey = stringPreferencesKey(Constants.KEY_FIXED_TASK_TEMPLATES)
    private val notificationEnabledKey = booleanPreferencesKey(Constants.KEY_NOTIFICATION_ENABLED)
    private val notificationHourKey = intPreferencesKey(Constants.KEY_NOTIFICATION_HOUR)
    private val notificationMinuteKey = intPreferencesKey(Constants.KEY_NOTIFICATION_MINUTE)
    private val themeModeKey = stringPreferencesKey(Constants.KEY_THEME_MODE)
    private val streakCountKey = intPreferencesKey(Constants.KEY_STREAK_COUNT)
    private val longestStreakKey = intPreferencesKey(Constants.KEY_LONGEST_STREAK)
    private val lastPerfectDateKey = stringPreferencesKey(Constants.KEY_LAST_PERFECT_DATE)
    private val motivationDateKey = stringPreferencesKey(Constants.KEY_MOTIVATION_DATE)
    private val motivationIndexKey = intPreferencesKey(Constants.KEY_MOTIVATION_INDEX)
    private val oemBackgroundConfirmedKey = booleanPreferencesKey(Constants.KEY_OEM_BACKGROUND_CONFIRMED)
    private val lastNotificationDateKey = stringPreferencesKey(Constants.KEY_LAST_NOTIFICATION_DATE)

    /** 身份锚点偏好数据 */
    data class AnchorPreferences(
        val isSetupComplete: Boolean,
        val identity: String?,
        val startDate: String?,
        val durationDays: Int?,
        val fixedTaskTemplates: List<String>,
    )

    data class StreakPreferences(
        val currentStreak: Int,
        val longestStreak: Int,
        val lastPerfectDate: String?,
    )

    data class MotivationPreferences(
        val date: String?,
        val index: Int?,
    )

    data class AppPreferences(
        val isSetupComplete: Boolean,
        val identity: String?,
        val startDate: String?,
        val durationDays: Int?,
        val fixedTaskTemplates: List<String>,
        val notificationEnabled: Boolean,
        val notificationHour: Int,
        val notificationMinute: Int,
        val themeMode: ThemeMode,
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val lastPerfectDate: String? = null,
    )

    val anchorPreferencesFlow: Flow<AnchorPreferences> = dataStore.data.map { preferences ->
        AnchorPreferences(
            isSetupComplete = preferences[setupCompleteKey] ?: false,
            identity = preferences[anchorIdentityKey],
            startDate = preferences[anchorStartDateKey],
            durationDays = preferences[anchorDurationDaysKey],
            fixedTaskTemplates = decodeTemplates(preferences[fixedTaskTemplatesKey]),
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
            lastPerfectDate = preferences[lastPerfectDateKey],
        )
    }

    val motivationPreferencesFlow: Flow<MotivationPreferences> = dataStore.data.map { preferences ->
        MotivationPreferences(
            date = preferences[motivationDateKey],
            index = preferences[motivationIndexKey],
        )
    }

    /** 保存身份锚点与固定任务模板 */
    suspend fun saveAnchorSetup(
        identity: String,
        startDate: String,
        durationDays: Int,
        fixedTaskTemplates: List<String>,
    ) {
        dataStore.edit { preferences ->
            preferences[setupCompleteKey] = true
            preferences[anchorIdentityKey] = identity
            preferences[anchorStartDateKey] = startDate
            preferences[anchorDurationDaysKey] = durationDays
            preferences[fixedTaskTemplatesKey] = encodeTemplates(fixedTaskTemplates)
        }
    }

    /** 延长锚点周期 */
    suspend fun extendAnchorDuration(newDurationDays: Int, newStartDate: String) {
        dataStore.edit { preferences ->
            preferences[anchorStartDateKey] = newStartDate
            preferences[anchorDurationDaysKey] = newDurationDays
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

    suspend fun getAnchorPreferences(): AnchorPreferences {
        return anchorPreferencesFlow.first()
    }

    suspend fun getStreakPreferences(): StreakPreferences {
        return streakPreferencesFlow.first()
    }

    suspend fun getMotivationPreferences(): MotivationPreferences {
        return motivationPreferencesFlow.first()
    }

    suspend fun saveMotivation(date: String, index: Int) {
        dataStore.edit { preferences ->
            preferences[motivationDateKey] = date
            preferences[motivationIndexKey] = index
        }
    }

    suspend fun isOemBackgroundConfirmed(): Boolean {
        return dataStore.data.first()[oemBackgroundConfirmedKey] ?: false
    }

    suspend fun setOemBackgroundConfirmed(confirmed: Boolean) {
        dataStore.edit { preferences ->
            preferences[oemBackgroundConfirmedKey] = confirmed
        }
    }

    suspend fun getLastNotificationDate(): String? {
        return dataStore.data.first()[lastNotificationDateKey]
    }

    suspend fun saveLastNotificationDate(date: String) {
        dataStore.edit { preferences ->
            preferences[lastNotificationDateKey] = date
        }
    }

    suspend fun saveStreakPreferences(
        currentStreak: Int,
        longestStreak: Int,
        lastPerfectDate: String?,
    ) {
        dataStore.edit { preferences ->
            preferences[streakCountKey] = currentStreak
            preferences[longestStreakKey] = longestStreak
            if (lastPerfectDate != null) {
                preferences[lastPerfectDateKey] = lastPerfectDate
            } else {
                preferences.remove(lastPerfectDateKey)
            }
        }
    }

    suspend fun getAppPreferences(): AppPreferences {
        val preferences = dataStore.data.first()
        return AppPreferences(
            isSetupComplete = preferences[setupCompleteKey] ?: false,
            identity = preferences[anchorIdentityKey],
            startDate = preferences[anchorStartDateKey],
            durationDays = preferences[anchorDurationDaysKey],
            fixedTaskTemplates = decodeTemplates(preferences[fixedTaskTemplatesKey]),
            notificationEnabled = preferences[notificationEnabledKey] ?: false,
            notificationHour = preferences[notificationHourKey] ?: Constants.DEFAULT_NOTIFICATION_HOUR,
            notificationMinute = preferences[notificationMinuteKey] ?: Constants.DEFAULT_NOTIFICATION_MINUTE,
            themeMode = ThemeMode.fromString(preferences[themeModeKey]),
            currentStreak = preferences[streakCountKey] ?: 0,
            longestStreak = preferences[longestStreakKey] ?: 0,
            lastPerfectDate = preferences[lastPerfectDateKey],
        )
    }

    suspend fun restoreAppPreferences(preferences: AppPreferences) {
        dataStore.edit { prefs ->
            prefs[setupCompleteKey] = preferences.isSetupComplete
            preferences.identity?.let { prefs[anchorIdentityKey] = it }
            preferences.startDate?.let { prefs[anchorStartDateKey] = it }
            preferences.durationDays?.let { prefs[anchorDurationDaysKey] = it }
            prefs[fixedTaskTemplatesKey] = encodeTemplates(preferences.fixedTaskTemplates)
            prefs[notificationEnabledKey] = preferences.notificationEnabled
            prefs[notificationHourKey] = preferences.notificationHour
            prefs[notificationMinuteKey] = preferences.notificationMinute
            prefs[themeModeKey] = preferences.themeMode.name
            prefs[streakCountKey] = preferences.currentStreak
            prefs[longestStreakKey] = preferences.longestStreak
            if (preferences.lastPerfectDate != null) {
                prefs[lastPerfectDateKey] = preferences.lastPerfectDate
            } else {
                prefs.remove(lastPerfectDateKey)
            }
        }
    }

    private fun encodeTemplates(templates: List<String>): String {
        return JSONArray(templates).toString()
    }

    private fun decodeTemplates(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        val array = JSONArray(raw)
        return buildList {
            for (index in 0 until array.length()) {
                add(array.getString(index))
            }
        }
    }
}
