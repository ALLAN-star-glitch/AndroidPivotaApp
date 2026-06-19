package com.example.pivota.dashboard.domain.model.listings_models.jobs

// ======================================================
// JOB POST MODEL
// ======================================================

data class JobPost(
    val id: String,
    val title: String,
    val description: String,
    // Job Characteristics
    val employmentType: String,
    val paymentType: String,
    val workArrangement: String,
    val commitment: String,
    val workSchedule: String,
    val documentationLevel: String,
    val skillLevel: String,
    val experienceLevel: String,
    val educationLevel: String,
    // Location
    val locationCity: String,
    val locationNeighborhood: String? = null,
    val isRemote: Boolean = false,
    // Compensation
    val payAmount: Double? = null,
    val payRate: String? = null,
    val isNegotiable: Boolean = false,
    // Requirements
    val skills: List<String> = emptyList(),
    val requiresDocuments: Boolean = false,
    val documentsNeeded: List<String> = emptyList(),
    val requiresEquipment: Boolean = false,
    val equipmentRequired: List<String> = emptyList(),
    val additionalNotes: String? = null,
    // Referrals
    val allowReferrals: Boolean = true,
    val referralBonus: Double? = null,
    // Timeline
    val applicationDeadline: String? = null,
    val startDate: String? = null,
    val startDateFlexible: Boolean = false,
    val maxApplications: Int? = null,
    // Privacy & Visibility
    val isAnonymous: Boolean = false,
    val displayName: String? = null,
    val contactEmail: String? = null,
    // Work Details
    val hoursPerWeek: Int? = null,
    val contractDuration: Int? = null,
    // Analytics
    val viewCount: Int = 0,
    val shareCount: Int = 0,
    // Status & Timestamps
    val status: String,
    val applicationsCount: Int = 0,
    val createdAt: String,
    val updatedAt: String,
    // Relations
    val category: CategoryBasic? = null,
    val subCategory: CategoryBasic? = null,
    val creator: UserBasic,
    val account: AccountBasic
)

data class CategoryBasic(
    val id: String,
    val name: String
)

data class UserBasic(
    val id: String,
    val fullName: String,
    val email: String? = null
)

data class AccountBasic(
    val id: String,
    val name: String
)

// ======================================================
// RESPONSE WRAPPER
// ======================================================

data class JobPostsResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: List<JobPost> = emptyList(),
    val pagination: PaginationInfo? = null,
    val error: ErrorPayload? = null
)

data class JobPostResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: JobPost? = null,
    val error: ErrorPayload? = null
)

data class PaginationInfo(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)

data class ErrorPayload(
    val code: String,
    val message: String,
    val details: String? = null
)

// ======================================================
// REQUEST PARAMETERS (Matches backend DTOs)
// ======================================================

// GET /jobs-module/jobs
data class GetAllJobsParams(
    val limit: Int = 20,
    val offset: Int = 0,
    val city: String? = null,
    val minPay: Double? = null,
    val maxPay: Double? = null,
    val sortBy: String = "recent",
    // Job Characteristics Filters
    val employmentType: String? = null,
    val paymentType: String? = null,
    val workArrangement: String? = null,
    val commitment: String? = null,
    val workSchedule: String? = null,
    val documentationLevel: String? = null,
    val skillLevel: String? = null,
    val experienceLevel: String? = null,
    val educationLevel: String? = null,
    // New Filters
    val isAnonymous: Boolean? = null,
    val isRemote: Boolean? = null,
    val applicationDeadlineBefore: String? = null,
    val applicationDeadlineAfter: String? = null,
    val startDateBefore: String? = null,
    val startDateAfter: String? = null,
    val hoursPerWeekMin: Int? = null,
    val hoursPerWeekMax: Int? = null,
    // Cache Control
    val bypassCache: Boolean = false,
    val skipCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 300
)

// GET /jobs-module/jobs/category
data class GetJobsByCategoryParams(
    val categoryId: String,
    val limit: Int = 20,
    val offset: Int = 0,
    val city: String? = null,
    val minPay: Double? = null,
    val maxPay: Double? = null,
    val sortBy: String = "recent",
    // Job Characteristics Filters
    val employmentType: String? = null,
    val paymentType: String? = null,
    val workArrangement: String? = null,
    val commitment: String? = null,
    val workSchedule: String? = null,
    val documentationLevel: String? = null,
    val skillLevel: String? = null,
    val experienceLevel: String? = null,
    val educationLevel: String? = null,
    // New Filters
    val isAnonymous: Boolean? = null,
    val isRemote: Boolean? = null,
    val applicationDeadlineBefore: String? = null,
    val applicationDeadlineAfter: String? = null,
    val startDateBefore: String? = null,
    val startDateAfter: String? = null,
    val hoursPerWeekMin: Int? = null,
    val hoursPerWeekMax: Int? = null,
    // Cache Control
    val bypassCache: Boolean = false,
    val skipCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 300
)

// GET /jobs-module/details/:id
data class GetJobByIdParams(
    val id: String,
    val bypassCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 600
)

// GET /jobs-module/jobs/owner/:accountId
data class GetJobListingsByOwnerParams(
    val accountId: String,
    val status: String? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: String = "recent",
    val bypassCache: Boolean = false,
    val skipCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 300
)

// GET /jobs-module/my-listings
data class GetOwnJobsParams(
    val status: String? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: String = "recent",
    // Job Characteristics Filters
    val employmentType: String? = null,
    val paymentType: String? = null,
    val workArrangement: String? = null,
    val commitment: String? = null,
    val experienceLevel: String? = null,
    val educationLevel: String? = null,
    // New Filters
    val isAnonymous: Boolean? = null,
    val hoursPerWeekMin: Int? = null,
    val hoursPerWeekMax: Int? = null
)

// POST /jobs-module/jobs
data class CreateJobPostParams(
    // Core fields
    val title: String,
    val description: String,
    val categoryId: String,
    val subCategoryId: String? = null,
    // Job Characteristics
    val employmentType: String,
    val paymentType: String,
    val workArrangement: String,
    val commitment: String,
    val workSchedule: String,
    val documentationLevel: String,
    val skillLevel: String,
    val experienceLevel: String,
    val educationLevel: String,
    // Location
    val locationCity: String,
    val locationNeighborhood: String? = null,
    val isRemote: Boolean = false,
    // Compensation
    val payAmount: Double? = null,
    val payRate: String? = null,
    val isNegotiable: Boolean = false,
    // Requirements
    val skills: List<String> = emptyList(),
    val requiresDocuments: Boolean = false,
    val requiresEquipment: Boolean = false,
    val documentsNeeded: List<String> = emptyList(),
    val equipmentRequired: List<String> = emptyList(),
    val additionalNotes: String? = null,
    // Referrals
    val allowReferrals: Boolean = true,
    val referralBonus: Double? = null,
    // Timeline
    val applicationDeadline: String? = null,
    val startDate: String? = null,
    val startDateFlexible: Boolean = false,
    val maxApplications: Int? = null,
    // Privacy & Visibility
    val isAnonymous: Boolean = false,
    val displayName: String? = null,
    val contactEmail: String? = null,
    // Work Details
    val hoursPerWeek: Int? = null,
    val contractDuration: Int? = null,
    // Status
    val status: String = "ACTIVE"
)

// PATCH /jobs-module/jobs/:id
data class UpdateJobPostParams(
    val title: String? = null,
    val description: String? = null,
    val categoryId: String? = null,
    val subCategoryId: String? = null,
    val employmentType: String? = null,
    val paymentType: String? = null,
    val workArrangement: String? = null,
    val commitment: String? = null,
    val workSchedule: String? = null,
    val documentationLevel: String? = null,
    val skillLevel: String? = null,
    val experienceLevel: String? = null,
    val educationLevel: String? = null,
    val locationCity: String? = null,
    val locationNeighborhood: String? = null,
    val isRemote: Boolean? = null,
    val payAmount: Double? = null,
    val payRate: String? = null,
    val isNegotiable: Boolean? = null,
    val skills: List<String>? = null,
    val requiresDocuments: Boolean? = null,
    val requiresEquipment: Boolean? = null,
    val documentsNeeded: List<String>? = null,
    val equipmentRequired: List<String>? = null,
    val additionalNotes: String? = null,
    val allowReferrals: Boolean? = null,
    val referralBonus: Double? = null,
    val applicationDeadline: String? = null,
    val startDate: String? = null,
    val startDateFlexible: Boolean? = null,
    val maxApplications: Int? = null,
    val isAnonymous: Boolean? = null,
    val displayName: String? = null,
    val contactEmail: String? = null,
    val hoursPerWeek: Int? = null,
    val contractDuration: Int? = null,
    val status: String? = null
)

// PATCH /jobs-module/jobs/:id/close
data class CloseJobPostParams(
    val jobId: String
)

// ======================================================
// CREATE JOB POST RESPONSE
// ======================================================

data class CreateJobPostResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: JobPostCreateData? = null,
    val error: ErrorPayload? = null
)

data class JobPostCreateData(
    val id: String,
    val status: String,
    val createdAt: String
)

// PATCH /jobs-module/jobs/:id/close RESPONSE
data class CloseJobPostResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: CloseJobPostData? = null,
    val error: ErrorPayload? = null
)

data class CloseJobPostData(
    val id: String,
    val status: String,
    val isClosed: Boolean,
    val closedAt: String
)

// ======================================================
// HELPER EXTENSIONS
// ======================================================

fun JobPost.getFormattedPay(): String {
    return when {
        payAmount == null -> "Not specified"
        payRate != null -> {
            val rateLabel = when (payRate) {
                "PER_HOUR" -> "per hour"
                "PER_DAY" -> "per day"
                "PER_WEEK" -> "per week"
                "PER_MONTH" -> "per month"
                "PER_PROJECT" -> "per project"
                "FIXED" -> "fixed"
                else -> payRate
            }
            "${payAmount.toInt()} KES $rateLabel"
        }
        else -> "${payAmount.toInt()} KES"
    }
}

fun JobPost.getFormattedLocation(): String {
    return when {
        isRemote -> "Remote"
        locationNeighborhood != null -> "$locationCity, $locationNeighborhood"
        else -> locationCity
    }
}

fun JobPost.getEmploymentTypeLabel(): String {
    return when (employmentType) {
        "PERMANENT" -> "Permanent"
        "CONTRACT" -> "Contract"
        "CASUAL" -> "Casual"
        "GIG" -> "Gig"
        "FREELANCE" -> "Freelance"
        "INTERNSHIP" -> "Internship"
        "APPRENTICESHIP" -> "Apprenticeship"
        "VOLUNTEER" -> "Volunteer"
        else -> employmentType
    }
}

fun JobPost.getWorkArrangementLabel(): String {
    return when (workArrangement) {
        "ONSITE" -> "On-site"
        "REMOTE" -> "Remote"
        "HYBRID" -> "Hybrid"
        "FIELD" -> "Field"
        "SHIFT" -> "Shift"
        "FLEXIBLE" -> "Flexible"
        else -> workArrangement
    }
}

fun JobPost.getExperienceLevelLabel(): String {
    return when (experienceLevel) {
        "ENTRY" -> "Entry Level"
        "JUNIOR" -> "Junior"
        "MID_LEVEL" -> "Mid Level"
        "SENIOR" -> "Senior"
        "LEAD" -> "Lead"
        "PRINCIPAL" -> "Principal"
        else -> experienceLevel
    }
}

fun JobPost.getCommitmentLabel(): String {
    return when (commitment) {
        "FULL_TIME" -> "Full Time"
        "PART_TIME" -> "Part Time"
        "PROJECT_BASED" -> "Project Based"
        "ON_CALL" -> "On Call"
        else -> commitment
    }
}

fun JobPost.getEducationLevelLabel(): String {
    return when (educationLevel) {
        "NONE" -> "None"
        "CERTIFICATE" -> "Certificate"
        "DIPLOMA" -> "Diploma"
        "BACHELORS" -> "Bachelor's Degree"
        "MASTERS" -> "Master's Degree"
        "PHD" -> "PhD"
        else -> educationLevel
    }
}

fun JobPost.getPaymentTypeLabel(): String {
    return when (paymentType) {
        "SALARY" -> "Salary"
        "WAGE" -> "Wage"
        "PER_TASK" -> "Per Task"
        "COMMISSION" -> "Commission"
        "PROJECT_BASED" -> "Project Based"
        "STIPEND" -> "Stipend"
        "IN_KIND" -> "In Kind"
        else -> paymentType
    }
}

// ======================================================
// SEALED CLASS FOR UI STATE
// ======================================================

sealed class JobsLoadingState {
    object Idle : JobsLoadingState()
    object Loading : JobsLoadingState()
    data class Success(
        val jobs: List<JobPost>,
        val hasMore: Boolean,
        val totalCount: Int
    ) : JobsLoadingState()
    data class Error(val message: String) : JobsLoadingState()
}