package com.example.pivota.core.di

import android.content.Context
import androidx.room.Room
import com.example.pivota.core.database.DatabaseConstants
import com.example.pivota.core.database.PivotaDatabase
import com.example.pivota.core.database.dao.CategoryDao
import com.example.pivota.core.database.dao.ServiceOfferingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PivotaDatabase {
        return Room.databaseBuilder(
            context,
            PivotaDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideUserDao(database: PivotaDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: PivotaDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideOrgMemberDao(database: PivotaDatabase) = database.orgMemberDao()

    @Provides
    @Singleton
    fun provideServiceOfferingDao(database: PivotaDatabase): ServiceOfferingDao {
        return database.serviceOfferingDao()
    }
}