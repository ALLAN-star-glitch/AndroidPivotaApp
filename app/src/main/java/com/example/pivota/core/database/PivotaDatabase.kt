package com.example.pivota.core.database

import com.example.pivota.core.database.dao.CategoryDao
import com.example.pivota.core.database.dao.OrgMemberDao
import com.example.pivota.core.database.dao.ServiceOfferingDao
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.database.entity.DiscoveryCategoryEntity
import com.example.pivota.core.database.entity.OrgMemberEntity
import com.example.pivota.core.database.entity.ServiceOfferingEntity
import com.example.pivota.core.database.entity.ServiceOfferingsCacheMetadataEntity
import com.example.pivota.core.database.entity.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        OrgMemberEntity::class,
        DiscoveryCategoryEntity::class,
        ServiceOfferingEntity::class,
        ServiceOfferingsCacheMetadataEntity::class
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false
)
abstract class PivotaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orgMemberDao(): OrgMemberDao
    abstract fun categoryDao(): CategoryDao
    abstract fun serviceOfferingDao(): ServiceOfferingDao
}