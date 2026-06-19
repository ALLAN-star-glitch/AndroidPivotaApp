package com.example.pivota.dashboard.data.mapper

import com.example.pivota.dashboard.data.dto.*
import com.example.pivota.dashboard.domain.model.listings_models.jobs.*

import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class JobPostMapper @Inject constructor() {

    // ======================================================
    // RESPONSE MAPPERS
    // ======================================================

    fun toJobPostsResponse(dto: JobPostsResponseDto): JobPostsResponse {
        return JobPostsResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.map { toJobPost(it) } ?: emptyList(),
            pagination = dto.pagination?.let { toPaginationInfo(it) },
            error = dto.error?.let { toErrorPayload(it) }
        )
    }

    fun toJobPostResponse(dto: JobPostResponseDto): JobPostResponse {
        return JobPostResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.let { toJobPost(it) },
            error = dto.error?.let { toErrorPayload(it) }
        )
    }

    fun toCreateJobPostResponse(dto: CreateJobPostResponseDto): CreateJobPostResponse {
        return CreateJobPostResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.let { toJobPostCreateData(it) },
            error = dto.error?.let { toErrorPayload(it) }
        )
    }

    fun toCloseJobPostResponse(dto: CloseJobPostResponseDto): CloseJobPostResponse {
        return CloseJobPostResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.let { toCloseJobPostData(it) },
            error = dto.error?.let { toErrorPayload(it) }
        )
    }

    // ======================================================
    // DTO TO DOMAIN MAPPERS
    // ======================================================

    fun toJobPost(dto: JobPostDto): JobPost {
        return JobPost(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            // Job Characteristics
            employmentType = dto.employmentType,
            paymentType = dto.paymentType,
            workArrangement = dto.workArrangement,
            commitment = dto.commitment,
            workSchedule = dto.workSchedule,
            documentationLevel = dto.documentationLevel,
            skillLevel = dto.skillLevel,
            experienceLevel = dto.experienceLevel,
            educationLevel = dto.educationLevel,
            // Location
            locationCity = dto.locationCity,
            locationNeighborhood = dto.locationNeighborhood,
            isRemote = dto.isRemote,
            // Compensation
            payAmount = dto.payAmount,
            payRate = dto.payRate,
            isNegotiable = dto.isNegotiable,
            // Requirements
            skills = dto.skills,
            requiresDocuments = dto.requiresDocuments,
            documentsNeeded = dto.documentsNeeded,
            requiresEquipment = dto.requiresEquipment,
            equipmentRequired = dto.equipmentRequired,
            additionalNotes = dto.additionalNotes,
            // Referrals
            allowReferrals = dto.allowReferrals,
            referralBonus = dto.referralBonus,
            // Timeline
            applicationDeadline = dto.applicationDeadline,
            startDate = dto.startDate,
            startDateFlexible = dto.startDateFlexible,
            maxApplications = dto.maxApplications,
            // Privacy & Visibility
            isAnonymous = dto.isAnonymous,
            displayName = dto.displayName,
            contactEmail = dto.contactEmail,
            // Work Details
            hoursPerWeek = dto.hoursPerWeek,
            contractDuration = dto.contractDuration,
            // Analytics
            viewCount = dto.viewCount,
            shareCount = dto.shareCount,
            // Status & Timestamps
            status = dto.status,
            applicationsCount = dto.applicationsCount,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            // Relations
            category = dto.category?.let { toCategoryBasic(it) },
            subCategory = dto.subCategory?.let { toCategoryBasic(it) },
            creator = toUserBasic(dto.creator),
            account = toAccountBasic(dto.account)
        )
    }

    fun toCategoryBasic(dto: CategoryBasicDto): CategoryBasic {
        return CategoryBasic(
            id = dto.id,
            name = dto.name
        )
    }

    fun toUserBasic(dto: UserBasicDto): UserBasic {
        return UserBasic(
            id = dto.id,
            fullName = dto.fullName,
            email = dto.email
        )
    }

    fun toAccountBasic(dto: AccountBasicDto): AccountBasic {
        return AccountBasic(
            id = dto.id,
            name = dto.name
        )
    }

    fun toPaginationInfo(dto: PaginationInfoDto): PaginationInfo {
        return PaginationInfo(
            total = dto.total,
            limit = dto.limit,
            offset = dto.offset,
            hasMore = dto.hasMore
        )
    }

    fun toErrorPayload(dto: ErrorPayloadDto): ErrorPayload {
        return ErrorPayload(
            code = dto.code?.toString() ?: "UNKNOWN_ERROR",
            message = dto.message,
            details = null
        )
    }

    fun toJobPostCreateData(dto: JobPostCreateDataDto): JobPostCreateData {
        return JobPostCreateData(
            id = dto.id,
            status = dto.status,
            createdAt = dto.createdAt
        )
    }

    fun toCloseJobPostData(dto: CloseJobPostDataDto): CloseJobPostData {
        return CloseJobPostData(
            id = dto.id,
            status = dto.status,
            isClosed = dto.isClosed,
            closedAt = dto.closedAt
        )
    }

    // ======================================================
    // DOMAIN TO DTO MAPPERS (Request Objects)
    // ======================================================

    fun toGetAllJobsRequestDto(params: GetAllJobsParams): GetAllJobsRequestDto {
        return GetAllJobsRequestDto(
            limit = params.limit,
            offset = params.offset,
            city = params.city,
            minPay = params.minPay,
            maxPay = params.maxPay,
            sortBy = params.sortBy,
            // Job Characteristics
            employmentType = params.employmentType,
            paymentType = params.paymentType,
            workArrangement = params.workArrangement,
            commitment = params.commitment,
            workSchedule = params.workSchedule,
            documentationLevel = params.documentationLevel,
            skillLevel = params.skillLevel,
            experienceLevel = params.experienceLevel,
            educationLevel = params.educationLevel,
            // New Filters
            isAnonymous = params.isAnonymous,
            isRemote = params.isRemote,
            applicationDeadlineAfter = params.applicationDeadlineAfter,
            applicationDeadlineBefore = params.applicationDeadlineBefore,
            startDateAfter = params.startDateAfter,
            startDateBefore = params.startDateBefore,
            hoursPerWeekMin = params.hoursPerWeekMin,
            hoursPerWeekMax = params.hoursPerWeekMax,
            // Cache Control
            bypassCache = params.bypassCache,
            skipCache = params.skipCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toGetJobsByCategoryRequestDto(params: GetJobsByCategoryParams): GetJobsByCategoryRequestDto {
        return GetJobsByCategoryRequestDto(
            categoryId = params.categoryId,
            limit = params.limit,
            offset = params.offset,
            city = params.city,
            minPay = params.minPay,
            maxPay = params.maxPay,
            sortBy = params.sortBy,
            // Job Characteristics
            employmentType = params.employmentType,
            paymentType = params.paymentType,
            workArrangement = params.workArrangement,
            commitment = params.commitment,
            workSchedule = params.workSchedule,
            documentationLevel = params.documentationLevel,
            skillLevel = params.skillLevel,
            experienceLevel = params.experienceLevel,
            educationLevel = params.educationLevel,
            // New Filters
            isAnonymous = params.isAnonymous,
            isRemote = params.isRemote,
            applicationDeadlineAfter = params.applicationDeadlineAfter,
            applicationDeadlineBefore = params.applicationDeadlineBefore,
            startDateAfter = params.startDateAfter,
            startDateBefore = params.startDateBefore,
            hoursPerWeekMin = params.hoursPerWeekMin,
            hoursPerWeekMax = params.hoursPerWeekMax,
            // Cache Control
            bypassCache = params.bypassCache,
            skipCache = params.skipCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toGetJobByIdRequestDto(params: GetJobByIdParams): GetJobByIdRequestDto {
        return GetJobByIdRequestDto(
            id = params.id,
            bypassCache = params.bypassCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toGetJobListingsByOwnerRequestDto(params: GetJobListingsByOwnerParams): GetJobListingsByOwnerRequestDto {
        return GetJobListingsByOwnerRequestDto(
            accountId = params.accountId,
            status = params.status,
            limit = params.limit,
            offset = params.offset,
            sortBy = params.sortBy,
            bypassCache = params.bypassCache,
            skipCache = params.skipCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toCreateJobPostRequestDto(params: CreateJobPostParams): CreateJobPostRequestDto {
        return CreateJobPostRequestDto(
            // Core fields
            title = params.title,
            description = params.description,
            categoryId = params.categoryId,
            subCategoryId = params.subCategoryId,
            // Job Characteristics
            employmentType = params.employmentType,
            paymentType = params.paymentType,
            workArrangement = params.workArrangement,
            commitment = params.commitment,
            workSchedule = params.workSchedule,
            documentationLevel = params.documentationLevel,
            skillLevel = params.skillLevel,
            experienceLevel = params.experienceLevel,
            educationLevel = params.educationLevel,
            // Location
            locationCity = params.locationCity,
            locationNeighborhood = params.locationNeighborhood,
            isRemote = params.isRemote,
            // Compensation
            payAmount = params.payAmount,
            payRate = params.payRate,
            isNegotiable = params.isNegotiable,
            // Requirements
            skills = params.skills,
            requiresDocuments = params.requiresDocuments,
            requiresEquipment = params.requiresEquipment,
            documentsNeeded = params.documentsNeeded,
            equipmentRequired = params.equipmentRequired,
            additionalNotes = params.additionalNotes,
            // Referrals
            allowReferrals = params.allowReferrals,
            referralBonus = params.referralBonus,
            // Timeline
            applicationDeadline = params.applicationDeadline,
            startDate = params.startDate,
            startDateFlexible = params.startDateFlexible,
            maxApplications = params.maxApplications,
            // Privacy & Visibility
            isAnonymous = params.isAnonymous,
            displayName = params.displayName,
            contactEmail = params.contactEmail,
            // Work Details
            hoursPerWeek = params.hoursPerWeek,
            contractDuration = params.contractDuration,
            // Status
            status = params.status
        )
    }

    fun toUpdateJobPostRequestDto(params: UpdateJobPostParams): UpdateJobPostRequestDto {
        return UpdateJobPostRequestDto(
            title = params.title,
            description = params.description,
            categoryId = params.categoryId,
            subCategoryId = params.subCategoryId,
            employmentType = params.employmentType,
            paymentType = params.paymentType,
            workArrangement = params.workArrangement,
            commitment = params.commitment,
            workSchedule = params.workSchedule,
            documentationLevel = params.documentationLevel,
            skillLevel = params.skillLevel,
            experienceLevel = params.experienceLevel,
            educationLevel = params.educationLevel,
            locationCity = params.locationCity,
            locationNeighborhood = params.locationNeighborhood,
            isRemote = params.isRemote,
            payAmount = params.payAmount,
            payRate = params.payRate,
            isNegotiable = params.isNegotiable,
            skills = params.skills,
            requiresDocuments = params.requiresDocuments,
            requiresEquipment = params.requiresEquipment,
            documentsNeeded = params.documentsNeeded,
            equipmentRequired = params.equipmentRequired,
            additionalNotes = params.additionalNotes,
            allowReferrals = params.allowReferrals,
            referralBonus = params.referralBonus,
            applicationDeadline = params.applicationDeadline,
            startDate = params.startDate,
            startDateFlexible = params.startDateFlexible,
            maxApplications = params.maxApplications,
            isAnonymous = params.isAnonymous,
            displayName = params.displayName,
            contactEmail = params.contactEmail,
            hoursPerWeek = params.hoursPerWeek,
            contractDuration = params.contractDuration,
            status = params.status
        )
    }
}


