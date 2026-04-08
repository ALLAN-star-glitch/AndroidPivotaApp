package com.example.pivota.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* ======================================================
   PURPOSE-SPECIFIC PROFILE DATA (Matches proto)
====================================================== */

@Serializable
data class JobSeekerProfileDataDto(
    @SerialName("headline") val headline: String? = null,
    @SerialName("isActivelySeeking") val isActivelySeeking: Boolean = true,
    @SerialName("skills") val skills: List<String> = emptyList(),
    @SerialName("industries") val industries: List<String> = emptyList(),
    @SerialName("jobTypes") val jobTypes: List<String> = emptyList(),
    @SerialName("seniorityLevel") val seniorityLevel: String? = null,
    @SerialName("noticePeriod") val noticePeriod: String? = null,
    @SerialName("expectedSalary") val expectedSalary: Double? = null,
    @SerialName("workAuthorization") val workAuthorization: List<String> = emptyList(),
    @SerialName("cvUrl") val cvUrl: String? = null,
    @SerialName("portfolioImages") val portfolioImages: List<String> = emptyList(),
    @SerialName("linkedInUrl") val linkedInUrl: String? = null,
    @SerialName("githubUrl") val githubUrl: String? = null,
    @SerialName("portfolioUrl") val portfolioUrl: String? = null,
    @SerialName("hasAgent") val hasAgent: Boolean = false,
    @SerialName("agentUuid") val agentUuid: String? = null
)

@Serializable
data class SkilledProfessionalProfileDataDto(
    @SerialName("title") val title: String? = null,
    @SerialName("profession") val profession: String? = null,
    @SerialName("specialties") val specialties: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("yearsExperience") val yearsExperience: Int? = null,
    @SerialName("licenseNumber") val licenseNumber: String? = null,
    @SerialName("insuranceInfo") val insuranceInfo: String? = null,
    @SerialName("hourlyRate") val hourlyRate: Double? = null,
    @SerialName("dailyRate") val dailyRate: Double? = null,
    @SerialName("paymentTerms") val paymentTerms: String? = null,
    @SerialName("availableToday") val availableToday: Boolean = false,
    @SerialName("availableWeekends") val availableWeekends: Boolean = false,
    @SerialName("emergencyService") val emergencyService: Boolean = false,
    @SerialName("portfolioImages") val portfolioImages: List<String> = emptyList(),
    @SerialName("certifications") val certifications: List<String> = emptyList()
)

@Serializable
data class IntermediaryAgentProfileDataDto(
    @SerialName("agentType") val agentType: String,
    @SerialName("specializations") val specializations: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("licenseNumber") val licenseNumber: String? = null,
    @SerialName("licenseBody") val licenseBody: String? = null,
    @SerialName("yearsExperience") val yearsExperience: Int? = null,
    @SerialName("agencyName") val agencyName: String? = null,
    @SerialName("agencyUuid") val agencyUuid: String? = null,
    @SerialName("commissionRate") val commissionRate: Double? = null,
    @SerialName("feeStructure") val feeStructure: String? = null,
    @SerialName("minimumFee") val minimumFee: Double? = null,
    @SerialName("typicalFee") val typicalFee: String? = null,
    @SerialName("about") val about: String? = null,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("contactEmail") val contactEmail: String? = null,
    @SerialName("contactPhone") val contactPhone: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("socialLinks") val socialLinks: Map<String, String> = emptyMap(),
    @SerialName("clientTypes") val clientTypes: List<String> = emptyList()
)

@Serializable
data class HousingSeekerProfileDataDto(
    // Basic preferences
    @SerialName("minBedrooms") val minBedrooms: Int? = null,
    @SerialName("maxBedrooms") val maxBedrooms: Int? = null,
    @SerialName("minBudget") val minBudget: Double? = null,
    @SerialName("maxBudget") val maxBudget: Double? = null,
    @SerialName("preferredTypes") val preferredTypes: List<String> = emptyList(),
    @SerialName("preferredCities") val preferredCities: List<String> = emptyList(),
    @SerialName("preferredNeighborhoods") val preferredNeighborhoods: List<String> = emptyList(),
    @SerialName("moveInDate") val moveInDate: String? = null,
    @SerialName("leaseDuration") val leaseDuration: String? = null,
    @SerialName("householdSize") val householdSize: Int? = null,
    @SerialName("hasPets") val hasPets: Boolean = false,
    @SerialName("petDetails") val petDetails: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("searchRadiusKm") val searchRadiusKm: Int? = null,
    @SerialName("hasAgent") val hasAgent: Boolean = false,
    @SerialName("agentUuid") val agentUuid: String? = null,

    // ======================================================
    // SEARCH TYPE (Matches backend listingType concept)
    // Values: "RENTAL", "SALE", "BOTH"
    // ======================================================
    @SerialName("searchType") val searchType: String? = null,
    @SerialName("isLookingForRental") val isLookingForRental: Boolean = false,
    @SerialName("isLookingToBuy") val isLookingToBuy: Boolean = false,

    // ======================================================
    // RENTAL PREFERENCES (Matches backend rental fields)
    // ======================================================
    @SerialName("minimumLeaseTerm") val minimumLeaseTerm: Int? = null,  // Minimum lease term in months
    @SerialName("maximumLeaseTerm") val maximumLeaseTerm: Int? = null,  // Maximum lease term in months
    @SerialName("depositAmount") val depositAmount: Double? = null,      // Security deposit amount
    @SerialName("isPetFriendly") val isPetFriendly: Boolean = false,     // Whether pets are allowed
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean = false, // Utilities included in rent
    @SerialName("utilitiesDetails") val utilitiesDetails: String? = null,    // Details about utilities

    // ======================================================
    // SALE PREFERENCES (Matches backend sale fields)
    // ======================================================
    @SerialName("isNegotiable") val isNegotiable: Boolean = true,         // Whether price is negotiable
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean = false // Title deed available
)

@Serializable
data class SupportBeneficiaryProfileDataDto(
    @SerialName("needs") val needs: List<String> = emptyList(),
    @SerialName("urgentNeeds") val urgentNeeds: List<String> = emptyList(),
    @SerialName("familySize") val familySize: Int? = null,
    @SerialName("dependents") val dependents: Int? = null,
    @SerialName("householdComposition") val householdComposition: String? = null,
    @SerialName("vulnerabilityFactors") val vulnerabilityFactors: List<String> = emptyList(),
    @SerialName("city") val city: String? = null,
    @SerialName("neighborhood") val neighborhood: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("landmark") val landmark: String? = null,
    @SerialName("prefersAnonymity") val prefersAnonymity: Boolean = false,
    @SerialName("languagePreference") val languagePreference: List<String> = emptyList(),
    @SerialName("consentToShare") val consentToShare: Boolean = false,
    @SerialName("referredBy") val referredBy: String? = null,
    @SerialName("referredByUuid") val referredByUuid: String? = null,
    @SerialName("caseWorkerUuid") val caseWorkerUuid: String? = null
)

@Serializable
data class EmployerProfileDataDto(
    @SerialName("companyName") val companyName: String? = null,
    @SerialName("industry") val industry: String? = null,
    @SerialName("companySize") val companySize: String? = null,
    @SerialName("foundedYear") val foundedYear: Int? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("logo") val logo: String? = null,
    @SerialName("preferredSkills") val preferredSkills: List<String> = emptyList(),
    @SerialName("remotePolicy") val remotePolicy: String? = null,
    @SerialName("worksWithAgents") val worksWithAgents: Boolean = false,
    @SerialName("preferredAgents") val preferredAgents: List<String> = emptyList(),
    @SerialName("businessName") val businessName: String? = null,
    @SerialName("isRegistered") val isRegistered: Boolean = false,
    @SerialName("yearsExperience") val yearsExperience: Int? = null
)

@Serializable
data class PropertyOwnerProfileDataDto(
    // Basic information
    @SerialName("isProfessional") val isProfessional: Boolean = false,
    @SerialName("licenseNumber") val licenseNumber: String? = null,
    @SerialName("companyName") val companyName: String? = null,
    @SerialName("yearsInBusiness") val yearsInBusiness: Int? = null,
    @SerialName("preferredPropertyTypes") val preferredPropertyTypes: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("usesAgent") val usesAgent: Boolean = false,
    @SerialName("managingAgentUuid") val managingAgentUuid: String? = null,
    @SerialName("propertyCount") val propertyCount: Int? = null,
    @SerialName("propertyTypes") val propertyTypes: List<String> = emptyList(),
    @SerialName("propertyPurpose") val propertyPurpose: String? = null,

    // ======================================================
    // LISTING TYPE (Matches backend listingType)
    // Values: "RENT", "SALE", "BOTH"
    // ======================================================
    @SerialName("listingType") val listingType: String? = null,
    @SerialName("isListingForRent") val isListingForRent: Boolean = false,
    @SerialName("isListingForSale") val isListingForSale: Boolean = false
)

/* ======================================================
   SIGNUP REQUEST (Matches proto SignupRequest without clientInfo)
====================================================== */

@Serializable
data class SignupRequestDto(
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String,
    @SerialName("planSlug") val planSlug: String = "free-forever",
    @SerialName("code") val code: String,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("primaryPurpose") val primaryPurpose: String? = null,
    // Oneof fields
    @SerialName("jobSeekerData") val jobSeekerData: JobSeekerProfileDataDto? = null,
    @SerialName("skilledProfessionalData") val skilledProfessionalData: SkilledProfessionalProfileDataDto? = null,
    @SerialName("intermediaryAgentData") val intermediaryAgentData: IntermediaryAgentProfileDataDto? = null,
    @SerialName("housingSeekerData") val housingSeekerData: HousingSeekerProfileDataDto? = null,
    @SerialName("supportBeneficiaryData") val supportBeneficiaryData: SupportBeneficiaryProfileDataDto? = null,
    @SerialName("employerData") val employerData: EmployerProfileDataDto? = null,
    @SerialName("propertyOwnerData") val propertyOwnerData: PropertyOwnerProfileDataDto? = null
)

/* ======================================================
   LOGIN REQUEST (Matches proto LoginRequest without clientInfo)
====================================================== */

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

/* ======================================================
   VERIFY MFA LOGIN REQUEST (Matches proto VerifyMfaLoginRequest without clientInfo)
====================================================== */

@Serializable
data class VerifyMfaLoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("code") val code: String
)

/* ======================================================
   REQUEST OTP (Matches proto RequestOtpRequest without clientInfo)
====================================================== */

@Serializable
data class RequestOtpRequestDto(
    @SerialName("email") val email: String,
    @SerialName("purpose") val purpose: String,  // "SIGNUP", "FORGOT_PASSWORD", "LOGIN"
    @SerialName("phone") val phone: String? = null,
)

/* ======================================================
   VERIFY OTP REQUEST (Matches proto VerifyOtpRequest without clientInfo)
====================================================== */

@Serializable
data class VerifyOtpRequestDto(
    @SerialName("email") val email: String,
    @SerialName("code") val code: String,
    @SerialName("purpose") val purpose: String
)