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

    // --- Session State ---

    val authToken: Flow<String?> = dataStore.data.map { it[AUTH_TOKEN] }

    /**
     * Specifically added to resolve the repository's "saveAccessToken" call.
     */
    suspend fun saveAccessToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    /**
     * Specifically added to resolve the repository's "saveRefreshToken" call.
     */
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
     * Clears only the Authentication tokens.
     */
    suspend fun clearSession() {
        dataStore.edit {
            it.remove(AUTH_TOKEN)
            it.remove(REFRESH_TOKEN)
        }
    }

    /**
     * Master Reset: Wipes everything including onboarding status.
     */
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}