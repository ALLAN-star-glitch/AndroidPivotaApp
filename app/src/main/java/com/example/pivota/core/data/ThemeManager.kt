// ThemeManager.kt
package com.example.pivota.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class ThemeManager @Inject constructor(
    @param:ApplicationContext  private val context: Context
) {
    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    private val dataStore = context.dataStore

    // Flow to observe theme changes
    val isDarkThemeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false
    }

    // Save theme preference
    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }

    // Toggle theme
    suspend fun toggleTheme() {
        val current = getCurrentTheme()
        setDarkTheme(!current)
    }

    // Get current theme (suspend version)
    suspend fun getCurrentTheme(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }.first()  // Use first() instead of collect
    }

    // Synchronous version for initial load (use with caution - blocks thread)
    fun getCurrentThemeSync(): Boolean {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[DARK_THEME_KEY] ?: false
            }.first()
        }
    }
}