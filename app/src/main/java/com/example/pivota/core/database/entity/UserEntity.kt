package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pivota.core.database.DatabaseConstants

/**
 * Represents the authenticated user's local profile and session context.
 * Currently focused on Individual users. Organization support will be added later.
 *
 * Note: Tokens (accessToken, refreshToken) are stored in DataStore for security,
 * not in Room database.
 */
@Entity(tableName = DatabaseConstants.Tables.USERS)
data class UserEntity(
    @PrimaryKey
    val uuid: String = "",
    val email: String,
    val firstName: String = "",
    val lastName: String = "",
    val userName: String = "",  // Full name from JWT (e.g., "John Doe")
    val phone: String? = null,
    val profileImage: String? = null,
    val isAuthenticated: Boolean = false,
    val isOnboardingComplete: Boolean = false,
    val hasSeenWelcomeScreen: Boolean = false,
    val primaryPurpose: String? = null,
    // JWT payload fields
    val role: String? = null,
    val accountType: String? = null,  // "INDIVIDUAL" or "ORGANIZATION"
    val accountId: String? = null,
    val accountName: String? = null,
    val organizationUuid: String? = null,
    val planSlug: String? = null,
    val tokenId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Get the display name for the user (first name or userName)
     */
    fun getDisplayName(): String {
        return when {
            firstName.isNotBlank() -> firstName
            userName.isNotBlank() -> userName.split(" ").firstOrNull() ?: "User"
            else -> email.split("@").firstOrNull() ?: "User"
        }
    }

    /**
     * Get the full name of the user
     */
    fun getFullName(): String {
        return when {
            userName.isNotBlank() -> userName
            firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
            firstName.isNotBlank() -> firstName
            else -> email.split("@").firstOrNull() ?: "User"
        }
    }

    /**
     * Get user initials for avatar
     */
    fun getUserInitials(): String {
        val name = getDisplayName()
        return when {
            name.length >= 2 -> name.take(2).uppercase()
            name.isNotEmpty() -> name.take(1).uppercase()
            else -> "U"
        }
    }
}