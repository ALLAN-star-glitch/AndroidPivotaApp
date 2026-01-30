package com.example.pivota.core.database

import UserDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pivota.auth.data.local.UserEntity

@Database(
    entities = [UserEntity::class],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false
)
abstract class PivotaDatabase : RoomDatabase() {
    // Feature DAOs injected/provided via Hilt

    abstract fun userDao(): UserDao
}