package com.example.pivota.dashboard.di

import com.example.pivota.dashboard.data.remote.CategoriesApiService
import com.example.pivota.dashboard.data.remote.ProfileApiService
import com.example.pivota.dashboard.data.remote.ServiceOfferingsApiService
import com.example.pivota.dashboard.data.remote.PricingUnitsApiService
import com.example.pivota.core.di.AuthHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideProfileApiService(
        @AuthHttpClient client: HttpClient
    ): ProfileApiService {
        return ProfileApiService(client)
    }

    @Provides
    @Singleton
    fun provideCategoriesApiService(
        @AuthHttpClient client: HttpClient
    ): CategoriesApiService {
        return CategoriesApiService(client)
    }

    @Provides
    @Singleton
    fun provideServiceOfferingsApiService(
        @AuthHttpClient client: HttpClient
    ): ServiceOfferingsApiService {
        return ServiceOfferingsApiService(client)
    }

    @Provides
    @Singleton
    fun providePricingUnitsApiService(
        @AuthHttpClient client: HttpClient
    ): PricingUnitsApiService {
        return PricingUnitsApiService(client)
    }
}