package com.example.pivota.auth.domain.model

/**
 * The core Domain Model for a User in PivotaConnect.
 * Represents the "Identity" and its "Root Anchor" (Account).
 */
data class User(
    val uuid: String,
    val accountUuid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val personalPhone: String = "", // empty string if not provided
    val accountType: AccountType,
    val isVerified: Boolean = false,
    val selectedPlan: SubscriptionPlan? = null,
    val isOnboardingComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Sealed class for account type: Individual or Organization.
 */
sealed class AccountType {

    /** Individual account type */
    data object Individual : AccountType()

    /** Organization account type */
    data class Organization(
        val orgUuid: String,
        val orgName: String,
        val orgType: String,           // NGO, Company, Institution, etc.
        val orgEmail: String,
        val orgPhone: String? = null,
        val orgAddress: String,
        val adminFirstName: String,
        val adminLastName: String
    ) : AccountType()
}

/** Subscription plans for users */
enum class SubscriptionPlan {
    FREE_FOREVER,
    BRONZE,
    SILVER,
    GOLD
}
