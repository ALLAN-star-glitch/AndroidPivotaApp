package com.example.pivota.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val dataStore: DataStore<Preferences>  // Make sure this is injected
) {
    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    // REMOVE THIS LINE IF IT EXISTS:
    // private val dataStore = context.dataStore  // ← DELETE THIS!

    val isDarkThemeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }

    suspend fun toggleTheme() {
        val current = getCurrentTheme()
        setDarkTheme(!current)
    }

    suspend fun getCurrentTheme(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }.first()
    }

    fun getCurrentThemeSync(): Boolean {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[DARK_THEME_KEY] ?: false
            }.first()
        }
    }
}