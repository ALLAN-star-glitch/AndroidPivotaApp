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
        // Wait for initialization with retry
        var attempts = 0
        while (!KtorClientFactory.isInitialized() && attempts < 30) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
            attempts++
        }

        return KtorClientFactory.build()
    }
}