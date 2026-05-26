package com.example.pivota.dashboard.domain.model.listings_models.professionals

// com.example.pivota.dashboard.domain.model.listings_models.professionals/ServiceOffering.kt

data class ServiceOffering(
    val id: String,
    val externalId: String,
    val professionalName: String,
    val professionalAvatar: String?,
    val isVerified: Boolean,
    val title: String,
    val description: String,
    val categoryId: String,
    val categoryName: String,
    val verticals: List<String>,
    val basePrice: Double,
    val priceUnit: String,
    val currency: String,
    val locationCity: String,
    val locationNeighborhood: String?,
    val availability: List<DayAvailability>,
    val yearsExperience: Int,
    val hourlyRate: Double,
    val serviceAreas: List<String>,
    val status: String,
    val averageRating: Double,
    val reviewCount: Int,
    val createdAt: String,
    val updatedAt: String
)

data class DayAvailability(
    val day: String,
    val open: String,
    val close: String,
    val isClosed: Boolean
)

data class ServiceOfferingsResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: List<ServiceOffering>,
    val pagination: PaginationInfo?
)

data class PaginationInfo(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)