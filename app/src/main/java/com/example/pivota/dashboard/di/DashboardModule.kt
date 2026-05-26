// dashboard/di/DashboardModule.kt
package com.example.pivota.dashboard.di

import com.example.pivota.dashboard.data.repository.CategoriesRepositoryImpl
import com.example.pivota.dashboard.data.repository.ProfileRepositoryImpl
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import com.example.pivota.dashboard.domain.repository.ProfileRepository
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import com.example.pivota.dashboard.data.repository.ServiceOfferingsRepositoryImpl
import com.example.pivota.dashboard.domain.useCase.GetCommonServicesUseCase
import com.example.pivota.dashboard.domain.useCase.GetOfferingsByCategoryUseCase
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

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(impl: CategoriesRepositoryImpl): CategoriesRepository

    @Binds
    @Singleton
    abstract fun bindServiceOfferingRepository(impl: ServiceOfferingsRepositoryImpl): ServiceOfferingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideGetProfileUseCase(repository: ProfileRepository): GetProfileUseCase {
            return GetProfileUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideGetCommonServicesUseCase(repository: CategoriesRepository): GetCommonServicesUseCase {
            return GetCommonServicesUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideGetOfferingsByCategoryUseCase(repository: ServiceOfferingsRepository): GetOfferingsByCategoryUseCase {
            return GetOfferingsByCategoryUseCase(repository)
        }
    }
}