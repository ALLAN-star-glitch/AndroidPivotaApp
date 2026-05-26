package com.example.pivota.dashboard.data.dto



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// ======================================================
// SERVICE OFFERING DTOS
// ======================================================

@Serializable
data class ServiceOfferingsResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: List<ServiceOfferingDto>? = null,
    @SerialName("pagination") val pagination: PaginationInfoDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class ServiceOfferingDto(
    @SerialName("id") val id: String,
    @SerialName("externalId") val externalId: String,
    @SerialName("professionalName") val professionalName: String,
    @SerialName("professionalAvatar") val professionalAvatar: String? = null,
    @SerialName("isVerified") val isVerified: Boolean,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("categoryId") val categoryId: String,
    @SerialName("categoryName") val categoryName: String,
    @SerialName("verticals") val verticals: List<String>,
    @SerialName("basePrice") val basePrice: Double,
    @SerialName("priceUnit") val priceUnit: String,
    @SerialName("currency") val currency: String,
    @SerialName("locationCity") val locationCity: String,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("availability") val availability: List<DayAvailabilityDto>? = null,
    @SerialName("yearsExperience") val yearsExperience: Int,
    @SerialName("hourlyRate") val hourlyRate: Double,
    @SerialName("serviceAreas") val serviceAreas: List<String>,
    @SerialName("status") val status: String,
    @SerialName("averageRating") val averageRating: Double,
    @SerialName("reviewCount") val reviewCount: Int,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class DayAvailabilityDto(
    @SerialName("day") val day: String,
    @SerialName("open") val open: String,
    @SerialName("close") val close: String,
    @SerialName("isClosed") val isClosed: Boolean
)

@Serializable
data class PaginationInfoDto(
    @SerialName("total") val total: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("offset") val offset: Int,
    @SerialName("hasMore") val hasMore: Boolean
)

// ======================================================
// SERVICE OFFERING REQUEST DTO
// ======================================================

@Serializable
data class CreateServiceOfferingRequestDto(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("verticals") val verticals: List<String>,
    @SerialName("categoryId") val categoryId: String,
    @SerialName("basePrice") val basePrice: Double,
    @SerialName("priceUnit") val priceUnit: String,
    @SerialName("currency") val currency: String = "KES",
    @SerialName("locationCity") val locationCity: String,
    @SerialName("locationNeighborhood") val locationNeighborhood: String? = null,
    @SerialName("yearsExperience") val yearsExperience: Int? = null,
    @SerialName("additionalNotes") val additionalNotes: String? = null,
    @SerialName("availability") val availability: List<DayAvailabilityDto>? = null
)