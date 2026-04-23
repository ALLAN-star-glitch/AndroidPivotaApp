// dashboard/di/DashboardModule.kt
package com.example.pivota.dashboard.di

import com.example.pivota.dashboard.data.repository.ProfileRepositoryImpl
import com.example.pivota.dashboard.domain.repository.ProfileRepository
import com.example.pivota.dashboard.domain.useCase.GetProfileUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DashboardModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    companion object {
        @Provides
        @Singleton
        fun provideGetProfileUseCase(repository: ProfileRepository): GetProfileUseCase {
            return GetProfileUseCase(repository)
        }
    }
}