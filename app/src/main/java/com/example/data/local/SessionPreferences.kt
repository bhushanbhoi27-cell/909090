package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.jodivaDataStore by preferencesDataStore(name = "jodiva_session_prefs")

data class UserPreferencesState(
    val currentUserId: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val preferredLanguage: String = "English",
    val autoTranslate: Boolean = false,
    val notifySocial: Boolean = true,
    val notifyQuiz: Boolean = true,
    val notifySecurity: Boolean = true
)

class SessionPreferences(private val context: Context) {
    private object Keys {
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val AUTO_TRANSLATE = booleanPreferencesKey("auto_translate")
        val NOTIFY_SOCIAL = booleanPreferencesKey("notify_social")
        val NOTIFY_QUIZ = booleanPreferencesKey("notify_quiz")
        val NOTIFY_SECURITY = booleanPreferencesKey("notify_security")
    }

    val preferencesFlow: Flow<UserPreferencesState> = context.jodivaDataStore.data.map { prefs ->
        val mode = when (prefs[Keys.THEME_MODE]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        UserPreferencesState(
            currentUserId = prefs[Keys.CURRENT_USER_ID],
            themeMode = mode,
            preferredLanguage = prefs[Keys.PREFERRED_LANGUAGE] ?: "English",
            autoTranslate = prefs[Keys.AUTO_TRANSLATE] ?: false,
            notifySocial = prefs[Keys.NOTIFY_SOCIAL] ?: true,
            notifyQuiz = prefs[Keys.NOTIFY_QUIZ] ?: true,
            notifySecurity = prefs[Keys.NOTIFY_SECURITY] ?: true
        )
    }

    suspend fun setCurrentUserId(userId: String?) {
        context.jodivaDataStore.edit { prefs ->
            if (userId == null) {
                prefs.remove(Keys.CURRENT_USER_ID)
            } else {
                prefs[Keys.CURRENT_USER_ID] = userId
            }
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.jodivaDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setPreferredLanguage(language: String) {
        context.jodivaDataStore.edit { it[Keys.PREFERRED_LANGUAGE] = language }
    }

    suspend fun setAutoTranslate(enabled: Boolean) {
        context.jodivaDataStore.edit { it[Keys.AUTO_TRANSLATE] = enabled }
    }

    suspend fun updateNotificationPrefs(social: Boolean, quiz: Boolean, security: Boolean) {
        context.jodivaDataStore.edit {
            it[Keys.NOTIFY_SOCIAL] = social
            it[Keys.NOTIFY_QUIZ] = quiz
            it[Keys.NOTIFY_SECURITY] = security
        }
    }
}
