package com.example.pivota.core.database.entity
import androidx.room.Entity
import androidx.room.Index
import com.example.pivota.core.database.DatabaseConstants

@Entity(
    tableName = DatabaseConstants.Tables.ORGANIZATION_MEMBERS,
    primaryKeys = ["userUuid", "organizationUuid"],
    indices = [Index(value = ["organizationUuid"])] // Speeds up member lookups
)
data class OrgMemberEntity(
    val userUuid: String,
    val organizationUuid: String,
    val roleName: String
)