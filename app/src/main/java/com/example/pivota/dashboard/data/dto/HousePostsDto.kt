package com.example.pivota.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ======================================================
// HOUSE LISTING DTOS
// ======================================================

@Serializable
data class HouseListingsResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: List<HousePostDto>? = null,
    @SerialName("pagination") val pagination: HousingPaginationInfoDto? = null,
    @SerialName("error") val error: HousingErrorPayloadDto? = null
)

@Serializable
data class HouseListingResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: HousePostDto? = null,
    @SerialName("error") val error: HousingErrorPayloadDto? = null
)

@Serializable
data class HousePostDto(
    // Core Identifiers
    @SerialName("id") val id: String,
    @SerialName("externalId") val externalId: String,

    // Basic Info
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("price") val price: Double,
    @SerialName("currency") val currency: String = "KES",

    // Location
    @SerialName("locationCity") val locationCity: String,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("address") val address: String? = null,

    // Property Details
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("bathrooms") val bathrooms: Int? = null,
    @SerialName("squareFootage") val squareFootage: Int? = null,
    @SerialName("yearBuilt") val yearBuilt: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,

    // Listing Type
    @SerialName("listingType") val listingType: String,
    @SerialName("status") val status: String,

    // Features
    @SerialName("isFurnished") val isFurnished: Boolean = false,
    @SerialName("amenities") val amenities: List<String> = emptyList(),

    // Rental Specific Fields
    @SerialName("minimumLeaseTerm") val minimumLeaseTerm: Int? = null,
    @SerialName("maximumLeaseTerm") val maximumLeaseTerm: Int? = null,
    @SerialName("depositAmount") val depositAmount: Double? = null,
    @SerialName("isPetFriendly") val isPetFriendly: Boolean? = null,
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean? = null,
    @SerialName("utilitiesDetails") val utilitiesDetails: String? = null,

    // Sale Specific Fields
    @SerialName("isNegotiable") val isNegotiable: Boolean? = null,
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean? = null,

    // Category
    @SerialName("category") val category: HousingCategoryBasicDto? = null,
    @SerialName("subCategory") val subCategory: HousingCategoryBasicDto? = null,

    // Identity - Creator & Account
    @SerialName("creator") val creator: HousingUserBasicDto,
    @SerialName("account") val account: HousingAccountBasicDto,

    // Images
    @SerialName("images") val images: List<HouseImageDto> = emptyList(),

    // Timestamps
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

// ======================================================
// HOUSING BASIC DTOS
// ======================================================

@Serializable
data class HousingCategoryBasicDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("slug") val slug: String,
    @SerialName("vertical") val vertical: String
)

@Serializable
data class HousingUserBasicDto(
    @SerialName("id") val id: String,
    @SerialName("fullName") val fullName: String,
    @SerialName("email") val email: String? = null,
    @SerialName("phone") val phone: String? = null
)

@Serializable
data class HousingAccountBasicDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String
)

// ======================================================
// HOUSE IMAGE DTO
// ======================================================

@Serializable
data class HouseImageDto(
    @SerialName("id") val id: String,
    @SerialName("url") val url: String,
    @SerialName("isMain") val isMain: Boolean = false
)

// ======================================================
// HOUSE VIEWING DTOS
// ======================================================

@Serializable
data class HouseViewingsResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: List<HouseViewingDto>? = null,
    @SerialName("pagination") val pagination: HousingPaginationInfoDto? = null,
    @SerialName("error") val error: HousingErrorPayloadDto? = null
)

@Serializable
data class HouseViewingResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: HouseViewingDto? = null,
    @SerialName("error") val error: HousingErrorPayloadDto? = null
)

@Serializable
data class HouseViewingDto(
    @SerialName("id") val id: String,
    @SerialName("viewingDate") val viewingDate: String,
    @SerialName("status") val status: String,
    @SerialName("houseId") val houseId: String,
    @SerialName("houseTitle") val houseTitle: String,
    @SerialName("houseImageUrl") val houseImageUrl: String,
    @SerialName("viewerId") val viewerId: String,
    @SerialName("viewerName") val viewerName: String,
    @SerialName("viewerPhone") val viewerPhone: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("bookedById") val bookedById: String,
    @SerialName("bookedByName") val bookedByName: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("housePrice") val housePrice: Double,
    @SerialName("houseLocation") val houseLocation: String,
    @SerialName("houseNeighborhood") val houseNeighborhood: String? = null
)

// ======================================================
// ADMIN HOUSE VIEWING DTO
// ======================================================

@Serializable
data class AdminHouseViewingDto(
    @SerialName("id") val id: String,
    @SerialName("viewingDate") val viewingDate: String,
    @SerialName("status") val status: String,
    @SerialName("houseId") val houseId: String,
    @SerialName("houseTitle") val houseTitle: String,
    @SerialName("houseImageUrl") val houseImageUrl: String,
    @SerialName("viewerId") val viewerId: String,
    @SerialName("viewerName") val viewerName: String,
    @SerialName("viewerPhone") val viewerPhone: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("bookedById") val bookedById: String,
    @SerialName("bookedByName") val bookedByName: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("housePrice") val housePrice: Double,
    @SerialName("houseLocation") val houseLocation: String,
    @SerialName("houseNeighborhood") val houseNeighborhood: String? = null,
    @SerialName("adminMetadata") val adminMetadata: AdminViewingMetadataDto? = null
)

@Serializable
data class AdminViewingMetadataDto(
    @SerialName("ipAddress") val ipAddress: String? = null,
    @SerialName("userAgent") val userAgent: String? = null,
    @SerialName("scheduledAt") val scheduledAt: String,
    @SerialName("isAdminBooking") val isAdminBooking: Boolean,
    @SerialName("auditTrail") val auditTrail: String? = null
)

// ======================================================
// HOUSE CREATE RESPONSE DTO
// ======================================================

@Serializable
data class HouseCreateResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: HouseCreateDataDto? = null,
    @SerialName("error") val error: HousingErrorPayloadDto? = null
)

@Serializable
data class HouseCreateDataDto(
    @SerialName("id") val id: String,
    @SerialName("status") val status: String,
    @SerialName("createdAt") val createdAt: String
)

// ======================================================
// PAGINATION & ERROR DTOS
// ======================================================

@Serializable
data class HousingPaginationInfoDto(
    @SerialName("total") val total: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("offset") val offset: Int,
    @SerialName("hasMore") val hasMore: Boolean
)

@Serializable
data class HousingErrorPayloadDto(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("details") val details: String? = null
)

// ======================================================
// REQUEST DTOS
// ======================================================

// GET /housing-module/listings
@Serializable
data class GetAllHousingRequestDto(
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    @SerialName("city") val city: String? = null,
    @SerialName("listingType") val listingType: String? = null,
    @SerialName("minPrice") val minPrice: Double? = null,
    @SerialName("maxPrice") val maxPrice: Double? = null,
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    @SerialName("isFurnished") val isFurnished: Boolean? = null,
    @SerialName("sortBy") val sortBy: String = "recent",
    // Cache Control
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("skipCache") val skipCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 300,
    @SerialName("readOnly") val readOnly: Boolean = false
)

// GET /housing-module/listings/search
@Serializable
data class SearchHousingRequestDto(
    @SerialName("city") val city: String? = null,
    @SerialName("listingType") val listingType: String? = null,
    @SerialName("minPrice") val minPrice: Double? = null,
    @SerialName("maxPrice") val maxPrice: Double? = null,
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    @SerialName("minLeaseTerm") val minLeaseTerm: Int? = null,
    @SerialName("isPetFriendly") val isPetFriendly: Boolean? = null,
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean? = null,
    @SerialName("isNegotiable") val isNegotiable: Boolean? = null,
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean? = null,
    @SerialName("sortBy") val sortBy: String = "recent",
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    @SerialName("categoryId") val categoryId: String? = null,
    @SerialName("subCategoryId") val subCategoryId: String? = null,
    // Cache Control
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("skipCache") val skipCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 300,
    @SerialName("readOnly") val readOnly: Boolean = false
)

// GET /housing-module/listings/category
@Serializable
data class GetHousingByCategoryRequestDto(
    @SerialName("categoryId") val categoryId: String,
    @SerialName("city") val city: String? = null,
    @SerialName("listingType") val listingType: String? = null,
    @SerialName("minPrice") val minPrice: Double? = null,
    @SerialName("maxPrice") val maxPrice: Double? = null,
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    @SerialName("isFurnished") val isFurnished: Boolean? = null,
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    // Cache Control
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("skipCache") val skipCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 300,
    @SerialName("readOnly") val readOnly: Boolean = false
)

// GET /housing-module/details/:id
@Serializable
data class GetHouseByIdRequestDto(
    @SerialName("id") val id: String,
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 600,
    @SerialName("readOnly") val readOnly: Boolean = false
)

// GET /housing-module/my-listings
@Serializable
data class GetOwnHousingRequestDto(
    @SerialName("status") val status: String? = null,
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    @SerialName("sortBy") val sortBy: String = "recent"
)

// GET /housing-module/admin/listings (Admin only)
@Serializable
data class GetAdminHousingRequestDto(
    @SerialName("status") val status: String? = null,
    @SerialName("accountId") val accountId: String? = null,
    @SerialName("creatorId") val creatorId: String? = null,
    @SerialName("listingType") val listingType: String? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    @SerialName("minBedrooms") val minBedrooms: Int? = null,
    @SerialName("minPrice") val minPrice: Double? = null,
    @SerialName("maxPrice") val maxPrice: Double? = null,
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0,
    // Cache Control
    @SerialName("bypassCache") val bypassCache: Boolean = false,
    @SerialName("skipCache") val skipCache: Boolean = false,
    @SerialName("refreshCache") val refreshCache: Boolean = false,
    @SerialName("cacheTTL") val cacheTTL: Int = 300,
    @SerialName("readOnly") val readOnly: Boolean = false
)

// ======================================================
// CREATE HOUSE REQUEST DTOS
// ======================================================

// POST /housing-module/listings
@Serializable
data class CreateHouseRequestDto(
    // Core fields
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("categoryId") val categoryId: String,
    @SerialName("subCategoryId") val subCategoryId: String,
    @SerialName("listingType") val listingType: String,
    @SerialName("price") val price: Double,
    @SerialName("currency") val currency: String = "KES",
    @SerialName("locationCity") val locationCity: String,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("address") val address: String? = null,
    // Property Details
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("bathrooms") val bathrooms: Int? = null,
    @SerialName("squareFootage") val squareFootage: Int? = null,
    @SerialName("yearBuilt") val yearBuilt: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    // Features
    @SerialName("isFurnished") val isFurnished: Boolean = false,
    @SerialName("amenities") val amenities: List<String> = emptyList(),
    // Rental Specific
    @SerialName("minimumLeaseTerm") val minimumLeaseTerm: Int? = null,
    @SerialName("maximumLeaseTerm") val maximumLeaseTerm: Int? = null,
    @SerialName("depositAmount") val depositAmount: Double? = null,
    @SerialName("isPetFriendly") val isPetFriendly: Boolean? = null,
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean? = null,
    @SerialName("utilitiesDetails") val utilitiesDetails: String? = null,
    // Sale Specific
    @SerialName("isNegotiable") val isNegotiable: Boolean? = null,
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean? = null
)

// POST /housing-module/admin/accounts/:accountId/listings (Admin only)
@Serializable
data class AdminCreateHouseRequestDto(
    // Core fields
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("categoryId") val categoryId: String,
    @SerialName("subCategoryId") val subCategoryId: String,
    @SerialName("listingType") val listingType: String,
    @SerialName("price") val price: Double,
    @SerialName("currency") val currency: String = "KES",
    @SerialName("locationCity") val locationCity: String,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("address") val address: String? = null,
    // Property Details
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("bathrooms") val bathrooms: Int? = null,
    @SerialName("squareFootage") val squareFootage: Int? = null,
    @SerialName("yearBuilt") val yearBuilt: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    // Features
    @SerialName("isFurnished") val isFurnished: Boolean = false,
    @SerialName("amenities") val amenities: List<String> = emptyList(),
    // Rental Specific
    @SerialName("minimumLeaseTerm") val minimumLeaseTerm: Int? = null,
    @SerialName("maximumLeaseTerm") val maximumLeaseTerm: Int? = null,
    @SerialName("depositAmount") val depositAmount: Double? = null,
    @SerialName("isPetFriendly") val isPetFriendly: Boolean? = null,
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean? = null,
    @SerialName("utilitiesDetails") val utilitiesDetails: String? = null,
    // Sale Specific
    @SerialName("isNegotiable") val isNegotiable: Boolean? = null,
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean? = null,
    // Admin Only
    @SerialName("creatorId") val creatorId: String? = null
)

// ======================================================
// UPDATE HOUSE REQUEST DTOS
// ======================================================

// PATCH /housing-module/listings/:id
@Serializable
data class UpdateHouseRequestDto(
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("bathrooms") val bathrooms: Int? = null,
    @SerialName("squareFootage") val squareFootage: Int? = null,
    @SerialName("yearBuilt") val yearBuilt: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    @SerialName("isFurnished") val isFurnished: Boolean? = null,
    @SerialName("amenities") val amenities: List<String>? = null,
    @SerialName("status") val status: String? = null,
    // Rental Specific
    @SerialName("minimumLeaseTerm") val minimumLeaseTerm: Int? = null,
    @SerialName("maximumLeaseTerm") val maximumLeaseTerm: Int? = null,
    @SerialName("depositAmount") val depositAmount: Double? = null,
    @SerialName("isPetFriendly") val isPetFriendly: Boolean? = null,
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean? = null,
    @SerialName("utilitiesDetails") val utilitiesDetails: String? = null,
    // Sale Specific
    @SerialName("isNegotiable") val isNegotiable: Boolean? = null,
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean? = null
)

// PATCH /housing-module/admin/listings/:id (Admin only)
@Serializable
data class AdminUpdateHouseRequestDto(
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("bathrooms") val bathrooms: Int? = null,
    @SerialName("squareFootage") val squareFootage: Int? = null,
    @SerialName("yearBuilt") val yearBuilt: Int? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    @SerialName("isFurnished") val isFurnished: Boolean? = null,
    @SerialName("amenities") val amenities: List<String>? = null,
    @SerialName("status") val status: String? = null,
    // Rental Specific
    @SerialName("minimumLeaseTerm") val minimumLeaseTerm: Int? = null,
    @SerialName("maximumLeaseTerm") val maximumLeaseTerm: Int? = null,
    @SerialName("depositAmount") val depositAmount: Double? = null,
    @SerialName("isPetFriendly") val isPetFriendly: Boolean? = null,
    @SerialName("utilitiesIncluded") val utilitiesIncluded: Boolean? = null,
    @SerialName("utilitiesDetails") val utilitiesDetails: String? = null,
    // Sale Specific
    @SerialName("isNegotiable") val isNegotiable: Boolean? = null,
    @SerialName("titleDeedAvailable") val titleDeedAvailable: Boolean? = null,
    // Admin Only
    @SerialName("creatorId") val creatorId: String? = null,
    @SerialName("accountId") val accountId: String? = null
)

// ======================================================
// VIEWING REQUEST DTOS
// ======================================================

// POST /housing-module/listings/:id/viewing
@Serializable
data class ScheduleViewingRequestDto(
    @SerialName("viewingDate") val viewingDate: String,
    @SerialName("notes") val notes: String? = null
)

// POST /housing-module/admin/listings/:id/viewing (Admin only)
@Serializable
data class AdminScheduleViewingRequestDto(
    @SerialName("viewingDate") val viewingDate: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("targetViewerId") val targetViewerId: String,
    @SerialName("targetViewerEmail") val targetViewerEmail: String? = null,
    @SerialName("targetViewerName") val targetViewerName: String? = null
)