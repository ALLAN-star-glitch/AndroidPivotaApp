package com.example.pivota.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ======================================================
// JOB POST DTOS
// ======================================================

@Serializable
data class JobPostsResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: List<JobPostDto>? = null,
    @SerialName("pagination") val pagination: PaginationInfoDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class JobPostDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    // Job Characteristics
    @SerialName("employmentType") val employmentType: String,
    @SerialName("paymentType") val paymentType: String,
    @SerialName("workArrangement") val workArrangement: String,
    @SerialName("commitment") val commitment: String,
    @SerialName("workSchedule") val workSchedule: String,
    @SerialName("documentationLevel") val documentationLevel: String,
    @SerialName("skillLevel") val skillLevel: String,
    @SerialName("experienceLevel") val experienceLevel: String,
    @SerialName("educationLevel") val educationLevel: String,
    // Location
    @SerialName("locationCity") val locationCity: String,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("isRemote") val isRemote: Boolean = false,
    // Compensation
    @SerialName("payAmount") val payAmount: Double? = null,
    @SerialName("payRate") val payRate: String? = null,
    @SerialName("isNegotiable") val isNegotiable: Boolean = false,
    // Requirements
    @SerialName("skills") val skills: List<String> = emptyList(),
    @SerialName("requiresDocuments") val requiresDocuments: Boolean = false,
    @SerialName("documentsNeeded") val documentsNeeded: List<String> = emptyList(),
    @SerialName("requiresEquipment") val requiresEquipment: Boolean = false,
    @SerialName("equipmentRequired") val equipmentRequired: List<String> = emptyList(),
    @SerialName("additionalNotes") val additionalNotes: String? = null,
    // Referrals
    @SerialName("allowReferrals") val allowReferrals: Boolean = true,
    @SerialName("referralBonus") val referralBonus: Double? = null,
    // Timeline
    @SerialName("applicationDeadline") val applicationDeadline: String? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("startDateFlexible") val startDateFlexible: Boolean = false,
    @SerialName("maxApplications") val maxApplications: Int? = null,
    // Privacy & Visibility
    @SerialName("isAnonymous") val isAnonymous: Boolean = false,
    @SerialName("displayName") val displayName: String? = null,
    @SerialName("contactEmail") val contactEmail: String? = null,
    // Work Details
    @SerialName("hoursPerWeek") val hoursPerWeek: Int? = null,
    @SerialName("contractDuration") val contractDuration: Int? = null,
    // Analytics
    @SerialName("viewCount") val viewCount: Int = 0,
    @SerialName("shareCount") val shareCount: Int = 0,
    // Status & Timestamps
    @SerialName("status") val status: String,
    @SerialName("applicationsCount") val applicationsCount: Int = 0,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    // Relations
    @SerialName("category") val category: CategoryBasicDto? = null,
    @SerialName("subCategory") val subCategory: CategoryBasicDto? = null,
    @SerialName("creator") val creator: UserBasicDto,
    @SerialName("account") val account: AccountBasicDto
)

@Serializable
data class CategoryBasicDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String
)

@Serializable
data class UserBasicDto(
    @SerialName("id") val id: String,
    @SerialName("fullName") val fullName: String,
    @SerialName("email") val email: String? = null
)

@Serializable
data class AccountBasicDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String
)

// ======================================================
// SINGLE JOB POST RESPONSE DTO
// ======================================================

@Serializable
data class JobPostResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: JobPostDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

// ======================================================
// JOB POST REQUEST DTOS
// ======================================================

@Serializable
data class GetAllJobsRequestDto(
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    @SerialName("city") val city: String? = null,
    @SerialName("minPay") val minPay: Double? = null,
    @SerialName("maxPay") val maxPay: Double? = null,
    @SerialName("sortBy") val sortBy: String = "recent",
    // Job Characteristics Filters
    @SerialName("employmentType") val employmentType: String? = null,
    @SerialName("paymentType") val paymentType: String? = null,
    @SerialName("workArrangement") val workArrangement: String? = null,
    @SerialName("commitment") val commitment: String? = null,
    @SerialName("workSchedule") val workSchedule: String? = null,
    @SerialName("documentationLevel") val documentationLevel: String? = null,
    @SerialName("skillLevel") val skillLevel: String? = null,
    @SerialName("experienceLevel") val experienceLevel: String? = null,
    @SerialName("educationLevel") val educationLevel: String? = null,
    // New Filters
    @SerialName("isAnonymous") val isAnonymous: Boolean? = null,
    @SerialName("isRemote") val isRemote: Boolean? = null,
    @SerialName("applicationDeadlineAfter") val applicationDeadlineAfter: String? = null,
    @SerialName("applicationDeadlineBefore") val applicationDeadlineBefore: String? = null,
    @SerialName("startDateAfter") val startDateAfter: String? = null,
    @SerialName("startDateBefore") val startDateBefore: String? = null,
    @SerialName("hoursPerWeekMin") val hoursPerWeekMin: Int? = null,
    @SerialName("hoursPerWeekMax") val hoursPerWeekMax: Int? = null,
    // Cache Control
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("skipCache") val skipCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 300,
    @SerialName("readOnly") val readOnly: Boolean = false
)

@Serializable
data class GetJobsByCategoryRequestDto(
    @SerialName("categoryId") val categoryId: String,
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    @SerialName("city") val city: String? = null,
    @SerialName("minPay") val minPay: Double? = null,
    @SerialName("maxPay") val maxPay: Double? = null,
    @SerialName("sortBy") val sortBy: String = "recent",
    // Job Characteristics Filters
    @SerialName("employmentType") val employmentType: String? = null,
    @SerialName("paymentType") val paymentType: String? = null,
    @SerialName("workArrangement") val workArrangement: String? = null,
    @SerialName("commitment") val commitment: String? = null,
    @SerialName("workSchedule") val workSchedule: String? = null,
    @SerialName("documentationLevel") val documentationLevel: String? = null,
    @SerialName("skillLevel") val skillLevel: String? = null,
    @SerialName("experienceLevel") val experienceLevel: String? = null,
    @SerialName("educationLevel") val educationLevel: String? = null,
    // New Filters
    @SerialName("isAnonymous") val isAnonymous: Boolean? = null,
    @SerialName("isRemote") val isRemote: Boolean? = null,
    @SerialName("applicationDeadlineAfter") val applicationDeadlineAfter: String? = null,
    @SerialName("applicationDeadlineBefore") val applicationDeadlineBefore: String? = null,
    @SerialName("startDateAfter") val startDateAfter: String? = null,
    @SerialName("startDateBefore") val startDateBefore: String? = null,
    @SerialName("hoursPerWeekMin") val hoursPerWeekMin: Int? = null,
    @SerialName("hoursPerWeekMax") val hoursPerWeekMax: Int? = null,
    // Cache Control
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("skipCache") val skipCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 300,
    @SerialName("readOnly") val readOnly: Boolean = false
)

@Serializable
data class GetJobByIdRequestDto(
    @SerialName("id") val id: String,
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 600,
    @SerialName("readOnly") val readOnly: Boolean = false
)

@Serializable
data class GetJobListingsByOwnerRequestDto(
    @SerialName("accountId") val accountId: String,
    @SerialName("status") val status: String? = null,
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    @SerialName("sortBy") val sortBy: String = "recent",
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("skipCache") val skipCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 300,
    @SerialName("readOnly") val readOnly: Boolean = false
)

// ======================================================
// CREATE JOB POST REQUEST/ RESPONSE DTOs
// ======================================================

@Serializable
data class CreateJobPostRequestDto(
    // Core fields
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("categoryId") val categoryId: String,
    @SerialName("subCategoryId") val subCategoryId: String? = null,
    // Job Characteristics
    @SerialName("employmentType") val employmentType: String,
    @SerialName("paymentType") val paymentType: String,
    @SerialName("workArrangement") val workArrangement: String,
    @SerialName("commitment") val commitment: String,
    @SerialName("workSchedule") val workSchedule: String,
    @SerialName("documentationLevel") val documentationLevel: String,
    @SerialName("skillLevel") val skillLevel: String,
    @SerialName("experienceLevel") val experienceLevel: String,
    @SerialName("educationLevel") val educationLevel: String,
    // Location
    @SerialName("locationCity") val locationCity: String,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("isRemote") val isRemote: Boolean = false,
    // Compensation
    @SerialName("payAmount") val payAmount: Double? = null,
    @SerialName("payRate") val payRate: String? = null,
    @SerialName("isNegotiable") val isNegotiable: Boolean = false,
    // Requirements
    @SerialName("skills") val skills: List<String> = emptyList(),
    @SerialName("requiresDocuments") val requiresDocuments: Boolean = false,
    @SerialName("requiresEquipment") val requiresEquipment: Boolean = false,
    @SerialName("documentsNeeded") val documentsNeeded: List<String> = emptyList(),
    @SerialName("equipmentRequired") val equipmentRequired: List<String> = emptyList(),
    @SerialName("additionalNotes") val additionalNotes: String? = null,
    // Referrals
    @SerialName("allowReferrals") val allowReferrals: Boolean = true,
    @SerialName("referralBonus") val referralBonus: Double? = null,
    // Timeline
    @SerialName("applicationDeadline") val applicationDeadline: String? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("startDateFlexible") val startDateFlexible: Boolean = false,
    @SerialName("maxApplications") val maxApplications: Int? = null,
    // Privacy & Visibility
    @SerialName("isAnonymous") val isAnonymous: Boolean = false,
    @SerialName("displayName") val displayName: String? = null,
    @SerialName("contactEmail") val contactEmail: String? = null,
    // Work Details
    @SerialName("hoursPerWeek") val hoursPerWeek: Int? = null,
    @SerialName("contractDuration") val contractDuration: Int? = null,
    // Status
    @SerialName("status") val status: String = "ACTIVE"
)

@Serializable
data class CreateJobPostResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: JobPostCreateDataDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class JobPostCreateDataDto(
    @SerialName("id") val id: String,
    @SerialName("status") val status: String,
    @SerialName("createdAt") val createdAt: String
)

// ======================================================
// CLOSE JOB POST RESPONSE DTO
// ======================================================

@Serializable
data class CloseJobPostResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: CloseJobPostDataDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class CloseJobPostDataDto(
    @SerialName("id") val id: String,
    @SerialName("status") val status: String,
    @SerialName("isClosed") val isClosed: Boolean,
    @SerialName("closedAt") val closedAt: String
)

// ======================================================
// UPDATE JOB POST REQUEST DTO
// ======================================================

@Serializable
data class UpdateJobPostRequestDto(
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("categoryId") val categoryId: String? = null,
    @SerialName("subCategoryId") val subCategoryId: String? = null,
    // Job Characteristics
    @SerialName("employmentType") val employmentType: String? = null,
    @SerialName("paymentType") val paymentType: String? = null,
    @SerialName("workArrangement") val workArrangement: String? = null,
    @SerialName("commitment") val commitment: String? = null,
    @SerialName("workSchedule") val workSchedule: String? = null,
    @SerialName("documentationLevel") val documentationLevel: String? = null,
    @SerialName("skillLevel") val skillLevel: String? = null,
    @SerialName("experienceLevel") val experienceLevel: String? = null,
    @SerialName("educationLevel") val educationLevel: String? = null,
    // Location
    @SerialName("locationCity") val locationCity: String? = null,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("isRemote") val isRemote: Boolean? = null,
    // Compensation
    @SerialName("payAmount") val payAmount: Double? = null,
    @SerialName("payRate") val payRate: String? = null,
    @SerialName("isNegotiable") val isNegotiable: Boolean? = null,
    // Requirements
    @SerialName("skills") val skills: List<String>? = null,
    @SerialName("requiresDocuments") val requiresDocuments: Boolean? = null,
    @SerialName("requiresEquipment") val requiresEquipment: Boolean? = null,
    @SerialName("documentsNeeded") val documentsNeeded: List<String>? = null,
    @SerialName("equipmentRequired") val equipmentRequired: List<String>? = null,
    @SerialName("additionalNotes") val additionalNotes: String? = null,
    // Referrals
    @SerialName("allowReferrals") val allowReferrals: Boolean? = null,
    @SerialName("referralBonus") val referralBonus: Double? = null,
    // Timeline
    @SerialName("applicationDeadline") val applicationDeadline: String? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("startDateFlexible") val startDateFlexible: Boolean? = null,
    @SerialName("maxApplications") val maxApplications: Int? = null,
    // Privacy & Visibility
    @SerialName("isAnonymous") val isAnonymous: Boolean? = null,
    @SerialName("displayName") val displayName: String? = null,
    @SerialName("contactEmail") val contactEmail: String? = null,
    // Work Details
    @SerialName("hoursPerWeek") val hoursPerWeek: Int? = null,
    @SerialName("contractDuration") val contractDuration: Int? = null,
    // Status
    @SerialName("status") val status: String? = null
)



