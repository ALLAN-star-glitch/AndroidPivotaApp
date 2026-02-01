package com.example.pivota.auth.domain.model

/**
 * The core Domain Model for a User in PivotaConnect.
 * This represents the "Identity" and its "Root Anchor" (Account).
 */
data class User(
    val uuid: String,
    val accountUuid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val personalPhone: String, // Changed to String to match mapper logic (using empty string fallback)
    val role: UserRole,
    val accountType: AccountType,
    val isVerified: Boolean = false,
    val selectedPlan: SubscriptionPlan? = null,
    val isOnboardingComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

sealed class AccountType {
    data object Individual : AccountType()

    data class Organization(
        val orgUuid: String,
        val orgName: String,
        // Optional metadata from the OrganizationProfile layer
        val verificationStatus: String = "PENDING"
    ) : AccountType()
}

/**
 * UserRole matches the 'roleName' strings used in your NestJS RBAC service.
 */
enum class UserRole {
    BUSINESS_ADMINISTRATOR,
    GeneralUser
}

enum class SubscriptionPlan {
    FREE_FOREVER,
    BRONZE,
    SILVER,
    GOLD
}