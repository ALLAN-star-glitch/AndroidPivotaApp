package com.example.pivota.dashboard.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CompleteProfile(
    val user: ProfileUser,
    val account: ProfileAccount,
    val individualProfile: IndividualProfile?,
    val organizationProfile: OrganizationProfile?,
    val professionalProfile: ProfessionalProfile?,
    val jobSeekerProfile: JobSeekerProfile?,
    val agentProfile: AgentProfile?,
    val housingSeekerProfile: HousingSeekerProfile?,
    val propertyOwnerProfile: PropertyOwnerProfile?,
    val beneficiaryProfile: BeneficiaryProfile?,
    val employerProfile: EmployerProfile?,
    val verifications: List<VerificationItem>,
    val completion: ProfileCompletion,
    val createdAt: String,
    val updatedAt: String
) {
    val displayName: String get() = user.displayName
    val profileImageUrl: String? get() = user.profileImageUrl ?: individualProfile?.profileImage
    val accountType: String get() = account.type.name
    val isFullyVerified: Boolean get() = verifications.all { it.isApproved }

    val primaryProfileType: ProfileType get() = when {
        professionalProfile != null -> ProfileType.PROFESSIONAL
        agentProfile != null -> ProfileType.AGENT
        jobSeekerProfile != null -> ProfileType.JOB_SEEKER
        propertyOwnerProfile != null -> ProfileType.PROPERTY_OWNER
        housingSeekerProfile != null -> ProfileType.HOUSING_SEEKER
        beneficiaryProfile != null -> ProfileType.BENEFICIARY
        employerProfile != null -> ProfileType.EMPLOYER
        organizationProfile != null -> ProfileType.ORGANIZATION
        individualProfile != null -> ProfileType.INDIVIDUAL
        else -> ProfileType.ACCOUNT
    }

    val primaryProfileData: Any? get() = when (primaryProfileType) {
        ProfileType.PROFESSIONAL -> professionalProfile
        ProfileType.AGENT -> agentProfile
        ProfileType.JOB_SEEKER -> jobSeekerProfile
        ProfileType.PROPERTY_OWNER -> propertyOwnerProfile
        ProfileType.HOUSING_SEEKER -> housingSeekerProfile
        ProfileType.BENEFICIARY -> beneficiaryProfile
        ProfileType.EMPLOYER -> employerProfile
        ProfileType.ORGANIZATION -> organizationProfile
        ProfileType.INDIVIDUAL -> individualProfile
        ProfileType.ACCOUNT -> null
    }
}


@Serializable
enum class ProfileType {
    @SerialName("PROFESSIONAL")
    PROFESSIONAL,

    @SerialName("AGENT")
    AGENT,

    @SerialName("JOB_SEEKER")
    JOB_SEEKER,

    @SerialName("PROPERTY_OWNER")
    PROPERTY_OWNER,

    @SerialName("HOUSING_SEEKER")
    HOUSING_SEEKER,

    @SerialName("BENEFICIARY")
    BENEFICIARY,

    @SerialName("EMPLOYER")
    EMPLOYER,

    @SerialName("ORGANIZATION")
    ORGANIZATION,

    @SerialName("INDIVIDUAL")
    INDIVIDUAL,

    @SerialName("ACCOUNT")
    ACCOUNT
}