package com.example.pivota.dashboard.data.mapper

import com.example.pivota.dashboard.data.dto.*
import com.example.pivota.dashboard.domain.model.listings_models.housing.*

import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class HousingMapper @Inject constructor() {

    // ======================================================
    // RESPONSE MAPPERS
    // ======================================================

    fun toHouseListingsResponse(dto: HouseListingsResponseDto): HouseListingsResponse {
        return HouseListingsResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.map { toHousePost(it) } ?: emptyList(),
            pagination = dto.pagination?.let { toHousingPaginationInfo(it) },
            error = dto.error?.let { toHousingErrorPayload(it) }
        )
    }

    fun toHouseListingResponse(dto: HouseListingResponseDto): HouseListingResponse {
        return HouseListingResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.let { toHousePost(it) },
            error = dto.error?.let { toHousingErrorPayload(it) }
        )
    }

    fun toHouseViewingsResponse(dto: HouseViewingsResponseDto): HouseViewingsResponse {
        return HouseViewingsResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.map { toHouseViewing(it) } ?: emptyList(),
            pagination = dto.pagination?.let { toHousingPaginationInfo(it) },
            error = dto.error?.let { toHousingErrorPayload(it) }
        )
    }

    fun toHouseViewingResponse(dto: HouseViewingResponseDto): HouseViewingResponse {
        return HouseViewingResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.let { toHouseViewing(it) },
            error = dto.error?.let { toHousingErrorPayload(it) }
        )
    }

    fun toHouseCreateResponse(dto: HouseCreateResponseDto): HouseCreateResponse {
        return HouseCreateResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.let { toHouseCreateData(it) },
            error = dto.error?.let { toHousingErrorPayload(it) }
        )
    }

    // ======================================================
    // DTO TO DOMAIN MAPPERS
    // ======================================================

    fun toHousePost(dto: HousePostDto): HousePost {
        return HousePost(
            // Core Identifiers
            id = dto.id,
            externalId = dto.externalId,
            // Basic Info
            title = dto.title,
            description = dto.description,
            price = dto.price,
            currency = dto.currency,
            // Location
            locationCity = dto.locationCity,
            locationNeighborhood = dto.locationNeighborhood,
            address = dto.address,
            // Property Details
            bedrooms = dto.bedrooms,
            bathrooms = dto.bathrooms,
            squareFootage = dto.squareFootage,
            yearBuilt = dto.yearBuilt,
            propertyType = dto.propertyType,
            // Listing Type
            listingType = dto.listingType,
            status = dto.status,
            // Features
            isFurnished = dto.isFurnished,
            amenities = dto.amenities,
            // Rental Specific
            minimumLeaseTerm = dto.minimumLeaseTerm,
            maximumLeaseTerm = dto.maximumLeaseTerm,
            depositAmount = dto.depositAmount,
            isPetFriendly = dto.isPetFriendly,
            utilitiesIncluded = dto.utilitiesIncluded,
            utilitiesDetails = dto.utilitiesDetails,
            // Sale Specific
            isNegotiable = dto.isNegotiable,
            titleDeedAvailable = dto.titleDeedAvailable,
            // Category
            category = dto.category?.let { toHousingCategoryBasic(it) },
            subCategory = dto.subCategory?.let { toHousingCategoryBasic(it) },
            // Identity
            creator = toHousingUserBasic(dto.creator),
            account = toHousingAccountBasic(dto.account),
            // Images
            images = dto.images.map { toHouseImage(it) },
            // Timestamps
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toHousingCategoryBasic(dto: HousingCategoryBasicDto): HousingCategoryBasic {
        return HousingCategoryBasic(
            id = dto.id,
            name = dto.name,
            slug = dto.slug,
            vertical = dto.vertical
        )
    }

    fun toHousingUserBasic(dto: HousingUserBasicDto): com.example.pivota.dashboard.domain.model.listings_models.jobs.UserBasic {
        return com.example.pivota.dashboard.domain.model.listings_models.jobs.UserBasic(
            id = dto.id,
            fullName = dto.fullName,
            email = dto.email
        )
    }

    fun toHousingAccountBasic(dto: HousingAccountBasicDto): com.example.pivota.dashboard.domain.model.listings_models.jobs.AccountBasic {
        return com.example.pivota.dashboard.domain.model.listings_models.jobs.AccountBasic(
            id = dto.id,
            name = dto.name
        )
    }

    fun toHouseImage(dto: HouseImageDto): HouseImage {
        return HouseImage(
            id = dto.id,
            url = dto.url,
            isMain = dto.isMain
        )
    }

    fun toHouseViewing(dto: HouseViewingDto): HouseViewing {
        return HouseViewing(
            id = dto.id,
            viewingDate = dto.viewingDate,
            status = dto.status,
            houseId = dto.houseId,
            houseTitle = dto.houseTitle,
            houseImageUrl = dto.houseImageUrl,
            viewerId = dto.viewerId,
            viewerName = dto.viewerName,
            viewerPhone = dto.viewerPhone,
            notes = dto.notes,
            bookedById = dto.bookedById,
            bookedByName = dto.bookedByName,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            housePrice = dto.housePrice,
            houseLocation = dto.houseLocation,
            houseNeighborhood = dto.houseNeighborhood
        )
    }

    fun toAdminHouseViewing(dto: AdminHouseViewingDto): AdminHouseViewing {
        return AdminHouseViewing(
            id = dto.id,
            viewingDate = dto.viewingDate,
            status = dto.status,
            houseId = dto.houseId,
            houseTitle = dto.houseTitle,
            houseImageUrl = dto.houseImageUrl,
            viewerId = dto.viewerId,
            viewerName = dto.viewerName,
            viewerPhone = dto.viewerPhone,
            notes = dto.notes,
            bookedById = dto.bookedById,
            bookedByName = dto.bookedByName,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            housePrice = dto.housePrice,
            houseLocation = dto.houseLocation,
            houseNeighborhood = dto.houseNeighborhood,
            adminMetadata = dto.adminMetadata?.let { toAdminViewingMetadata(it) }
        )
    }

    fun toAdminViewingMetadata(dto: AdminViewingMetadataDto): AdminViewingMetadata {
        return AdminViewingMetadata(
            ipAddress = dto.ipAddress,
            userAgent = dto.userAgent,
            scheduledAt = dto.scheduledAt,
            isAdminBooking = dto.isAdminBooking,
            auditTrail = dto.auditTrail
        )
    }

    fun toHousingPaginationInfo(dto: HousingPaginationInfoDto): HousingPaginationInfo {
        return HousingPaginationInfo(
            total = dto.total,
            limit = dto.limit,
            offset = dto.offset,
            hasMore = dto.hasMore
        )
    }

    fun toHousingErrorPayload(dto: HousingErrorPayloadDto): HousingErrorPayload {
        return HousingErrorPayload(
            code = dto.code,
            message = dto.message,
            details = dto.details
        )
    }

    fun toHouseCreateData(dto: HouseCreateDataDto): HouseCreateData {
        return HouseCreateData(
            id = dto.id,
            status = dto.status,
            createdAt = dto.createdAt
        )
    }

    // ======================================================
    // DOMAIN TO DTO MAPPERS (Request Objects)
    // ======================================================

    fun toGetAllHousingRequestDto(params: GetAllHousingParams): GetAllHousingRequestDto {
        return GetAllHousingRequestDto(
            limit = params.limit,
            offset = params.offset,
            city = params.city,
            listingType = params.listingType,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            bedrooms = params.bedrooms,
            propertyType = params.propertyType,
            isFurnished = params.isFurnished,
            sortBy = params.sortBy,
            // Cache Control
            bypassCache = params.bypassCache,
            skipCache = params.skipCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toSearchHousingRequestDto(params: SearchHousingParams): SearchHousingRequestDto {
        return SearchHousingRequestDto(
            city = params.city,
            listingType = params.listingType,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            bedrooms = params.bedrooms,
            propertyType = params.propertyType,
            minLeaseTerm = params.minLeaseTerm,
            isPetFriendly = params.isPetFriendly,
            utilitiesIncluded = params.utilitiesIncluded,
            isNegotiable = params.isNegotiable,
            titleDeedAvailable = params.titleDeedAvailable,
            sortBy = params.sortBy,
            limit = params.limit,
            offset = params.offset,
            categoryId = params.categoryId,
            subCategoryId = params.subCategoryId,
            // Cache Control
            bypassCache = params.bypassCache,
            skipCache = params.skipCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toGetHousingByCategoryRequestDto(params: GetHousingByCategoryParams): GetHousingByCategoryRequestDto {
        return GetHousingByCategoryRequestDto(
            categoryId = params.categoryId,
            city = params.city,
            listingType = params.listingType,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            bedrooms = params.bedrooms,
            propertyType = params.propertyType,
            isFurnished = params.isFurnished,
            limit = params.limit,
            offset = params.offset,
            // Cache Control
            bypassCache = params.bypassCache,
            skipCache = params.skipCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toGetHouseByIdRequestDto(params: GetHouseByIdParams): GetHouseByIdRequestDto {
        return GetHouseByIdRequestDto(
            id = params.id,
            bypassCache = params.bypassCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toGetOwnHousingRequestDto(params: GetOwnHousingParams): GetOwnHousingRequestDto {
        return GetOwnHousingRequestDto(
            status = params.status,
            limit = params.limit,
            offset = params.offset,
            sortBy = params.sortBy
        )
    }

    fun toGetAdminHousingRequestDto(params: GetAdminHousingParams): GetAdminHousingRequestDto {
        return GetAdminHousingRequestDto(
            status = params.status,
            accountId = params.accountId,
            creatorId = params.creatorId,
            listingType = params.listingType,
            propertyType = params.propertyType,
            minBedrooms = params.minBedrooms,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            limit = params.limit,
            offset = params.offset,
            // Cache Control
            bypassCache = params.bypassCache,
            skipCache = params.skipCache,
            refreshCache = params.refreshCache,
            cacheTTL = params.cacheTTL,
            readOnly = params.readOnly
        )
    }

    fun toCreateHouseRequestDto(params: CreateHouseParams): CreateHouseRequestDto {
        return CreateHouseRequestDto(
            // Core fields
            title = params.title,
            description = params.description,
            categoryId = params.categoryId,
            subCategoryId = params.subCategoryId,
            listingType = params.listingType,
            price = params.price,
            currency = params.currency,
            locationCity = params.locationCity,
            locationNeighborhood = params.locationNeighborhood,
            address = params.address,
            // Property Details
            bedrooms = params.bedrooms,
            bathrooms = params.bathrooms,
            squareFootage = params.squareFootage,
            yearBuilt = params.yearBuilt,
            propertyType = params.propertyType,
            // Features
            isFurnished = params.isFurnished,
            amenities = params.amenities,
            // Rental Specific
            minimumLeaseTerm = params.minimumLeaseTerm,
            maximumLeaseTerm = params.maximumLeaseTerm,
            depositAmount = params.depositAmount,
            isPetFriendly = params.isPetFriendly,
            utilitiesIncluded = params.utilitiesIncluded,
            utilitiesDetails = params.utilitiesDetails,
            // Sale Specific
            isNegotiable = params.isNegotiable,
            titleDeedAvailable = params.titleDeedAvailable
        )
    }

    fun toAdminCreateHouseRequestDto(params: AdminCreateHouseParams): AdminCreateHouseRequestDto {
        return AdminCreateHouseRequestDto(
            // Core fields
            title = params.title,
            description = params.description,
            categoryId = params.categoryId,
            subCategoryId = params.subCategoryId,
            listingType = params.listingType,
            price = params.price,
            currency = params.currency,
            locationCity = params.locationCity,
            locationNeighborhood = params.locationNeighborhood,
            address = params.address,
            // Property Details
            bedrooms = params.bedrooms,
            bathrooms = params.bathrooms,
            squareFootage = params.squareFootage,
            yearBuilt = params.yearBuilt,
            propertyType = params.propertyType,
            // Features
            isFurnished = params.isFurnished,
            amenities = params.amenities,
            // Rental Specific
            minimumLeaseTerm = params.minimumLeaseTerm,
            maximumLeaseTerm = params.maximumLeaseTerm,
            depositAmount = params.depositAmount,
            isPetFriendly = params.isPetFriendly,
            utilitiesIncluded = params.utilitiesIncluded,
            utilitiesDetails = params.utilitiesDetails,
            // Sale Specific
            isNegotiable = params.isNegotiable,
            titleDeedAvailable = params.titleDeedAvailable,
            // Admin Only
            creatorId = params.creatorId
        )
    }

    fun toUpdateHouseRequestDto(params: UpdateHouseParams): UpdateHouseRequestDto {
        return UpdateHouseRequestDto(
            title = params.title,
            description = params.description,
            price = params.price,
            locationNeighborhood = params.locationNeighborhood,
            address = params.address,
            bedrooms = params.bedrooms,
            bathrooms = params.bathrooms,
            squareFootage = params.squareFootage,
            yearBuilt = params.yearBuilt,
            propertyType = params.propertyType,
            isFurnished = params.isFurnished,
            amenities = params.amenities,
            status = params.status,
            // Rental Specific
            minimumLeaseTerm = params.minimumLeaseTerm,
            maximumLeaseTerm = params.maximumLeaseTerm,
            depositAmount = params.depositAmount,
            isPetFriendly = params.isPetFriendly,
            utilitiesIncluded = params.utilitiesIncluded,
            utilitiesDetails = params.utilitiesDetails,
            // Sale Specific
            isNegotiable = params.isNegotiable,
            titleDeedAvailable = params.titleDeedAvailable
        )
    }

    fun toAdminUpdateHouseRequestDto(params: AdminUpdateHouseParams): AdminUpdateHouseRequestDto {
        return AdminUpdateHouseRequestDto(
            title = params.title,
            description = params.description,
            price = params.price,
            locationNeighborhood = params.locationNeighborhood,
            address = params.address,
            bedrooms = params.bedrooms,
            bathrooms = params.bathrooms,
            squareFootage = params.squareFootage,
            yearBuilt = params.yearBuilt,
            propertyType = params.propertyType,
            isFurnished = params.isFurnished,
            amenities = params.amenities,
            status = params.status,
            // Rental Specific
            minimumLeaseTerm = params.minimumLeaseTerm,
            maximumLeaseTerm = params.maximumLeaseTerm,
            depositAmount = params.depositAmount,
            isPetFriendly = params.isPetFriendly,
            utilitiesIncluded = params.utilitiesIncluded,
            utilitiesDetails = params.utilitiesDetails,
            // Sale Specific
            isNegotiable = params.isNegotiable,
            titleDeedAvailable = params.titleDeedAvailable,
            // Admin Only
            creatorId = params.creatorId,
            accountId = params.accountId
        )
    }

    fun toScheduleViewingRequestDto(params: ScheduleViewingParams): ScheduleViewingRequestDto {
        return ScheduleViewingRequestDto(
            viewingDate = params.viewingDate,
            notes = params.notes
        )
    }

    fun toAdminScheduleViewingRequestDto(params: AdminScheduleViewingParams): AdminScheduleViewingRequestDto {
        return AdminScheduleViewingRequestDto(
            viewingDate = params.viewingDate,
            notes = params.notes,
            targetViewerId = params.targetViewerId,
            targetViewerEmail = params.targetViewerEmail,
            targetViewerName = params.targetViewerName
        )
    }
}