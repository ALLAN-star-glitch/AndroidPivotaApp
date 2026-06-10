package com.example.pivota.dashboard.domain.model.listings_models.professionals

// ======================================================
// BOOKING DOMAIN MODELS
// ======================================================

/**
 * Main Booking domain model
 * Represents a booking from the backend
 */
data class Booking(
    val id: String,
    val externalId: String,
    val contractorId: String,
    val clientId: String,
    val serviceId: String? = null,
    val service: ServiceOffering? = null,
    val contractorName: String? = null,
    val serviceTitle: String? = null,
    val status: String,
    val serviceExecutionStatus: String? = null,
    val scheduledDate: String? = null,
    val locationCity: String? = null,
    val servicePrice: Double? = null,
    val servicePriceUnit: String? = null,
    val serviceDuration: Int? = null,
    val currency: String,
    val customerNotes: String? = null,
    val bookingFeeAmount: Double? = null,
    val bookingFeeCurrency: String? = null,
    val bookingFeeRefundable: Boolean? = null,
    val totalAmount: Double? = null,
    val confirmedAt: String? = null,
    val declinedAt: String? = null,
    val cancelledAt: String? = null,
    val completedAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
    // Enriched fields (populated when requested)
    val clientInfo: BookingClientInfo? = null,
    val contractorInfo: BookingContractorInfo? = null,
    val serviceDetails: BookingServiceDetails? = null
) {
    // Helper properties for UI
    val isPending: Boolean get() = status == "PENDING"
    val isConfirmed: Boolean get() = status == "CONFIRMED"
    val isCancelled: Boolean get() = status == "CANCELLED"
    val isDeclined: Boolean get() = status == "DECLINED"

    val isServiceNotStarted: Boolean get() = serviceExecutionStatus == "NOT_STARTED"
    val isServiceInProgress: Boolean get() = serviceExecutionStatus == "IN_PROGRESS"
    val isServiceCompleted: Boolean get() = serviceExecutionStatus == "COMPLETED"

    val displayServicePrice: String get() = "$currency ${servicePrice ?: 0}"
    val displayTotalAmount: String get() = "$currency ${totalAmount ?: servicePrice ?: 0}"
    val displayBookingFee: String?
        get() = if (bookingFeeAmount != null && bookingFeeAmount > 0) {
            "$currency $bookingFeeAmount"
        } else null

    val isNegotiated: Boolean get() = servicePrice != null && service != null && servicePrice != service.basePrice

    // Status badge color for UI
    val statusBadgeColor: Int
        get() = when (status) {
            "PENDING" -> android.R.color.holo_orange_dark
            "CONFIRMED" -> android.R.color.holo_green_dark
            "CANCELLED", "DECLINED" -> android.R.color.holo_red_dark
            else -> android.R.color.darker_gray
        }

    val executionBadgeColor: Int
        get() = when (serviceExecutionStatus) {
            "NOT_STARTED" -> android.R.color.darker_gray
            "IN_PROGRESS" -> android.R.color.holo_orange_dark
            "COMPLETED" -> android.R.color.holo_green_dark
            else -> android.R.color.darker_gray
        }
}

/**
 * Client information enriched in booking response
 */
data class BookingClientInfo(
    val uuid: String,
    val name: String,
    val email: String,
    val phone: String
)

/**
 * Contractor information enriched in booking response
 */
data class BookingContractorInfo(
    val uuid: String,
    val name: String,
    val profileImage: String? = null,
    val isVerified: Boolean,
    val rating: Double,
    val phone: String,
    val email: String
)

/**
 * Service details enriched in booking response
 */
data class BookingServiceDetails(
    val id: String,
    val title: String,
    val description: String? = null,
    val basePrice: Double,
    val priceUnit: String,
    val category: String? = null
)

// ======================================================
// CREATE BOOKING REQUEST MODEL
// ======================================================

/**
 * Request model for creating a new booking
 * Used to send data to the backend
 */
// Update the CreateBookingRequest in BookingModels.kt - NO clientId or isPlatformAdmin
data class CreateBookingRequest(
    val serviceId: String,
    val contractorId: String,
    val scheduledDate: String,  // ISO format: "2024-03-25T14:00:00"
    val locationCity: String,
    val durationHours: Int? = null,
    val durationDays: Int? = null,
    val durationWeeks: Int? = null,
    val durationMonths: Int? = null,
    val customerNotes: String? = null,
    val proposedPrice: Double? = null  // For negotiable services
)

// ======================================================
// BOOKING ACTION MODELS
// ======================================================

/**
 * Response model for booking actions (accept/decline/cancel)
 */
data class BookingAction(
    val id: String,
    val status: String,
    val updatedAt: String
)

/**
 * Request model for accepting a booking
 */
data class AcceptBookingRequest(
    val bookingId: String,
    val contractorId: String,
    val isPlatformAdmin: Boolean = false
)

/**
 * Request model for declining a booking
 */
data class DeclineBookingRequest(
    val bookingId: String,
    val contractorId: String,
    val reason: String? = null,
    val isPlatformAdmin: Boolean = false
)

/**
 * Request model for cancelling a booking
 */
data class CancelBookingRequest(
    val bookingId: String,
    val userId: String,
    val professionalId: String? = null,
    val reason: String? = null,
    val isPlatformAdmin: Boolean = false
)

// ======================================================
// BOOKING LIST & PAGINATION MODELS
// ======================================================

/**
 * Paginated response wrapper for bookings
 */
data class PaginatedBookings(
    val bookings: List<Booking>,
    val pagination: PaginationInfo?
)

/**
 * Request model for getting customer bookings
 */
data class GetCustomerBookingsRequest(
    val clientId: String,
    val status: String? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val isPlatformAdmin: Boolean = false
)

/**
 * Request model for getting contractor bookings
 */
data class GetContractorBookingsRequest(
    val contractorId: String,
    val status: String? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val isPlatformAdmin: Boolean = false
)

/**
 * Request model for getting booking details
 */
data class GetBookingDetailsRequest(
    val bookingId: String,
    val userId: String,
    val professionalId: String? = null,
    val isPlatformAdmin: Boolean = false
)

// ======================================================
// UPCOMING BOOKINGS & STATISTICS MODELS
// ======================================================

/**
 * Simplified booking for upcoming bookings list
 */
data class UpcomingBooking(
    val id: String,
    val serviceTitle: String? = null,
    val scheduledDate: String? = null,
    val locationCity: String? = null,
    val clientName: String? = null,
    val clientPhone: String? = null,
    val agreedPrice: Double? = null,
    val currency: String
)

/**
 * Booking statistics for contractor dashboard
 */
data class BookingStatistics(
    val total: Int,
    val pending: Int,
    val confirmed: Int,
    val completed: Int,
    val cancelled: Int,
    val declined: Int,
    val upcoming: Int,
    val completedThisMonth: Int
)

/**
 * Request model for getting professional booking stats
 */
data class GetProfessionalStatsRequest(
    val contractorId: String,
    val isPlatformAdmin: Boolean = false
)

/**
 * Request model for getting upcoming bookings
 */
data class GetUpcomingBookingsRequest(
    val contractorId: String,
    val limit: Int = 10,
    val isPlatformAdmin: Boolean = false
)

// ======================================================
// DURATION HELPER
// ======================================================

/**
 * Duration type enum for UI
 */
enum class DurationType(val displayName: String, val apiField: String) {
    HOURS("hours", "durationHours"),
    DAYS("days", "durationDays"),
    WEEKS("weeks", "durationWeeks"),
    MONTHS("months", "durationMonths");

    companion object {
        fun fromPriceUnit(priceUnit: String): DurationType {
            return when (priceUnit) {
                "PER_HOUR" -> HOURS
                "PER_DAY" -> DAYS
                "PER_WEEK" -> WEEKS
                "PER_MONTH" -> MONTHS
                else -> HOURS
            }
        }
    }
}