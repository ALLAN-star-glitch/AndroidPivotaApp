package com.example.pivota.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pivota.core.database.dao.*
import com.example.pivota.core.database.entity.*

@Database(
    entities = [
        // User related
        UserEntity::class,
        OrgMemberEntity::class,

        // Category related
        DiscoveryCategoryEntity::class,
        CategoryEntity::class,
        CategoriesCacheMetadataEntity::class,

        // Service offering related
        ServiceOfferingEntity::class,
        ServiceOfferingsCacheMetadataEntity::class,

        // Booking related
        BookingEntity::class,
        UpcomingBookingEntity::class,
        BookingStatsEntity::class,
        BookingStatusEntity::class,
        BookingCacheMetadataEntity::class,
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false
)
abstract class PivotaDatabase : RoomDatabase() {

    // Existing DAOs
    abstract fun userDao(): UserDao
    abstract fun orgMemberDao(): OrgMemberDao
    abstract fun categoryDao(): CategoryDao
    abstract fun serviceOfferingDao(): ServiceOfferingDao

    // New Booking DAO
    abstract fun bookingDao(): BookingDao
}