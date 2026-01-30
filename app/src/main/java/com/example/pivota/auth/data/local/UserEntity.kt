package com.example.pivota.auth.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pivota.core.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.Tables.USERS)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val isOrganization: Boolean, // Section 1: Organizations vs Individuals
    val role: String,            // "ADMIN" or "MEMBER"
    val createdAt: Long = System.currentTimeMillis()
)