/**
 * Manager for lightweight persistent app state using DataStore.
 * * This class handles:
 * - **Session Persistence**: Managing JWT tokens for backend authentication.
 * - **Navigation Logic**: Tracking [isOnboardingComplete] to determine the
 * initial destination.
 * - **User Preferences**: Local settings like [SELECTED_LANGUAGE].
 * * Use [clearSession] for standard logouts and [clearAll] for a full factory
 * reset of the app state.
 */

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

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[AUTH_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    /**
     * Clears only the Authentication tokens.
     * Use this for a "Soft Logout" where the user returns to Guest Mode
     * and does NOT see the Welcome Screen again.
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