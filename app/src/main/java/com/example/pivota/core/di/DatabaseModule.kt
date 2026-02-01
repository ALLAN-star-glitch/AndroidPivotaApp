package com.example.pivota.core.di

import android.content.Context
import androidx.room.Room
import com.example.pivota.core.database.DatabaseConstants
import com.example.pivota.core.database.PivotaDatabase
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
            .fallbackToDestructiveMigration(false) // Recommended during active development
            .build()
    }

    @Provides
    fun provideUserDao(database: PivotaDatabase) = database.userDao()

    @Provides
    fun provideOrgMemberDao(database: PivotaDatabase) = database.orgMemberDao()
}