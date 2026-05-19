package com.example.pivota.auth.di

import com.example.pivota.auth.data.remote.api.AuthApiService
import com.example.pivota.auth.data.remote.api.AuthAuthenticatedApiService
import com.example.pivota.auth.data.repository.AuthRepositoryImpl
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.auth.domain.useCase.*
import com.example.pivota.core.auth.TokenManager
import com.example.pivota.core.auth.TokenProvider
import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.di.UnauthHttpClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenProvider(impl: TokenManager): TokenProvider

    companion object {
        @Provides
        @Singleton
        fun provideAuthApiService(
            @UnauthHttpClient client: HttpClient
        ): AuthApiService {
            return AuthApiService(client)
        }

        @Provides
        @Singleton
        fun provideAuthAuthenticatedApiService(
            @AuthHttpClient client: HttpClient
        ): AuthAuthenticatedApiService {
            return AuthAuthenticatedApiService(client)
        }

        @Provides
        @Singleton
        fun provideAuthUseCases(
            repository: AuthRepository
            // ✅ Remove authAuthenticatedApiService from here
        ): AuthUseCases {
            return AuthUseCases(
                requestOtp = RequestOtpUseCase(repository),
                registerUser = RegisterUserUseCase(repository),
                loginUser = LoginUserUseCase(repository),
                verifyMfaLogin = VerifyMfaLoginUseCase(repository),
                refreshToken = RefreshTokenUseCase(repository),
                requestPasswordReset = RequestPasswordResetUseCase(repository),
                resetPassword = ResetPasswordUseCase(repository),
                logout = LogoutUseCase(repository),
                setWelcomeSeen = SetWelcomeSeenUseCase(repository),
                hasSeenWelcome = HasSeenWelcomeUseCase(repository),
                googleSignIn = GoogleSignInUseCase(repository)
            )
        }
    }
}