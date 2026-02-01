/**
 * Represents the authenticated user's local profile and session context.
 * * This entity uses a "Flattened" structure to store both core identity and
 * high-level Organization metadata. This allows the UI to instantly resolve:
 * - **Account Type**: Distinguished by [isOrganization].
 * - **Context**: The associated [orgName] and [orgUuid] for members or admins.
 * - **Permissions**: Roles (assigned via backend) and verification status.
 */

package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pivota.core.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.Tables.USERS )
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