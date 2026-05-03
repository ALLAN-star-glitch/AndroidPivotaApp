package com.example.pivota.dashboard.domain.model

data class ProfileAccount(
    val id: String,
    val code: String,
    val name: String?,
    val type: AccountType,
    val status: AccountStatus,
    val isVerified: Boolean,
    val verifiedFeatures: List<VerifiedFeature>,
    val createdAt: String,
    val updatedAt: String
) {
    val isActive: Boolean get() = status == AccountStatus.ACTIVE
    val isIndividual: Boolean get() = type == AccountType.INDIVIDUAL
    val isOrganization: Boolean get() = type == AccountType.ORGANIZATION
}

enum class AccountType {
    INDIVIDUAL, ORGANIZATION;

    companion object {
        fun fromString(value: String): AccountType = when (value.uppercase()) {
            "INDIVIDUAL" -> INDIVIDUAL
            "ORGANIZATION" -> ORGANIZATION
            else -> INDIVIDUAL
        }
    }
}

enum class AccountStatus {
    ACTIVE, SUSPENDED, PENDING_PAYMENT, CLOSED;

    companion object {
        fun fromString(value: String): AccountStatus = when (value.uppercase()) {
            "ACTIVE" -> ACTIVE
            "SUSPENDED" -> SUSPENDED
            "PENDING_PAYMENT" -> PENDING_PAYMENT
            "CLOSED" -> CLOSED
            else -> ACTIVE
        }
    }
}

enum class VerifiedFeature {
    IDENTITY, BUSINESS, PROFESSIONAL_LICENSE, AGENT_LICENSE, NGO_REGISTRATION;

    companion object {
        fun fromString(value: String): VerifiedFeature = when (value.uppercase()) {
            "IDENTITY" -> IDENTITY
            "BUSINESS" -> BUSINESS
            "PROFESSIONAL_LICENSE" -> PROFESSIONAL_LICENSE
            "AGENT_LICENSE" -> AGENT_LICENSE
            "NGO_REGISTRATION" -> NGO_REGISTRATION
            else -> IDENTITY
        }
    }
}