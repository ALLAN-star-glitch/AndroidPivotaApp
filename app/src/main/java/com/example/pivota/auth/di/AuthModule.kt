package com.example.pivota.auth.di

import com.example.pivota.auth.data.remote.AuthApiService
import com.example.pivota.auth.data.repository.AuthRepositoryImpl
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.domain.useCase.HasSeenWelcomeUseCase
import com.example.pivota.auth.domain.useCase.LoginUserUserUseCase
import com.example.pivota.auth.domain.useCase.LoginWithMfaUseCase
import com.example.pivota.auth.domain.useCase.RegisterUserUseCase
import com.example.pivota.auth.domain.useCase.RequestOtpUseCase
import com.example.pivota.auth.domain.useCase.SetWelcomeSeenUseCase
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
                loginWithMfa = LoginWithMfaUseCase(repository),
                setWelcomeSeen = SetWelcomeSeenUseCase(repository),
                hasSeenWelcome = HasSeenWelcomeUseCase(repository),
                loginUser = LoginUserUserUseCase(repository)
            )
        }
    }


}