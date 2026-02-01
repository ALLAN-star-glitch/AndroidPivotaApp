package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uuid: String,
    val accountUuid: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val personalPhone: String?,
    val roleName: String,
    val isVerified: Boolean,
    val selectedPlan: String?,

    // Essential Organization "Flattened" fields
    // We keep only what is required for immediate UI display (Identity & Connection)
    val isOrganization: Boolean,
    val orgUuid: String?,
    val orgName: String?,
    val verificationStatus: String?,

    val isOnboardingComplete: Boolean = false
)