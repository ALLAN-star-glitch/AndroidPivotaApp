package com.example.pivota.dashboard.di

import com.example.pivota.dashboard.data.mapper.ServiceOfferingCacheMapper
import com.example.pivota.dashboard.data.mapper.ServiceOfferingMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideServiceOfferingMapper(): ServiceOfferingMapper {
        return ServiceOfferingMapper()
    }

    @Provides
    @Singleton
    fun provideServiceOfferingCacheMapper(moshi: Moshi): ServiceOfferingCacheMapper {
        return ServiceOfferingCacheMapper(moshi)
    }
}