package com.example.pivota.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor

/* ======================================================
   BASE RESPONSE DTO (Matches backend BaseResponseDto)
====================================================== */

@Serializable
data class BaseResponseDto<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,  // String in backend, but can be number at gateway
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
) {
    companion object {
        fun <T> ok(data: T, message: String = "Success", code: String = "OK"): BaseResponseDto<T> {
            return BaseResponseDto(
                success = true,
                message = message,
                code = code,
                data = data,
                error = null
            )
        }

        fun fail(message: String, code: String = "INTERNAL_ERROR", details: Any? = null): BaseResponseDto<Nothing> {
            return BaseResponseDto(
                success = false,
                message = message,
                code = code,
                data = null,
                error = ErrorPayloadDto(
                    message = message,
                    code = code,
                    details = details
                )
            )
        }
    }
}

@Serializable(with = ErrorPayloadDtoSerializer::class)
data class ErrorPayloadDto(
    val message: String,
    val code: String? = null,
    val details: Any? = null
)

object ErrorPayloadDtoSerializer : kotlinx.serialization.KSerializer<ErrorPayloadDto> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ErrorPayloadDto")

    override fun deserialize(decoder: Decoder): ErrorPayloadDto {
        val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
            ?: return ErrorPayloadDto(message = "Unknown error")

        val element = jsonDecoder.decodeJsonElement()
        val jsonObject = element.jsonObject

        val message = when (val msgElement = jsonObject["message"]) {
            null -> "Unknown error"
            is JsonPrimitive -> msgElement.content
            else -> {
                msgElement.jsonArray.joinToString(", ") { it.jsonPrimitive.content }
            }
        }

        return ErrorPayloadDto(
            message = message,
            code = jsonObject["code"]?.jsonPrimitive?.contentOrNull,
            details = jsonObject["details"]
        )
    }

    override fun serialize(encoder: Encoder, value: ErrorPayloadDto) {
        // Not needed for deserialization
    }
}

/* ======================================================
   BASE OTP RESPONSE (Matches backend BaseOtpResponse)
====================================================== */
typealias BaseOtpResponseDto = BaseResponseDto<Nothing>

/* ======================================================
   VERIFY OTP RESPONSE (Matches backend VerifyOtpResponse)
====================================================== */

@Serializable
data class VerifyOtpDataDto(
    @SerialName("verified") val verified: Boolean
)

typealias VerifyOtpResponseDto = BaseResponseDto<VerifyOtpDataDto>

/* ======================================================
   SIGNUP RESPONSE (Matches backend SignupResponse)
   Updated to support auto-login with tokens
====================================================== */

@Serializable
data class SignupSuccessDataDto(
    @SerialName("message") val message: String,  // "Signup successful"

    // For auto-login (free plan)
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,
    @SerialName("redirectTo") val redirectTo: String? = null,  // "/dashboard"

    // For premium payment required
    @SerialName("redirectUrl") val redirectUrl: String? = null,
    @SerialName("merchantReference") val merchantReference: String? = null
)

typealias SignupResponseDto = BaseResponseDto<SignupSuccessDataDto>
/* ======================================================
   LOGIN RESPONSE (Matches backend LoginResponse)
   Used for both Login and VerifyMfaLogin
====================================================== */

@Serializable
data class LoginDataDto(
    // Stage 1: MFA required
    @SerialName("email") val email: String? = null,
    @SerialName("uuid") val uuid: String? = null,
    @SerialName("message") val message: String? = null,

    // Stage 2: Tokens
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,

    // JWT Payload fields
    @SerialName("userUuid") val userUuid: String? = null,
    @SerialName("userName") val userName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("primaryPurpose") val primaryPurpose: String? = null,
    @SerialName("accountId") val accountId: String? = null,
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("accountType") val accountType: String? = null,
    @SerialName("tokenId") val tokenId: String? = null,
    @SerialName("role") val role: String? = null,
    @SerialName("organizationUuid") val organizationUuid: String? = null,
    @SerialName("planSlug") val planSlug: String? = null
)

typealias LoginResponseDto = BaseResponseDto<LoginDataDto>

/* ======================================================
   REFRESH TOKEN RESPONSE (Matches backend BaseRefreshTokenResponse)
====================================================== */

@Serializable
data class RefreshTokenDataDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String
)

typealias RefreshTokenResponseDto = BaseResponseDto<RefreshTokenDataDto>

/* ======================================================
   DEV TOKEN RESPONSE (Matches backend BaseDevTokenResponse)
====================================================== */

typealias BaseDevTokenResponseDto = BaseResponseDto<RefreshTokenDataDto>

/* ======================================================
   GET ACTIVE SESSIONS RESPONSE (Matches backend GetActiveSessionsResponse)
====================================================== */

@Serializable
data class SessionDto(
    @SerialName("id") val id: Int,
    @SerialName("tokenId") val tokenId: String,
    @SerialName("device") val device: String? = null,
    @SerialName("ipAddress") val ipAddress: String? = null,
    @SerialName("userAgent") val userAgent: String? = null,
    @SerialName("os") val os: String? = null,
    @SerialName("lastActiveAt") val lastActiveAt: String,
    @SerialName("expiresAt") val expiresAt: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("revoked") val revoked: Boolean
)

typealias GetActiveSessionsResponseDto = BaseResponseDto<List<SessionDto>>

/* ======================================================
   GOOGLE SIGN-IN REQUEST (Matches proto GoogleLoginRequest)
   Note: clientInfo is handled by the gateway decorator, not passed in body
====================================================== */

@Serializable
data class GoogleSignInRequestDto(
    @SerialName("token") val token: String,
    @SerialName("onboardingData") val onboardingData: GoogleOnboardingDataDto? = null
)

/* ======================================================
   GOOGLE ONBOARDING DATA (Matches proto GoogleOnboardingData)
====================================================== */

@Serializable
data class GoogleOnboardingDataDto(
    @SerialName("primaryPurpose") val primaryPurpose: String? = null,
    @SerialName("jobSeekerData") val jobSeekerData: JobSeekerProfileDataDto? = null,
    @SerialName("housingSeekerData") val housingSeekerData: HousingSeekerProfileDataDto? = null,
    @SerialName("skilledProfessionalData") val skilledProfessionalData: SkilledProfessionalProfileDataDto? = null,
    @SerialName("intermediaryAgentData") val intermediaryAgentData: IntermediaryAgentProfileDataDto? = null,
    @SerialName("supportBeneficiaryData") val supportBeneficiaryData: SupportBeneficiaryProfileDataDto? = null,
    @SerialName("employerData") val employerData: EmployerProfileDataDto? = null,
    @SerialName("propertyOwnerData") val propertyOwnerData: PropertyOwnerProfileDataDto? = null
)


/* ======================================================
   PROFILE FETCH RESPONSE DTOs
   Matches backend GET /profile endpoint response
====================================================== */

@Serializable
data class ProfileResponseDto(
    @SerialName("account") val account: AccountDataDto,
    @SerialName("user") val user: UserDataDto? = null,
    @SerialName("profile") val profile: IndividualProfileDto? = null,  // Note: 'profile', not 'individualProfile'
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("completion") val completion: ProfileCompletionDto? = null,
    // Other profiles can be null since they're not in the response yet
    @SerialName("jobSeekerProfile") val jobSeekerProfile: JobSeekerProfileDto? = null,
    @SerialName("skilledProfessionalProfile") val skilledProfessionalProfile: SkilledProfessionalProfileDto? = null,
    @SerialName("intermediaryAgentProfile") val intermediaryAgentProfile: IntermediaryAgentProfileDto? = null,
    @SerialName("housingSeekerProfile") val housingSeekerProfile: HousingSeekerProfileDto? = null,
    @SerialName("supportBeneficiaryProfile") val supportBeneficiaryProfile: SupportBeneficiaryProfileDto? = null,
    @SerialName("employerProfile") val employerProfile: EmployerProfileDto? = null,
    @SerialName("propertyOwnerProfile") val propertyOwnerProfile: PropertyOwnerProfileDto? = null,
    @SerialName("organizationProfile") val organizationProfile: OrganizationProfileDto? = null,
)

/* ======================================================
   ACCOUNT DATA (Matches backend AccountDto)
====================================================== */

// Update AccountDataDto in your ProfileResponseDto file
@Serializable
data class AccountDataDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("accountCode") val accountCode: String,
    @SerialName("type") val type: String,  // "INDIVIDUAL", "ORGANIZATION"
    @SerialName("isBusiness") val isBusiness: Boolean = false,  // New field
    // Make these optional with default values since backend might not send them
    @SerialName("name") val name: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("isVerified") val isVerified: Boolean = false,
    @SerialName("verifiedFeatures") val verifiedFeatures: List<String> = emptyList(),
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
)

/* ======================================================
   USER DATA (Matches backend UserDto)
====================================================== */

@Serializable
data class UserDataDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("roleName") val roleName: String,  // Note: roleName, not role
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("status") val status: String
)

/* ======================================================
   INDIVIDUAL PROFILE (Matches backend IndividualProfileDto)
====================================================== */

@Serializable
data class IndividualProfileDto(
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("nationalId") val nationalId: String? = null
)

/* ======================================================
   ORGANIZATION PROFILE (Matches backend OrganizationProfileDto)
====================================================== */

@Serializable
data class OrganizationProfileDto(
    @SerialName("name") val name: String,
    @SerialName("type") val type: String? = null,
    @SerialName("registrationNo") val registrationNo: String? = null,
    @SerialName("kraPin") val kraPin: String? = null,
    @SerialName("officialEmail") val officialEmail: String? = null,
    @SerialName("officialPhone") val officialPhone: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("about") val about: String? = null,
    @SerialName("logo") val logo: String? = null,
    @SerialName("physicalAddress") val physicalAddress: String? = null,
    @SerialName("members") val members: List<TeamMemberDto> = emptyList(),
    @SerialName("pendingInvitations") val pendingInvitations: List<PendingInvitationDto> = emptyList()
)

@Serializable
data class TeamMemberDto(
    @SerialName("userUuid") val userUuid: String,
    @SerialName("userName") val userName: String,
    @SerialName("userEmail") val userEmail: String,
    @SerialName("userImage") val userImage: String? = null,
    @SerialName("roleName") val roleName: String
)

@Serializable
data class PendingInvitationDto(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("status") val status: String,
    @SerialName("expiresAt") val expiresAt: String
)

/* ======================================================
   JOB SEEKER PROFILE (Matches backend JobSeekerProfileDto)
====================================================== */

@Serializable
data class JobSeekerProfileDto(
    @SerialName("uuid") val uuid: String? = null,
    @SerialName("headline") val headline: String? = null,
    @SerialName("isActivelySeeking") val isActivelySeeking: Boolean = false,
    @SerialName("skills") val skills: List<String> = emptyList(),
    @SerialName("industries") val industries: List<String> = emptyList(),
    @SerialName("jobTypes") val jobTypes: List<String> = emptyList(),
    @SerialName("seniorityLevel") val seniorityLevel: String? = null,
    @SerialName("noticePeriod") val noticePeriod: String? = null,
    @SerialName("expectedSalary") val expectedSalary: Double? = null,
    @SerialName("workAuthorization") val workAuthorization: List<String> = emptyList(),
    @SerialName("cvUrl") val cvUrl: String? = null,
    @SerialName("cvLastUpdated") val cvLastUpdated: String? = null,
    @SerialName("portfolioImages") val portfolioImages: List<String> = emptyList(),
    @SerialName("linkedInUrl") val linkedInUrl: String? = null,
    @SerialName("githubUrl") val githubUrl: String? = null,
    @SerialName("portfolioUrl") val portfolioUrl: String? = null,
    @SerialName("averageRating") val averageRating: Float = 0f,
    @SerialName("totalReviews") val totalReviews: Int = 0,
    @SerialName("completedJobs") val completedJobs: Int = 0
)

/* ======================================================
   SKILLED PROFESSIONAL PROFILE (Matches backend SkilledProfessionalProfileDto)
====================================================== */

@Serializable
data class SkilledProfessionalProfileDto(
    @SerialName("uuid") val uuid: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("profession") val profession: String? = null,
    @SerialName("specialties") val specialties: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("yearsExperience") val yearsExperience: Int? = null,
    @SerialName("licenseNumber") val licenseNumber: String? = null,
    @SerialName("licenseBody") val licenseBody: String? = null,
    @SerialName("insuranceInfo") val insuranceInfo: String? = null,
    @SerialName("hourlyRate") val hourlyRate: Double? = null,
    @SerialName("dailyRate") val dailyRate: Double? = null,
    @SerialName("paymentTerms") val paymentTerms: String? = null,
    @SerialName("availableToday") val availableToday: Boolean = false,
    @SerialName("availableWeekends") val availableWeekends: Boolean = false,
    @SerialName("emergencyService") val emergencyService: Boolean = false,
    @SerialName("portfolioImages") val portfolioImages: List<String> = emptyList(),
    @SerialName("certifications") val certifications: List<String> = emptyList(),
    @SerialName("isVerified") val isVerified: Boolean = false,
    @SerialName("averageRating") val averageRating: Float = 0f,
    @SerialName("totalReviews") val totalReviews: Int = 0,
    @SerialName("completedJobs") val completedJobs: Int = 0
)

/* ======================================================
   INTERMEDIARY AGENT PROFILE (Matches backend IntermediaryAgentProfileDto)
====================================================== */

@Serializable
data class IntermediaryAgentProfileDto(
    @SerialName("uuid") val uuid: String? = null,
    @SerialName("agentType") val agentType: String,  // HOUSING_AGENT, RECRUITMENT_AGENT, etc.
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
    @SerialName("clientTypes") val clientTypes: List<String> = emptyList(),
    @SerialName("isVerified") val isVerified: Boolean = false,
    @SerialName("averageRating") val averageRating: Float = 0f,
    @SerialName("totalReviews") val totalReviews: Int = 0,
    @SerialName("completedDeals") val completedDeals: Int = 0
)

/* ======================================================
   HOUSING SEEKER PROFILE (Matches backend HousingSeekerProfileDto)
====================================================== */

@Serializable
data class HousingSeekerProfileDto(
    @SerialName("uuid") val uuid: String? = null,
    // Search Type
    @SerialName("searchType") val searchType: String? = null,  // "RENT", "BUY", "BOTH"
    @SerialName("isLookingForRental") val isLookingForRental: Boolean = false,
    @SerialName("isLookingToBuy") val isLookingToBuy: Boolean = false,
    // Rental preferences
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
    @SerialName("minimumLeaseTerm") val minimumLeaseTerm: Int? = null,
    @SerialName("maximumLeaseTerm") val maximumLeaseTerm: Int? = null,
    @SerialName("depositAmount") val depositAmount: Double? = null,
    @SerialName("isPetFriendly") val isPetFriendly: Boolean = false,
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean = false,
    @SerialName("utilitiesDetails") val utilitiesDetails: String? = null,
    // Sale preferences
    @SerialName("isNegotiable") val isNegotiable: Boolean = true,
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean = false,
    // Location
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("searchRadiusKm") val searchRadiusKm: Int? = null,
    @SerialName("hasAgent") val hasAgent: Boolean = false,
    @SerialName("agentUuid") val agentUuid: String? = null
)

/* ======================================================
   SUPPORT BENEFICIARY PROFILE (Matches backend SupportBeneficiaryProfileDto)
====================================================== */

@Serializable
data class SupportBeneficiaryProfileDto(
    @SerialName("uuid") val uuid: String? = null,
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
    @SerialName("caseWorkerUuid") val caseWorkerUuid: String? = null,
    @SerialName("assignedNgo") val assignedNgo: String? = null,
    @SerialName("caseStatus") val caseStatus: String? = null  // "OPEN", "IN_PROGRESS", "CLOSED"
)

/* ======================================================
   EMPLOYER PROFILE (Matches backend EmployerProfileDto)
====================================================== */

@Serializable
data class EmployerProfileDto(
    @SerialName("uuid") val uuid: String? = null,
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
    @SerialName("isVerifiedEmployer") val isVerifiedEmployer: Boolean = false,
    @SerialName("averageRating") val averageRating: Float = 0f,
    @SerialName("totalReviews") val totalReviews: Int = 0,
    @SerialName("activeJobs") val activeJobs: Int = 0,
    @SerialName("totalHires") val totalHires: Int = 0
)

/* ======================================================
   PROPERTY OWNER PROFILE (Matches backend PropertyOwnerProfileDto)
====================================================== */

@Serializable
data class PropertyOwnerProfileDto(
    @SerialName("uuid") val uuid: String? = null,
    // Listing Type
    @SerialName("listingType") val listingType: String? = null,  // "RENT", "SALE", "BOTH"
    @SerialName("isListingForRent") val isListingForRent: Boolean = false,
    @SerialName("isListingForSale") val isListingForSale: Boolean = false,
    // Professional details
    @SerialName("isProfessional") val isProfessional: Boolean = false,
    @SerialName("licenseNumber") val licenseNumber: String? = null,
    @SerialName("companyName") val companyName: String? = null,
    @SerialName("yearsInBusiness") val yearsInBusiness: Int? = null,
    // Property details
    @SerialName("propertyCount") val propertyCount: Int? = null,
    @SerialName("propertyTypes") val propertyTypes: List<String> = emptyList(),
    @SerialName("preferredPropertyTypes") val preferredPropertyTypes: List<String> = emptyList(),
    @SerialName("serviceAreas") val serviceAreas: List<String> = emptyList(),
    @SerialName("propertyPurpose") val propertyPurpose: String? = null,  // "PRIMARY", "INVESTMENT", "BOTH"
    @SerialName("usesAgent") val usesAgent: Boolean = false,
    @SerialName("managingAgentUuid") val managingAgentUuid: String? = null,
    @SerialName("isVerifiedOwner") val isVerifiedOwner: Boolean = false,
    @SerialName("averageRating") val averageRating: Float = 0f,
    @SerialName("totalReviews") val totalReviews: Int = 0,
    @SerialName("activeListings") val activeListings: Int = 0,
    @SerialName("totalRentals") val totalRentals: Int = 0,
    @SerialName("totalSales") val totalSales: Int = 0
)

/* ======================================================
   VERIFICATION ITEM (Matches backend VerificationItemDto)
====================================================== */

@Serializable
data class VerificationItemDto(
    @SerialName("type") val type: String,  // "IDENTITY", "BUSINESS", "PROFESSIONAL_LICENSE", "AGENT_LICENSE", "NGO_REGISTRATION"
    @SerialName("status") val status: String,  // "PENDING", "APPROVED", "REJECTED", "EXPIRED"
    @SerialName("documentUrl") val documentUrl: String? = null,
    @SerialName("rejectionReason") val rejectionReason: String? = null,
    @SerialName("verifiedAt") val verifiedAt: String? = null,
    @SerialName("expiresAt") val expiresAt: String? = null
)

/* ======================================================
   PROFILE COMPLETION (Matches backend ProfileCompletionDto)
====================================================== */

@Serializable
data class ProfileCompletionDto(
    @SerialName("percentage") val percentage: Int,
    @SerialName("isComplete") val isComplete: Boolean
)

/* ======================================================
   REVIEWS (Matches backend ReviewDto)
====================================================== */

@Serializable
data class ReviewDto(
    @SerialName("id") val id: String,
    @SerialName("reviewerName") val reviewerName: String,
    @SerialName("reviewerImage") val reviewerImage: String? = null,
    @SerialName("rating") val rating: Float,
    @SerialName("comment") val comment: String,
    @SerialName("date") val date: String
)

typealias ReviewsResponseDto = BaseResponseDto<List<ReviewDto>>