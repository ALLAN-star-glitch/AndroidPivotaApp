// dashboard/di/DashboardModule.kt
package com.example.pivota.dashboard.di

import com.example.pivota.dashboard.data.repository.BookingRepositoryImpl
import com.example.pivota.dashboard.data.repository.CategoriesRepositoryImpl
import com.example.pivota.dashboard.data.repository.JobPostRepositoryImpl
import com.example.pivota.dashboard.data.repository.PricingUnitsRepositoryImpl
import com.example.pivota.dashboard.data.repository.ProfileRepositoryImpl
import com.example.pivota.dashboard.data.repository.ServiceOfferingsRepositoryImpl
import com.example.pivota.dashboard.domain.repository.BookingRepository
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import com.example.pivota.dashboard.domain.repository.JobPostRepository
import com.example.pivota.dashboard.domain.repository.PricingUnitsRepository
import com.example.pivota.dashboard.domain.repository.ProfileRepository
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import com.example.pivota.dashboard.domain.useCase.CreateBookingUseCase
import com.example.pivota.dashboard.domain.useCase.GetCommonServicesUseCase
import com.example.pivota.dashboard.domain.useCase.GetComplimentaryCategoriesUseCase
import com.example.pivota.dashboard.domain.useCase.GetFullComplimentaryCategoriesUseCase
import com.example.pivota.dashboard.domain.useCase.GetOfferingsByCategoryUseCase
import com.example.pivota.dashboard.domain.useCase.GetPricingUnitsByCategoryUseCase
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

    @Binds
    @Singleton
    abstract fun bindPricingUnitsRepository(impl: PricingUnitsRepositoryImpl): PricingUnitsRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository  // Fixed: BookingRepositoryImpl -> BookingRepository

    // In the @Binds section
    @Binds
    @Singleton
    abstract fun bindJobPostRepository(impl: JobPostRepositoryImpl): JobPostRepository

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

        @Provides
        @Singleton
        fun provideGetPricingUnitsByCategoryUseCase(repository: PricingUnitsRepository): GetPricingUnitsByCategoryUseCase {
            return GetPricingUnitsByCategoryUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideGetComplimentaryCategoriesUseCase(repository: CategoriesRepository): GetComplimentaryCategoriesUseCase {
            return GetComplimentaryCategoriesUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideGetFullComplimentaryCategoriesUseCase(
            repository: CategoriesRepository
        ): GetFullComplimentaryCategoriesUseCase {
            return GetFullComplimentaryCategoriesUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideCreateBookingUseCase(
            repository: BookingRepository
        ): CreateBookingUseCase {
            return CreateBookingUseCase(repository)
        }
    }
}