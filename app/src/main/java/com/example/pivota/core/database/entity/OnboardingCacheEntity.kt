package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "onboarding_cache")
data class OnboardingCacheEntity(
    @PrimaryKey val id: Int = 1, // Always 1 because we only cache the current session
    val isOrganization: Boolean,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val orgName: String?,
    val currentStep: Int
)