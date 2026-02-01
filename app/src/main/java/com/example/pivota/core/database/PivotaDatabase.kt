package com.example.pivota.core.database

import com.example.pivota.core.database.dao.UserDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pivota.core.database.dao.OrgMemberDao
import com.example.pivota.core.database.entity.OrgMemberEntity
import com.example.pivota.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, OrgMemberEntity::class],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false
)
abstract class PivotaDatabase : RoomDatabase() {
    // Feature DAOs injected/provided via Hilt

    abstract fun userDao(): UserDao
    abstract fun orgMemberDao(): OrgMemberDao


}