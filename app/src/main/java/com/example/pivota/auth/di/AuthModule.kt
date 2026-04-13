package com.example.pivota.auth.di

import com.example.pivota.auth.data.remote.api.AuthApiService
import com.example.pivota.auth.data.repository.AuthRepositoryImpl
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.auth.domain.useCase.*
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

    companion object {
        @Provides
        @Singleton
        fun provideAuthApiService(client: HttpClient): AuthApiService {
            return AuthApiService(client)
        }

        @Provides
        @Singleton
        fun provideAuthUseCases(repository: AuthRepository): AuthUseCases {
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