package com.example.pivota.dashboard.domain.model

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

enum class ProfileType {
    PROFESSIONAL,
    AGENT,
    JOB_SEEKER,
    PROPERTY_OWNER,
    HOUSING_SEEKER,
    BENEFICIARY,
    EMPLOYER,
    ORGANIZATION,
    INDIVIDUAL,
    ACCOUNT
}