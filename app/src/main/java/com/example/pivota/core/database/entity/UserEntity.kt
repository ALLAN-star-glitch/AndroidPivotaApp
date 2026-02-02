package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pivota.core.database.DatabaseConstants

/**
 * Represents the authenticated user's local profile and session context.
 * Flattened structure to support both Individual and Organization account types in Room.
 */
@Entity(tableName = DatabaseConstants.Tables.USERS)
data class UserEntity(
    @PrimaryKey
    val uuid: String,
    val accountUuid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val personalPhone: String?,
    val accountType: String, // "INDIVIDUAL" or "ORGANIZATION"
    val isVerified: Boolean,
    val selectedPlan: String?,

    // Flattened Organization Fields
    val orgUuid: String? = null,
    val orgName: String? = null,
    val orgType: String? = null,
    val officialEmail: String? = null,
    val officialPhone: String? = null,
    val physicalAddress: String? = null,
    val adminFirstName: String? = null,
    val adminLastName: String? = null,

    val isOnboardingComplete: Boolean = false,
    val createdAt: Long? = null
)