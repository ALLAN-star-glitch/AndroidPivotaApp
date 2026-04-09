package com.example.pivota.auth.domain.model

/* ======================================================
   USER MODEL
====================================================== */

data class User(
    val uuid: String = "",
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
    val title: String? = null,
    val profession: String = "",
    val specialties: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),
    val yearsExperience: Int? = null,
    val licenseNumber: String? = null,
    val insuranceInfo: String? = null,
    val hourlyRate: Double? = null,
    val dailyRate: Double? = null,
    val paymentTerms: String? = null,
    val availableToday: Boolean = false,
    val availableWeekends: Boolean = false,
    val emergencyService: Boolean = false,
    val portfolioImages: List<String> = emptyList(),
    val certifications: List<String> = emptyList()
)

/* ======================================================
   INTERMEDIARY AGENT MODELS
====================================================== */

data class IntermediaryAgentProfile(
    val agentType: String,  // HOUSING_AGENT, RECRUITMENT_AGENT, etc.
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
    val clientTypes: List<String> = emptyList()
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
    val yearsExperience: Int? = null
)

/* ======================================================
   PROPERTY OWNER MODELS (UPDATED - Simplified)
====================================================== */

data class PropertyOwnerPortfolio(
    // Listing Type (what are they listing)
    val listingType: String? = null,  // "RENT", "SALE", "BOTH"
    val isListingForRent: Boolean = false,
    val isListingForSale: Boolean = false,

    // Property details
    val propertyCount: Int? = null,
    val propertyTypes: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),

    // Legacy fields kept for backward compatibility
    val isProfessional: Boolean = false,
    val licenseNumber: String? = null,
    val companyName: String? = null,
    val yearsInBusiness: Int? = null,
    val preferredPropertyTypes: List<String> = emptyList(),
    val usesAgent: Boolean = false,
    val managingAgentUuid: String? = null,
    val propertyPurpose: String? = null  // "PRIMARY", "INVESTMENT", "BOTH"
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