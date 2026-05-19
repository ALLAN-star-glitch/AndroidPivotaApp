package com.example.pivota.dashboard.domain.model.profile_models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
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

@Serializable
enum class AccountType {
    @SerialName("INDIVIDUAL")
    INDIVIDUAL,

    @SerialName("ORGANIZATION")
    ORGANIZATION;

    companion object {
        fun fromString(value: String): AccountType = when (value.uppercase()) {
            "INDIVIDUAL" -> INDIVIDUAL
            "ORGANIZATION" -> ORGANIZATION
            else -> INDIVIDUAL
        }
    }
}

@Serializable
enum class AccountStatus {
    @SerialName("ACTIVE")
    ACTIVE,

    @SerialName("SUSPENDED")
    SUSPENDED,

    @SerialName("PENDING_PAYMENT")
    PENDING_PAYMENT,

    @SerialName("CLOSED")
    CLOSED;

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

@Serializable
enum class VerifiedFeature {
    @SerialName("IDENTITY")
    IDENTITY,

    @SerialName("BUSINESS")
    BUSINESS,

    @SerialName("PROFESSIONAL_LICENSE")
    PROFESSIONAL_LICENSE,

    @SerialName("AGENT_LICENSE")
    AGENT_LICENSE,

    @SerialName("NGO_REGISTRATION")
    NGO_REGISTRATION;

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