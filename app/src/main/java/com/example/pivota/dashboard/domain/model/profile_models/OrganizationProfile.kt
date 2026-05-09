package com.example.pivota.dashboard.domain.model.profile_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrganizationProfile(
    val id: String,
    val name: String,
    val type: String?,
    val registrationNumber: String?,
    val kraPin: String?,
    val officialEmail: String?,
    val officialPhone: String?,
    val website: String?,
    val about: String?,
    val logo: String?,
    val physicalAddress: String?,
    val members: List<TeamMember>,
    val pendingInvitations: List<PendingInvitation>
) {
    val hasMembers: Boolean get() = members.isNotEmpty()
    val hasPendingInvitations: Boolean get() = pendingInvitations.isNotEmpty()
    val memberCount: Int get() = members.size
}
@Serializable
data class TeamMember(
    val userId: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val role: String
)
@Serializable
data class PendingInvitation(
    val id: String,
    val email: String,
    val status: InvitationStatus,
    val expiresAt: String
)

@Serializable
enum class InvitationStatus {
    @SerialName("PENDING")
    PENDING,

    @SerialName("ACCEPTED")
    ACCEPTED,

    @SerialName("EXPIRED")
    EXPIRED,

    @SerialName("CANCELLED")
    CANCELLED;

    companion object {
        fun fromString(value: String): InvitationStatus = when (value.uppercase()) {
            "PENDING" -> PENDING
            "ACCEPTED" -> ACCEPTED
            "EXPIRED" -> EXPIRED
            "CANCELLED" -> CANCELLED
            else -> PENDING
        }
    }
}
