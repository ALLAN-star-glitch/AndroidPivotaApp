package com.example.pivota.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PivotaPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        // Added for session identification
        private val USER_EMAIL = stringPreferencesKey("user_email")
    }

    // --- Navigation State ---

    val isOnboardingComplete: Flow<Boolean> = dataStore.data.map {
        it[ONBOARDING_COMPLETE] ?: false
    }

    suspend fun isOnboardingComplete(): Boolean {
        return dataStore.data.map { it[ONBOARDING_COMPLETE] ?: false }.first()
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETE] = complete }
    }

    // --- User Info State ---

    val userEmail: Flow<String?> = dataStore.data.map { it[USER_EMAIL] }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { it[USER_EMAIL] = email }
    }

    suspend fun getUserEmail(): String? {
        return dataStore.data.map { it[USER_EMAIL] }.first()
    }

    // --- Session State ---

    val authToken: Flow<String?> = dataStore.data.map { it[AUTH_TOKEN] }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { it[REFRESH_TOKEN] = token }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[AUTH_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    /**
     * Clears Authentication tokens and user specific info.
     */
    suspend fun clearSession() {
        dataStore.edit {
            it.remove(AUTH_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(USER_EMAIL)
        }
    }

    /**
     * Master Reset: Wipes everything including onboarding status.
     */
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}