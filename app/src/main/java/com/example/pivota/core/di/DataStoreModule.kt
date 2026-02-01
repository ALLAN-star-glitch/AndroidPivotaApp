/**
 * Dependency Injection module for the Preferences DataStore.
 * * Provides a singleton [DataStore] instance to manage lightweight persistent
 * key-value pairs. Typically used for:
 * - **Session State**: Auth tokens or last-active timestamps.
 * - **UI Preferences**: Theme settings or "Remember Me" flags.
 * - **Onboarding**: Tracking if the initial setup flow has been completed.
 */

package com.example.pivota.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.pivota.core.preferences.PivotaPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("pivota_prefs") }
        )
    }
}