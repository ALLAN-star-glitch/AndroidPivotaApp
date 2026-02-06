package com.example.pivota.listings.domain.models


/**
 * The core Domain Model for a Job Posting in PivotaConnect.
 * Aligned with Prisma Backend for MVP1.
 */
data class JobPost(
    val uuid: String = "",
    val creatorUuid: String, // The User who posted it
    val orgUuid: String? = null, // Linked if AccountType is Organization

    // Core Details
    val title: String,
    val description: String,
    val jobType: JobType, // INFORMAL or FORMAL
    val categoryId: String,
    val subCategoryId: String? = null,

    // Location
    val locationCity: String,
    val locationNeighborhood: String,
    val isRemote: Boolean = false,

    // Compensation
    val payAmount: Double,
    val payRate: PayRate, // Daily, Weekly, Monthly, PerTask
    val isNegotiable: Boolean = false,

    // Requirements (Forward-Compatible)
    val employmentType: EmploymentType? = null, // Mostly for FORMAL
    val experienceLevel: String? = null,
    val educationLevel: String? = null,
    val skills: List<String> = emptyList(),

    // Equipment & Documents
    val requiresEquipment: Boolean = false,
    val equipmentRequired: List<String> = emptyList(),
    val requiresDocuments: Boolean = false,
    val documentsNeeded: List<DocumentType> = emptyList(),

    // Future-Ready Benefits (Stored in additionalNotes for MVP1)
    val benefits: List<String> = emptyList(),
    val additionalNotes: String? = null,

    val status: JobStatus = JobStatus.DRAFT,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PayRate {
    DAILY, WEEKLY, MONTHLY, PER_TASK
}

enum class EmploymentType {
    FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
}

enum class DocumentType {
    CV_RESUME, COVER_LETTER, NATIONAL_ID, CERTIFICATE, LICENSE, PORTFOLIO, OTHER
}

enum class JobStatus {
    DRAFT, ACTIVE, FILLED, EXPIRED, DELETED
}