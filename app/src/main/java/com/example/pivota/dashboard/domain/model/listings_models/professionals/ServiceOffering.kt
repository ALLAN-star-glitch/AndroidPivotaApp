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
    val updatedAt: String,
    // ========== NEW: Negotiable Pricing Fields ==========
    val isNegotiable: Boolean = true,
    val minNegotiablePrice: Double? = null,
    val maxNegotiablePrice: Double? = null,
    // ========== NEW: Booking Fee Override Fields ==========
    val useCustomBookingFee: Boolean = false,
    val customBookingFeeEnabled: Boolean? = null,
    val customBookingFeeAmount: Double? = null,
    val customBookingFeeCurrency: String? = null,
    val customBookingFeeDescription: String? = null,
    val customBookingFeeRefundable: Boolean? = null
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
    val availability: List<DayAvailability>? = null,
    // ========== NEW: Negotiable Pricing Fields ==========
    val isNegotiable: Boolean = true,
    val minNegotiablePrice: Double? = null,
    val maxNegotiablePrice: Double? = null,
    // ========== NEW: Booking Fee Override Fields ==========
    val useCustomBookingFee: Boolean = false,
    val customBookingFeeEnabled: Boolean? = null,
    val customBookingFeeAmount: Double? = null,
    val customBookingFeeCurrency: String? = null,
    val customBookingFeeDescription: String? = null,
    val customBookingFeeRefundable: Boolean? = null
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
    val availability: List<DayAvailability>? = null,
    // ========== NEW: Negotiable Pricing Fields ==========
    val isNegotiable: Boolean? = null,
    val minNegotiablePrice: Double? = null,
    val maxNegotiablePrice: Double? = null,
    // ========== NEW: Booking Fee Override Fields ==========
    val useCustomBookingFee: Boolean? = null,
    val customBookingFeeEnabled: Boolean? = null,
    val customBookingFeeAmount: Double? = null,
    val customBookingFeeCurrency: String? = null,
    val customBookingFeeDescription: String? = null,
    val customBookingFeeRefundable: Boolean? = null
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
    val isCancelled: Boolean get() = value == "CANCELLED"
    val isDeclined: Boolean get() = value == "DECLINED"
    // REMOVED isCompleted - now tracked by ServiceExecutionStatus

    // Badge color for UI (Android resources)
    val badgeColorRes: Int
        get() = when (badgeVariant) {
            "warning" -> android.R.color.holo_orange_dark
            "success" -> android.R.color.holo_green_dark
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

// ======================================================
// SERVICE EXECUTION STATUS MODELS (NEW)
// ======================================================

enum class ServiceExecutionStatus(val value: String) {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED");

    val label: String
        get() = when (this) {
            NOT_STARTED -> "Not Started"
            IN_PROGRESS -> "In Progress"
            COMPLETED -> "Completed"
        }

    val badgeColorRes: Int
        get() = when (this) {
            NOT_STARTED -> android.R.color.darker_gray
            IN_PROGRESS -> android.R.color.holo_orange_dark
            COMPLETED -> android.R.color.holo_green_dark
        }
}

// ======================================================
// CREATE BOOKING REQUEST (UPDATED)
// ======================================================

data class CreateBookingRequest(
    val serviceId: String,
    val contractorId: String,
    val scheduledDate: String,  // ISO format
    val locationCity: String,
    val durationHours: Int? = null,
    val durationDays: Int? = null,
    val durationWeeks: Int? = null,
    val durationMonths: Int? = null,
    val customerNotes: String? = null,
    val proposedPrice: Double? = null  // NEW: For negotiation
)

// ======================================================
// BOOKING RESPONSE (UPDATED)
// ======================================================

data class BookingResponse(
    val id: String,
    val externalId: String,
    val contractorId: String,
    val clientId: String,
    val serviceId: String?,
    val service: ServiceOffering?,
    val contractorName: String?,
    val serviceTitle: String?,
    val status: String,  // BookingStatus value
    val serviceExecutionStatus: String? = null,  // ServiceExecutionStatus value
    val scheduledDate: String?,
    val locationCity: String?,
    val servicePrice: Double?,  // Base service price (priceUnit * duration)
    val servicePriceUnit: String?,
    val serviceDuration: Int?,
    val currency: String,
    val customerNotes: String?,
    val bookingFeeAmount: Double?,
    val bookingFeeCurrency: String?,
    val bookingFeeRefundable: Boolean?,
    val totalAmount: Double?,
    val confirmedAt: String?,
    val declinedAt: String?,
    val cancelledAt: String?,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String,
    // Helper properties
    val isNegotiated: Boolean = false
) {
    // Helper properties for UI
    val isPending: Boolean get() = status == "PENDING"
    val isConfirmed: Boolean get() = status == "CONFIRMED"
    val isCancelled: Boolean get() = status == "CANCELLED"
    val isDeclined: Boolean get() = status == "DECLINED"

    val isServiceStarted: Boolean get() = serviceExecutionStatus == "IN_PROGRESS"
    val isServiceCompleted: Boolean get() = serviceExecutionStatus == "COMPLETED"

    val displayPrice: String get() = "$currency ${servicePrice ?: 0}"
    val displayTotal: String get() = "$currency ${totalAmount ?: servicePrice ?: 0}"
    val displayBookingFee: String?
        get() = if (bookingFeeAmount != null && bookingFeeAmount > 0)
        "$currency $bookingFeeAmount" else null
}