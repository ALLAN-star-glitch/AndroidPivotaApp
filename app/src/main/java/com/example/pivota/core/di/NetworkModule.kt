/**
 * Dependency Injection module for network operations.
 * * Provides a singleton [HttpClient] configured via [KtorClientFactory].
 * This client serves as the primary engine for:
 * - **API Communication**: Handling registration, OTP verification, and login.
 * - **Backend Integration**: Supporting both Individual and Organization
 * workflows with standardized engine configurations (logging, serialization, etc.).
 */

package com.example.pivota.core.di

import com.example.pivota.core.network.KtorClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return KtorClientFactory.build()
    }
}