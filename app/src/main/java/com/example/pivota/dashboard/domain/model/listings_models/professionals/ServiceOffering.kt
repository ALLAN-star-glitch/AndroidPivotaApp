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
    val basePrice: Double,
    val priceUnit: String,
    val currency: String,
    // ❌ REMOVED locationCity and locationNeighborhood
    // ✅ ADDED coverageAreas (replaces serviceAreas)
    val coverageAreas: List<String>,
    val availability: List<DayAvailability>,
    val yearsExperience: Int?,
    val hourlyRate: Double?,
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

// ======================================================
// CREATE SERVICE OFFERING REQUEST
// ======================================================

data class CreateServiceOfferingRequest(
    val title: String,
    val description: String,
    val categoryId: String,
    val basePrice: Double,
    val priceUnit: String,
    val currency: String = "KES",
    val coverageAreas: List<String>,  // ✅ Required field
    val yearsExperience: Int? = null,
    val additionalNotes: String? = null,
    val availability: List<DayAvailability>? = null
)

// ======================================================
// UPDATE SERVICE OFFERING REQUEST
// ======================================================

data class UpdateServiceOfferingRequest(
    val title: String? = null,
    val description: String? = null,
    val basePrice: Double? = null,
    val priceUnit: String? = null,
    val coverageAreas: List<String>? = null,
    val availability: List<DayAvailability>? = null
)

// ======================================================
// BOOKING STATUS MODELS (NEW)
// ======================================================

data class BookingStatus(
    val value: String,
    val label: String,
    val description: String,
    val badgeVariant: String,
    val order: Int
) {
    // Helper properties for UI
    val isPending: Boolean get() = value == "PENDING"
    val isConfirmed: Boolean get() = value == "CONFIRMED"
    val isCompleted: Boolean get() = value == "COMPLETED"
    val isCancelled: Boolean get() = value == "CANCELLED"
    val isDeclined: Boolean get() = value == "DECLINED"

    // Badge color for UI (Android resources)
    val badgeColorRes: Int
        get() = when (badgeVariant) {
            "warning" -> android.R.color.holo_orange_dark
            "success" -> android.R.color.holo_green_dark
            "info" -> android.R.color.holo_blue_dark
            "danger" -> android.R.color.holo_red_dark
            else -> android.R.color.darker_gray
        }
}

data class BookingStatusListResponse(
    val success: Boolean,
    val message: String,
    val code: String,
    val statuses: List<BookingStatus>
)