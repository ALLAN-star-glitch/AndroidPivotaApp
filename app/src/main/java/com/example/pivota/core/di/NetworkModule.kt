// NetworkModule.kt
package com.example.pivota.core.di

import com.example.pivota.core.auth.TokenProvider
import com.example.pivota.core.health.GatewayHealthChecker
import com.example.pivota.core.network.KtorClientFactory
import com.example.pivota.core.network.api.HealthApiService
import com.example.pivota.core.network.repository.HealthRepository
import com.example.pivota.core.network.useCase.HealthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Client WITHOUT auth interceptor (for login, signup, refresh, health)
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
    fun provideAuthHttpClient(tokenProvider: TokenProvider): HttpClient {
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
        KtorClientFactory.setTokenProvider(tokenProvider)
        return KtorClientFactory.build()
    }

    // ✅ Gateway Health Checker (DNS-based)
    @Provides
    @Singleton
    fun provideGatewayHealthChecker(): GatewayHealthChecker {
        return GatewayHealthChecker()
    }

    // ✅ Health API Service (HTTP health check)
    @Provides
    @Singleton
    fun provideHealthApiService(
        @UnauthHttpClient client: HttpClient
    ): HealthApiService {
        return HealthApiService(client)
    }

    // ✅ Health Repository
    @Provides
    @Singleton
    fun provideHealthRepository(
        healthApiService: HealthApiService
    ): HealthRepository {
        return HealthRepository(healthApiService)
    }

    // ✅ Health UseCase
    @Provides
    @Singleton
    fun provideHealthUseCase(
        healthRepository: HealthRepository
    ): HealthUseCase {
        return HealthUseCase(healthRepository)
    }
}