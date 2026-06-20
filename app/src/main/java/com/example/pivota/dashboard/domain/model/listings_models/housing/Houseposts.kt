package com.example.pivota.dashboard.domain.model.listings_models.housing

import com.example.pivota.dashboard.domain.model.listings_models.jobs.AccountBasic
import com.example.pivota.dashboard.domain.model.listings_models.jobs.UserBasic

// ======================================================
// HOUSE POST MODEL (Matches HouseListingResponseDto)
// ======================================================

data class HousePost(
    // Core Identifiers
    val id: String,
    val externalId: String,

    // Basic Info
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "KES",

    // Location
    val locationCity: String,
    val locationNeighborhood: String? = null,
    val address: String? = null,

    // Property Details
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val squareFootage: Int? = null,
    val yearBuilt: Int? = null,
    val propertyType: String? = null, // APARTMENT, HOUSE, CONDO, TOWNHOUSE, VILLA, STUDIO

    // Listing Type
    val listingType: String, // RENTAL or SALE
    val status: String, // ACTIVE, PENDING, SOLD, RENTED, etc.

    // Features
    val isFurnished: Boolean = false,
    val amenities: List<String> = emptyList(),

    // Rental Specific Fields (only for rental listings)
    val minimumLeaseTerm: Int? = null, // in months
    val maximumLeaseTerm: Int? = null, // in months
    val depositAmount: Double? = null,
    val isPetFriendly: Boolean? = null,
    val utilitiesIncluded: Boolean? = null,
    val utilitiesDetails: String? = null,

    // Sale Specific Fields (only for sale listings)
    val isNegotiable: Boolean? = null,
    val titleDeedAvailable: Boolean? = null,

    // Category
    val category: HousingCategoryBasic? = null,
    val subCategory: HousingCategoryBasic? = null,

    // Identity - Creator & Account
    val creator: UserBasic,
    val account: AccountBasic,

    // Images
    val images: List<HouseImage> = emptyList(),

    // Timestamps
    val createdAt: String,
    val updatedAt: String
)

// ======================================================
// HOUSING CATEGORY BASIC
// ======================================================

data class HousingCategoryBasic(
    val id: String,
    val name: String,
    val slug: String,
    val vertical: String
)

// ======================================================
// HOUSE IMAGE
// ======================================================

data class HouseImage(
    val id: String,
    val url: String,
    val isMain: Boolean = false
)

// ======================================================
// HOUSE SUMMARY (For Search & Dashboard Lists)
// ======================================================

data class HouseSummary(
    val id: String,
    val title: String,
    val price: Double,
    val currency: String = "KES",
    val bedrooms: Int? = null,
    val locationCity: String,
    val locationNeighborhood: String? = null,
    val status: String,
    val imageUrl: String? = null,
    val accountName: String,
    val createdAt: String
)

// ======================================================
// HOUSE VIEWING
// ======================================================

data class HouseViewingSummary(
    val id: String,
    val viewingDate: String,
    val status: String, // SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
    val houseId: String,
    val houseTitle: String,
    val houseImageUrl: String
)

data class HouseViewing(
    val id: String,
    val viewingDate: String,
    val status: String,
    val houseId: String,
    val houseTitle: String,
    val houseImageUrl: String,
    val viewerId: String,
    val viewerName: String,
    val viewerPhone: String,
    val notes: String? = null,
    val bookedById: String,
    val bookedByName: String,
    val createdAt: String,
    val updatedAt: String,
    val housePrice: Double,
    val houseLocation: String,
    val houseNeighborhood: String? = null
)

data class AdminHouseViewing(
    val id: String,
    val viewingDate: String,
    val status: String,
    val houseId: String,
    val houseTitle: String,
    val houseImageUrl: String,
    val viewerId: String,
    val viewerName: String,
    val viewerPhone: String,
    val notes: String? = null,
    val bookedById: String,
    val bookedByName: String,
    val createdAt: String,
    val updatedAt: String,
    val housePrice: Double,
    val houseLocation: String,
    val houseNeighborhood: String? = null,
    val adminMetadata: AdminViewingMetadata? = null
)

data class AdminViewingMetadata(
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val scheduledAt: String,
    val isAdminBooking: Boolean,
    val auditTrail: String? = null
)

// ======================================================
// RESPONSE WRAPPERS
// ======================================================

data class HouseListingsResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: List<HousePost> = emptyList(),
    val pagination: HousingPaginationInfo? = null,
    val error: HousingErrorPayload? = null
)

data class HouseListingResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: HousePost? = null,
    val error: HousingErrorPayload? = null
)

data class HouseViewingsResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: List<HouseViewing> = emptyList(),
    val pagination: HousingPaginationInfo? = null,
    val error: HousingErrorPayload? = null
)

data class HouseViewingResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: HouseViewing? = null,
    val error: HousingErrorPayload? = null
)

data class HouseCreateResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: HouseCreateData? = null,
    val error: HousingErrorPayload? = null
)

data class HouseCreateData(
    val id: String,
    val status: String,
    val createdAt: String
)

data class HousingPaginationInfo(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)

data class HousingErrorPayload(
    val code: String,
    val message: String,
    val details: String? = null
)

// ======================================================
// REQUEST PARAMETERS (Matches backend DTOs)
// ======================================================

// GET /housing-module/listings
data class GetAllHousingParams(
    val limit: Int = 20,
    val offset: Int = 0,
    val city: String? = null,
    val listingType: String? = null, // RENTAL or SALE
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val bedrooms: Int? = null,
    val propertyType: String? = null, // APARTMENT, HOUSE, CONDO, TOWNHOUSE, VILLA, STUDIO
    val isFurnished: Boolean? = null,
    val sortBy: String = "recent", // recent, price_asc, price_desc
    // Cache Control
    val bypassCache: Boolean = false,
    val skipCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 300
)

// GET /housing-module/listings/search
data class SearchHousingParams(
    val city: String? = null,
    val listingType: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val bedrooms: Int? = null,
    val propertyType: String? = null,
    val minLeaseTerm: Int? = null,
    val isPetFriendly: Boolean? = null,
    val utilitiesIncluded: Boolean? = null,
    val isNegotiable: Boolean? = null,
    val titleDeedAvailable: Boolean? = null,
    val sortBy: String = "recent",
    val limit: Int = 20,
    val offset: Int = 0,
    val categoryId: String? = null,
    val subCategoryId: String? = null,
    // Cache Control
    val bypassCache: Boolean = false,
    val skipCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 300
)

// GET /housing-module/listings/category
data class GetHousingByCategoryParams(
    val categoryId: String,
    val city: String? = null,
    val listingType: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val bedrooms: Int? = null,
    val propertyType: String? = null,
    val isFurnished: Boolean? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    // Cache Control
    val bypassCache: Boolean = false,
    val skipCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 300
)

// GET /housing-module/details/:id
data class GetHouseByIdParams(
    val id: String,
    val bypassCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 600
)

// GET /housing-module/my-listings
data class GetOwnHousingParams(
    val status: String? = null, // ACTIVE, PENDING, SOLD, RENTED, etc.
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: String = "recent"
)

// GET /housing-module/admin/listings (Admin only)
data class GetAdminHousingParams(
    val status: String? = null,
    val accountId: String? = null,
    val creatorId: String? = null,
    val listingType: String? = null,
    val propertyType: String? = null,
    val minBedrooms: Int? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    // Cache Control
    val bypassCache: Boolean = false,
    val skipCache: Boolean = false,
    val refreshCache: Boolean = false,
    val readOnly: Boolean = false,
    val cacheTTL: Int = 300
)

// POST /housing-module/listings
data class CreateHouseParams(
    // Core fields
    val title: String,
    val description: String,
    val categoryId: String,
    val subCategoryId: String,
    val listingType: String, // RENTAL or SALE
    val price: Double,
    val currency: String = "KES",
    val locationCity: String,
    val locationNeighborhood: String? = null,
    val address: String? = null,
    // Property Details
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val squareFootage: Int? = null,
    val yearBuilt: Int? = null,
    val propertyType: String? = null,
    // Features
    val isFurnished: Boolean = false,
    val amenities: List<String> = emptyList(),
    // Rental Specific
    val minimumLeaseTerm: Int? = null,
    val maximumLeaseTerm: Int? = null,
    val depositAmount: Double? = null,
    val isPetFriendly: Boolean? = null,
    val utilitiesIncluded: Boolean? = null,
    val utilitiesDetails: String? = null,
    // Sale Specific
    val isNegotiable: Boolean? = null,
    val titleDeedAvailable: Boolean? = null
)

// POST /housing-module/admin/accounts/:accountId/listings (Admin only)
data class AdminCreateHouseParams(
    // Core fields
    val title: String,
    val description: String,
    val categoryId: String,
    val subCategoryId: String,
    val listingType: String,
    val price: Double,
    val currency: String = "KES",
    val locationCity: String,
    val locationNeighborhood: String? = null,
    val address: String? = null,
    // Property Details
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val squareFootage: Int? = null,
    val yearBuilt: Int? = null,
    val propertyType: String? = null,
    // Features
    val isFurnished: Boolean = false,
    val amenities: List<String> = emptyList(),
    // Rental Specific
    val minimumLeaseTerm: Int? = null,
    val maximumLeaseTerm: Int? = null,
    val depositAmount: Double? = null,
    val isPetFriendly: Boolean? = null,
    val utilitiesIncluded: Boolean? = null,
    val utilitiesDetails: String? = null,
    // Sale Specific
    val isNegotiable: Boolean? = null,
    val titleDeedAvailable: Boolean? = null,
    // Admin Only
    val creatorId: String? = null
)

// PATCH /housing-module/listings/:id
data class UpdateHouseParams(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val locationNeighborhood: String? = null,
    val address: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val squareFootage: Int? = null,
    val yearBuilt: Int? = null,
    val propertyType: String? = null,
    val isFurnished: Boolean? = null,
    val amenities: List<String>? = null,
    val status: String? = null,
    // Rental Specific
    val minimumLeaseTerm: Int? = null,
    val maximumLeaseTerm: Int? = null,
    val depositAmount: Double? = null,
    val isPetFriendly: Boolean? = null,
    val utilitiesIncluded: Boolean? = null,
    val utilitiesDetails: String? = null,
    // Sale Specific
    val isNegotiable: Boolean? = null,
    val titleDeedAvailable: Boolean? = null
)

// PATCH /housing-module/admin/listings/:id (Admin only)
data class AdminUpdateHouseParams(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val locationNeighborhood: String? = null,
    val address: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val squareFootage: Int? = null,
    val yearBuilt: Int? = null,
    val propertyType: String? = null,
    val isFurnished: Boolean? = null,
    val amenities: List<String>? = null,
    val status: String? = null,
    // Rental Specific
    val minimumLeaseTerm: Int? = null,
    val maximumLeaseTerm: Int? = null,
    val depositAmount: Double? = null,
    val isPetFriendly: Boolean? = null,
    val utilitiesIncluded: Boolean? = null,
    val utilitiesDetails: String? = null,
    // Sale Specific
    val isNegotiable: Boolean? = null,
    val titleDeedAvailable: Boolean? = null,
    // Admin Only
    val creatorId: String? = null,
    val accountId: String? = null
)

// POST /housing-module/listings/:id/viewing
data class ScheduleViewingParams(
    val viewingDate: String, // ISO 8601 format
    val notes: String? = null
)

// POST /housing-module/admin/listings/:id/viewing (Admin only)
data class AdminScheduleViewingParams(
    val viewingDate: String,
    val notes: String? = null,
    val targetViewerId: String,
    val targetViewerEmail: String? = null,
    val targetViewerName: String? = null
)

// ======================================================
// HELPER EXTENSIONS
// ======================================================

fun HousePost.getMainImage(): String? {
    return images.find { it.isMain }?.url ?: images.firstOrNull()?.url
}

fun HousePost.getFormattedPrice(): String {
    return when {
        listingType == "RENTAL" -> "${price.toInt()} KES/mo"
        else -> "${price.toInt()} KES"
    }
}

fun HousePost.getFormattedLocation(): String {
    return when {
        locationNeighborhood != null -> "$locationCity, $locationNeighborhood"
        else -> locationCity
    }
}

fun HousePost.getPropertyTypeLabel(): String {
    return when (propertyType) {
        "APARTMENT" -> "Apartment"
        "HOUSE" -> "House"
        "CONDO" -> "Condo"
        "TOWNHOUSE" -> "Townhouse"
        "VILLA" -> "Villa"
        "STUDIO" -> "Studio"
        else -> propertyType ?: "Property"
    }
}

fun HousePost.getListingTypeLabel(): String {
    return when (listingType) {
        "RENTAL" -> "For Rent"
        "SALE" -> "For Sale"
        else -> listingType
    }
}

fun HousePost.getStatusLabel(): String {
    return when (status) {
        "ACTIVE" -> "Active"
        "PENDING" -> "Pending"
        "SOLD" -> "Sold"
        "RENTED" -> "Rented"
        "EXPIRED" -> "Expired"
        "DRAFT" -> "Draft"
        else -> status
    }
}

fun HousePost.getBedroomsLabel(): String {
    return when (bedrooms) {
        null -> "N/A"
        0 -> "Studio"
        1 -> "1 Bedroom"
        else -> "$bedrooms Bedrooms"
    }
}

// ======================================================
// SEALED CLASS FOR UI STATE
// ======================================================

sealed class HousingUiState {
    object Idle : HousingUiState()
    object Loading : HousingUiState()
    data class Success(
        val houses: List<HousePost>,
        val hasMore: Boolean,
        val totalCount: Int
    ) : HousingUiState()
    data class Error(val message: String) : HousingUiState()
}