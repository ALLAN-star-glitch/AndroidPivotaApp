// NetworkModule.kt - Updated to use TokenProvider
package com.example.pivota.core.di

import com.example.pivota.core.auth.TokenProvider
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

    // Client WITHOUT auth interceptor (for login, signup, refresh)
    @Provides
    @Singleton
    @UnauthHttpClient
    fun provideUnauthHttpClient(): HttpClient {
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
        return KtorClientFactory.buildUnauth()
    }

    // Client WITH auth interceptor (for authenticated requests like profile)
    @Provides
    @Singleton
    @AuthHttpClient
    fun provideAuthHttpClient(tokenProvider: TokenProvider): HttpClient {  // Use TokenProvider, not TokenManager
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
        KtorClientFactory.setTokenProvider(tokenProvider)  // Changed from setTokenManager to setTokenProvider
        return KtorClientFactory.build()
    }
}