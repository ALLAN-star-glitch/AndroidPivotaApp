package com.example.pivota.auth.domain.model

/* ======================================================
   USER MODEL
====================================================== */

data class User(
    val uuid: String = "",
    val userCode: String? = null,
    val email: String,
    val firstName: String = "",
    val lastName: String = "",
    val userName: String = "",  // Full name from JWT (e.g., "John Doe")
    val personalPhone: String? = null,
    val profileImage: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val isAuthenticated: Boolean = false,
    val primaryPurpose: String? = null,
    val profileImageUrl: String? = null,
    // JWT additional fields
    val userUuid: String? = null,
    val accountId: String? = null,
    val accountName: String? = null,
    val accountType: String? = null,  // "INDIVIDUAL" or "ORGANIZATION"
    val tokenId: String? = null,
    val role: String? = null,
    val organizationUuid: String? = null,
    val planSlug: String? = null,
    // Profile data based on purpose
    val jobSeekerPreferences: JobSeekerPreferences? = null,
    val skilledProfessionalProfile: SkilledProfessionalProfile? = null,
    val intermediaryAgentProfile: IntermediaryAgentProfile? = null,
    val housingSeekerPreferences: HousingSeekerPreferences? = null,
    val supportBeneficiaryNeeds: SupportBeneficiaryNeeds? = null,
    val employerRequirements: EmployerRequirements? = null,
    val propertyOwnerPortfolio: PropertyOwnerPortfolio? = null
)

/* ======================================================
   JOB SEEKER MODELS
====================================================== */

data class JobSeekerPreferences(
    val headline: String = "",
    val isActivelySeeking: Boolean = true,
    val skills: List<String> = emptyList(),
    val industries: List<String> = emptyList(),
    val jobTypes: List<String> = emptyList(),
    val seniorityLevel: String? = null,
    val noticePeriod: String? = null,
    val expectedSalary: Int? = null,
    val workAuthorization: List<String> = emptyList(),
    val cvUrl: String? = null,
    val cvLastUpdated: String? = null,  // ADD THIS - missing field
    val portfolioImages: List<String> = emptyList(),
    val linkedInUrl: String? = null,
    val githubUrl: String? = null,
    val portfolioUrl: String? = null,
    val hasAgent: Boolean = false,
    val agentUuid: String? = null
)

/* ======================================================
   SKILLED PROFESSIONAL MODELS
====================================================== */

data class SkilledProfessionalProfile(
    val uuid: String? = null,  // ADD THIS - missing field
    val title: String? = null,
    val profession: String = "",
    val specialties: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),
    val yearsExperience: Int? = null,
    val licenseNumber: String? = null,
    val licenseBody: String? = null,  // ADD THIS - missing field
    val insuranceInfo: String? = null,
    val hourlyRate: Double? = null,
    val dailyRate: Double? = null,
    val paymentTerms: String? = null,
    val availableToday: Boolean = false,
    val availableWeekends: Boolean = false,
    val emergencyService: Boolean = false,
    val portfolioImages: List<String> = emptyList(),
    val certifications: List<String> = emptyList(),
    val isVerified: Boolean = false,  // ADD THIS - missing field
    val averageRating: Float = 0f,    // ADD THIS - missing field
    val totalReviews: Int = 0,        // ADD THIS - missing field
    val completedJobs: Int = 0        // ADD THIS - missing field
)

/* ======================================================
   INTERMEDIARY AGENT MODELS
====================================================== */

data class IntermediaryAgentProfile(
    val agentUuid: String? = null,  // ADD THIS (note: different name from uuid)
    val agentType: String,
    val specializations: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),
    val licenseNumber: String? = null,
    val licenseBody: String? = null,
    val yearsExperience: Int? = null,
    val agencyName: String? = null,
    val agencyUuid: String? = null,
    val commissionRate: Double? = null,
    val feeStructure: String? = null,
    val minimumFee: Double? = null,
    val typicalFee: String? = null,
    val about: String? = null,
    val profileImage: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val website: String? = null,
    val socialLinks: Map<String, String> = emptyMap(),
    val clientTypes: List<String> = emptyList(),
    val isVerified: Boolean = false,      // ADD THIS - missing field
    val averageRating: Float = 0f,        // ADD THIS - missing field
    val totalReviews: Int = 0,            // ADD THIS - missing field
    val completedDeals: Int = 0           // ADD THIS - missing field
)

/* ======================================================
   HOUSING SEEKER MODELS (UPDATED - Simplified)
====================================================== */

data class HousingSeekerPreferences(
    // Search Type (what are they looking for)
    val searchType: String? = null,  // "RENTAL", "SALE", "BOTH"
    val isLookingForRental: Boolean = false,
    val isLookingToBuy: Boolean = false,

    // Property Types (multi-select)
    val propertyTypes: List<String> = emptyList(),  // "APARTMENT", "HOUSE", "BEDSITTER", etc.

    // Legacy fields kept for backward compatibility
    val minBedrooms: Int? = null,
    val maxBedrooms: Int? = null,
    val minBudget: Double? = null,
    val maxBudget: Double? = null,
    val preferredTypes: List<String> = emptyList(),
    val preferredCities: List<String> = emptyList(),
    val preferredNeighborhoods: List<String> = emptyList(),
    val moveInDate: String? = null,
    val leaseDuration: String? = null,
    val householdSize: Int? = null,
    val hasPets: Boolean = false,
    val petDetails: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val searchRadiusKm: Int? = null,
    val hasAgent: Boolean = false,
    val agentUuid: String? = null
)

/* ======================================================
   SUPPORT BENEFICIARY MODELS
====================================================== */

data class SupportBeneficiaryNeeds(
    val needs: List<String> = emptyList(),
    val urgentNeeds: List<String> = emptyList(),
    val familySize: Int? = null,
    val dependents: Int? = null,
    val householdComposition: String? = null,
    val vulnerabilityFactors: List<String> = emptyList(),
    val city: String? = null,
    val neighborhood: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val landmark: String? = null,
    val prefersAnonymity: Boolean = false,
    val languagePreference: List<String> = emptyList(),
    val consentToShare: Boolean = false,
    val referredBy: String? = null,
    val referredByUuid: String? = null,
    val caseWorkerUuid: String? = null
)

/* ======================================================
   EMPLOYER MODELS
====================================================== */

data class EmployerRequirements(
    val companyName: String? = null,
    val industry: String? = null,
    val companySize: String? = null,
    val foundedYear: Int? = null,
    val description: String? = null,
    val logo: String? = null,
    val preferredSkills: List<String> = emptyList(),
    val remotePolicy: String? = null,
    val worksWithAgents: Boolean = false,
    val preferredAgents: List<String> = emptyList(),
    // Individual employer fields
    val businessName: String? = null,
    val isRegistered: Boolean = false,
    val yearsExperience: Int? = null,
    val isVerifiedEmployer: Boolean = false  // ADD THIS - missing field (note: different name)
)

/* ======================================================
   PROPERTY OWNER MODELS (UPDATED - Simplified)
====================================================== */

data class PropertyOwnerPortfolio(
    // Listing Type
    val listingType: String? = null,
    val isListingForRent: Boolean = false,
    val isListingForSale: Boolean = false,
    // Property details
    val propertyCount: Int? = null,
    val propertyTypes: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),
    // Legacy fields
    val isProfessional: Boolean = false,
    val licenseNumber: String? = null,
    val companyName: String? = null,
    val yearsInBusiness: Int? = null,
    val preferredPropertyTypes: List<String> = emptyList(),
    val usesAgent: Boolean = false,
    val managingAgentUuid: String? = null,
    val propertyPurpose: String? = null,
    val isVerifiedOwner: Boolean = false  // ADD THIS - missing field
)

/* ======================================================
   LOGIN RESPONSE SEALED CLASS
====================================================== */

sealed class LoginResponse {
    data class MfaRequired(
        val email: String,
        val uuid: String
    ) : LoginResponse()

    data class Authenticated(
        val user: User,
        val message: String?,
        val accessToken: String,
        val refreshToken: String
    ) : LoginResponse()
}

// Add to your domain models file (where User.kt is located)

/* ======================================================
   ACCOUNT MODEL
====================================================== */

data class Account(
    val uuid: String,
    val accountCode: String,
    val type: String,  // "INDIVIDUAL", "ORGANIZATION"
    val status: String,  // "ACTIVE", "PENDING_PAYMENT", "SUSPENDED", "CLOSED"
    val isVerified: Boolean,
    val verifiedFeatures: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val name: String? = null
) {
    val isActive: Boolean get() = status == "ACTIVE"
    val isIndividual: Boolean get() = type == "INDIVIDUAL"
    val isOrganization: Boolean get() = type == "ORGANIZATION"
}

/* ======================================================
   INDIVIDUAL PROFILE MODEL
====================================================== */

data class IndividualProfile(
    val bio: String? = null,
    val gender: String? = null,
    val dateOfBirth: String? = null,
    val nationalId: String? = null,
    val profileImage: String? = null
)

/* ======================================================
   ORGANIZATION PROFILE MODEL
====================================================== */

data class OrganizationProfile(
    val name: String,
    val type: String? = null,
    val registrationNo: String? = null,
    val kraPin: String? = null,
    val officialEmail: String? = null,
    val officialPhone: String? = null,
    val website: String? = null,
    val about: String? = null,
    val logo: String? = null,
    val physicalAddress: String? = null,
    val members: List<TeamMember> = emptyList(),
    val pendingInvitations: List<PendingInvitation> = emptyList()
)

data class TeamMember(
    val userUuid: String,
    val userName: String,
    val userEmail: String,
    val userImage: String? = null,
    val roleName: String
)

data class PendingInvitation(
    val id: String,
    val email: String,
    val status: String,
    val expiresAt: String
)

/* ======================================================
   VERIFICATION MODELS
====================================================== */

enum class VerificationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED;

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

enum class VerificationType {
    IDENTITY,
    BUSINESS,
    PROFESSIONAL_LICENSE,
    AGENT_LICENSE,
    NGO_REGISTRATION;

    val displayName: String
        get() = when (this) {
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

data class VerificationItem(
    val type: VerificationType,
    val status: VerificationStatus,
    val documentUrl: String? = null,
    val rejectionReason: String? = null,
    val verifiedAt: String? = null,
    val expiresAt: String? = null
) {
    val isApproved: Boolean get() = status == VerificationStatus.APPROVED
    val isPending: Boolean get() = status == VerificationStatus.PENDING
    val isRejected: Boolean get() = status == VerificationStatus.REJECTED
    val isExpired: Boolean get() = status == VerificationStatus.EXPIRED
}

/* ======================================================
   PROFILE COMPLETION MODEL
====================================================== */

data class ProfileCompletion(
    val accountCompleted: Boolean,
    val profileCompleted: Int,  // Percentage 0-100
    val documentsCompleted: Int  // Percentage 0-100
)

/* ======================================================
   COMPLETE PROFILE RESULT - Container for all profile data
====================================================== */

data class CompleteProfileResult(
    val account: Account,
    val user: User? = null,
    val individualProfile: IndividualProfile? = null,
    val organizationProfile: OrganizationProfile? = null,
    val jobSeekerProfile: JobSeekerPreferences? = null,
    val skilledProfessionalProfile: SkilledProfessionalProfile? = null,
    val intermediaryAgentProfile: IntermediaryAgentProfile? = null,
    val housingSeekerProfile: HousingSeekerPreferences? = null,
    val supportBeneficiaryProfile: SupportBeneficiaryNeeds? = null,
    val employerProfile: EmployerRequirements? = null,
    val propertyOwnerProfile: PropertyOwnerPortfolio? = null,
    val verifications: List<VerificationItem> = emptyList(),
    val completion: ProfileCompletion? = null
) {
    // Helper properties for UI
    val displayName: String
        get() = when {
            user != null -> "${user.firstName} ${user.lastName}".trim().ifEmpty { account.name ?: "User" }
            account.name != null -> account.name
            else -> "User"
        }

    val profileImageUrl: String?
        get() = user?.profileImage
            ?: individualProfile?.profileImage
            ?: organizationProfile?.logo

    val accountType: String
        get() = account.type

    val isFullyVerified: Boolean
        get() = verifications.all { it.status == VerificationStatus.APPROVED }

    val primaryProfileType: String
        get() = when {
            jobSeekerProfile != null -> "JOB_SEEKER"
            skilledProfessionalProfile != null -> "PROFESSIONAL"
            intermediaryAgentProfile != null -> "AGENT"
            housingSeekerProfile != null -> "PROPERTY_SEEKER"
            supportBeneficiaryProfile != null -> "BENEFICIARY"
            employerProfile != null -> "EMPLOYER"
            propertyOwnerProfile != null -> "PROPERTY_OWNER"
            organizationProfile != null -> "ORGANIZATION"
            else -> "ACCOUNT"
        }

    val profileCompletionPercentage: Int
        get() = completion?.profileCompleted ?: 0
}