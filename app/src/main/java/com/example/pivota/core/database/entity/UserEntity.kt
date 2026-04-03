package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pivota.core.database.DatabaseConstants

/**
 * Represents the authenticated user's local profile and session context.
 * Currently focused on Individual users. Organization support will be added later.
 */
@Entity(tableName = DatabaseConstants.Tables.USERS)
data class UserEntity(
    @PrimaryKey
    val uuid: String = "",
    val email: String,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val profileImage: String? = null,
    val isAuthenticated: Boolean = false,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val isOnboardingComplete: Boolean = false,
    val hasSeenWelcomeScreen: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)