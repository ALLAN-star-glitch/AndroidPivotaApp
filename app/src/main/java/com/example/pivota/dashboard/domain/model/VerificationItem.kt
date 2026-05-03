package com.example.pivota.dashboard.domain.model

data class VerificationItem(
    val type: VerificationType,
    val status: VerificationStatus,
    val documentUrl: String?,
    val rejectionReason: String?,
    val verifiedAt: String?,
    val expiresAt: String?
) {
    val isApproved: Boolean get() = status == VerificationStatus.APPROVED
    val isPending: Boolean get() = status == VerificationStatus.PENDING
    val isRejected: Boolean get() = status == VerificationStatus.REJECTED
    val isExpired: Boolean get() = status == VerificationStatus.EXPIRED

    val statusColor: VerificationStatusColor get() = when (status) {
        VerificationStatus.APPROVED -> VerificationStatusColor.SUCCESS
        VerificationStatus.PENDING -> VerificationStatusColor.WARNING
        VerificationStatus.REJECTED -> VerificationStatusColor.ERROR
        VerificationStatus.EXPIRED -> VerificationStatusColor.NEUTRAL
    }
}

enum class VerificationType {
    IDENTITY, BUSINESS, PROFESSIONAL_LICENSE, AGENT_LICENSE, NGO_REGISTRATION;

    val displayName: String get() = when (this) {
        IDENTITY -> "ID Verification"
        BUSINESS -> "Business Verification"
        PROFESSIONAL_LICENSE -> "Professional License"
        AGENT_LICENSE -> "Agent License"
        NGO_REGISTRATION -> "NGO Registration"
    }

    companion object {
        fun fromString(value: String): VerificationType = when (value.uppercase()) {
            "IDENTITY" -> IDENTITY
            "BUSINESS" -> BUSINESS
            "PROFESSIONAL_LICENSE" -> PROFESSIONAL_LICENSE
            "AGENT_LICENSE" -> AGENT_LICENSE
            "NGO_REGISTRATION" -> NGO_REGISTRATION
            else -> IDENTITY
        }
    }
}

enum class VerificationStatus {
    PENDING, APPROVED, REJECTED, EXPIRED;

    companion object {
        fun fromString(value: String): VerificationStatus = when (value.uppercase()) {
            "PENDING" -> PENDING
            "APPROVED" -> APPROVED
            "REJECTED" -> REJECTED
            "EXPIRED" -> EXPIRED
            else -> PENDING
        }
    }
}

enum class VerificationStatusColor {
    SUCCESS, WARNING, ERROR, NEUTRAL
}