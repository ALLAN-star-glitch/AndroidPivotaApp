package com.example.pivota.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ======================================================
// RESPONSE DTOS (Match UserProfileResponse from proto)
// ======================================================

@Serializable
data class ProfileResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: UserProfDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class UserProfDto(
    @SerialName("account") val account: AccountBaseDto,
    @SerialName("user") val user: UserBaseDto,
    @SerialName("profile") val profile: UserProfileMetadataDto,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("completion") val completion: CompletionResponseDto,
    @SerialName("organization") val organization: OrganizationBaseDto? = null,
    @SerialName("contractor") val contractor: ContractorDataDto? = null,
    @SerialName("jobSeekerProfile") val jobSeekerProfile: JobSeekerProfileDataDto? = null,
    @SerialName("skilledProfessionalProfile") val skilledProfessionalProfile: SkilledProfessionalProfileDataDto? = null,
    @SerialName("housingSeekerProfile") val housingSeekerProfile: HousingSeekerProfileDataDto? = null,
    @SerialName("propertyOwnerProfile") val propertyOwnerProfile: PropertyOwnerProfileDataDto? = null,
    @SerialName("supportBeneficiaryProfile") val supportBeneficiaryProfile: SupportBeneficiaryProfileDataDto? = null,
    @SerialName("intermediaryAgentProfile") val intermediaryAgentProfile: IntermediaryAgentProfileDataDto? = null
)

// ======================================================
// REQUEST/PROFILE DATA DTOs (Match proto *ProfileData messages)
// ======================================================

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
    @SerialName("searchType") val searchType: String? = null,
    @SerialName("isLookingForRental") val isLookingForRental: Boolean = false,
    @SerialName("isLookingToBuy") val isLookingToBuy: Boolean = false
)

@Serializable
data class PropertyOwnerProfileDataDto(
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
    @SerialName("listingType") val listingType: String? = null,
    @SerialName("isListingForRent") val isListingForRent: Boolean = false,
    @SerialName("isListingForSale") val isListingForSale: Boolean = false
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

// ======================================================
// RESPONSE AGGREGATE DTOs (Match proto *Data messages)
// These contain additional fields like uuid, ratings, etc.
// ======================================================

@Serializable
data class SkilledProfessionalDataDto(
    @SerialName("id") val id: String,
    @SerialName("uuid") val uuid: String,
    @SerialName("title") val title: String? = null,
    @SerialName("profession") val profession: String? = null,
    @SerialName("specialties") val specialties: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("yearsExperience") val yearsExperience: Int? = null,
    @SerialName("licenseNumber") val licenseNumber: String? = null,
    @SerialName("hourlyRate") val hourlyRate: Double? = null,
    @SerialName("dailyRate") val dailyRate: Double? = null,
    @SerialName("isVerified") val isVerified: Boolean = false,
    @SerialName("averageRating") val averageRating: Float = 0f,
    @SerialName("totalReviews") val totalReviews: Int = 0,
    @SerialName("portfolioImages") val portfolioImages: List<String> = emptyList()
)

@Serializable
data class IntermediaryAgentDataDto(
    @SerialName("id") val id: String,
    @SerialName("uuid") val uuid: String,
    @SerialName("agentType") val agentType: String,
    @SerialName("specializations") val specializations: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("licenseNumber") val licenseNumber: String? = null,
    @SerialName("yearsExperience") val yearsExperience: Int? = null,
    @SerialName("agencyName") val agencyName: String? = null,
    @SerialName("commissionRate") val commissionRate: Double? = null,
    @SerialName("isVerified") val isVerified: Boolean = false,
    @SerialName("averageRating") val averageRating: Float = 0f,
    @SerialName("totalReviews") val totalReviews: Int = 0,
    @SerialName("about") val about: String? = null,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("licenseBody") val licenseBody: String? = null,
    @SerialName("agencyUuid") val agencyUuid: String? = null,
    @SerialName("feeStructure") val feeStructure: String? = null,
    @SerialName("minimumFee") val minimumFee: Double? = null,
    @SerialName("typicalFee") val typicalFee: String? = null,
    @SerialName("completedDeals") val completedDeals: Int = 0,
    @SerialName("contactEmail") val contactEmail: String? = null,
    @SerialName("contactPhone") val contactPhone: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("socialLinks") val socialLinks: Map<String, String> = emptyMap(),
    @SerialName("clientTypes") val clientTypes: List<String> = emptyList()
)

// ======================================================
// BASE DTOs (Match proto base messages)
// ======================================================

@Serializable
data class AccountBaseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("accountCode") val accountCode: String,
    @SerialName("type") val type: String,
    @SerialName("isBusiness") val isBusiness: Boolean,
    @SerialName("businessType") val businessType: String? = null
)

@Serializable
data class UserBaseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String,
    @SerialName("status") val status: String,
    @SerialName("roleName") val roleName: String
)

@Serializable
data class UserProfileMetadataDto(
    @SerialName("bio") val bio: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("profileImage") val profileImage: String? = null
)

@Serializable
data class CompletionResponseDto(
    @SerialName("percentage") val percentage: Int,
    @SerialName("missingFields") val missingFields: List<String>? = null,
    @SerialName("isComplete") val isComplete: Boolean
)

@Serializable
data class OrganizationBaseDto(
    @SerialName("id") val id: String,
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
    @SerialName("orgCode") val orgCode: String,
    @SerialName("verificationStatus") val verificationStatus: String,
    @SerialName("officialEmail") val officialEmail: String
)

@Serializable
data class ContractorDataDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("accountId") val accountId: String,
    @SerialName("specialties") val specialties: List<String>,
    @SerialName("serviceAreas") val serviceAreas: List<String>,
    @SerialName("yearsExperience") val yearsExperience: Int,
    @SerialName("isVerified") val isVerified: Boolean,
    @SerialName("averageRating") val averageRating: Float,
    @SerialName("totalReviews") val totalReviews: Int,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class OrganizationProfileDataDto(
    @SerialName("id") val id: String,
    @SerialName("uuid") val uuid: String,
    @SerialName("orgCode") val orgCode: String,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String? = null,
    @SerialName("verificationStatus") val verificationStatus: String,
    @SerialName("registrationNo") val registrationNo: String? = null,
    @SerialName("kraPin") val kraPin: String? = null,
    @SerialName("officialEmail") val officialEmail: String? = null,
    @SerialName("officialPhone") val officialPhone: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("about") val about: String? = null,
    @SerialName("logo") val logo: String? = null,
    @SerialName("physicalAddress") val physicalAddress: String? = null,
    @SerialName("coverPhoto") val coverPhoto: String? = null,
    @SerialName("account") val account: AccountBaseDto,
    @SerialName("admin") val admin: AdminResponseDto? = null,
    @SerialName("completion") val completion: CompletionResponseDto,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("employerProfile") val employerProfile: EmployerProfileDataDto? = null,
    @SerialName("propertyOwnerProfile") val propertyOwnerProfile: PropertyOwnerProfileDataDto? = null,
    @SerialName("skilledProfessionalProfile") val skilledProfessionalProfile: SkilledProfessionalProfileDataDto? = null,
    @SerialName("intermediaryAgentProfile") val intermediaryAgentProfile: IntermediaryAgentProfileDataDto? = null,
    @SerialName("socialServiceProviderProfile") val socialServiceProviderProfile: SocialServiceProviderProfileDataDto? = null
)

@Serializable
data class AdminResponseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("roleName") val roleName: String
)

@Serializable
data class SocialServiceProviderProfileDataDto(
    @SerialName("providerType") val providerType: String,
    @SerialName("servicesOffered") val servicesOffered: List<String> = emptyList(),
    @SerialName("targetBeneficiaries") val targetBeneficiaries: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("about") val about: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("contactEmail") val contactEmail: String? = null,
    @SerialName("contactPhone") val contactPhone: String? = null,
    @SerialName("officeHours") val officeHours: String? = null,
    @SerialName("physicalAddress") val physicalAddress: String? = null,
    @SerialName("peopleServed") val peopleServed: Int? = null,
    @SerialName("yearEstablished") val yearEstablished: Int? = null,
    @SerialName("acceptsDonations") val acceptsDonations: Boolean = false,
    @SerialName("needsVolunteers") val needsVolunteers: Boolean = false,
    @SerialName("donationInfo") val donationInfo: String? = null,
    @SerialName("volunteerNeeds") val volunteerNeeds: String? = null,
    @SerialName("operatingName") val operatingName: String? = null,
    @SerialName("yearsExperience") val yearsExperience: Int? = null,
    @SerialName("qualifications") val qualifications: List<String> = emptyList(),
    @SerialName("availability") val availability: String? = null
)

@Serializable
data class ErrorPayloadDto(
    @SerialName("message") val message: String,
    @SerialName("code") val code: Int? = null
)