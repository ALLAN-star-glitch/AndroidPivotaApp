package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_offerings")
data class ServiceOfferingEntity(
    @PrimaryKey
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
    val coverageAreas: String, // Store as JSON string e.g., ["Nairobi", "Westlands"]
    val availability: String?, // Store as JSON string
    val yearsExperience: Int?,
    val hourlyRate: Double?,
    val status: String,
    val averageRating: Double,
    val reviewCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val lastUpdated: Long = System.currentTimeMillis(),
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

@Entity(tableName = "service_offerings_cache_metadata")
data class ServiceOfferingsCacheMetadataEntity(
    @PrimaryKey
    val categoryId: String,
    val lastUpdated: Long,
    val totalCount: Int,
    val etag: String? = null
)

// ======================================================
// BOOKING STATUS ENTITIES
// ======================================================

@Entity(tableName = "booking_statuses")
data class BookingStatusEntity(
    @PrimaryKey
    val value: String,
    val label: String,
    val description: String,
    val badgeVariant: String,
    val order: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "booking_statuses_cache_metadata")
data class BookingStatusesCacheMetadataEntity(
    @PrimaryKey
    val id: String = "singleton",
    val lastUpdated: Long,
    val totalCount: Int,
    val etag: String? = null
)

// ======================================================
// SERVICE BOOKING ENTITY (NEW)
// ======================================================

@Entity(tableName = "service_bookings")
data class ServiceBookingEntity(
    @PrimaryKey
    val id: String,
    val externalId: String,
    val contractorId: String,
    val clientId: String,
    val serviceId: String?,
    val contractorName: String?,
    val contractorEmail: String?,
    val contractorPhone: String?,
    val clientName: String?,
    val clientEmail: String?,
    val clientPhone: String?,
    val serviceTitle: String?,
    val status: String, // BookingStatus value (PENDING, CONFIRMED, CANCELLED, DECLINED)
    val serviceExecutionStatus: String? = null, // NOT_STARTED, IN_PROGRESS, COMPLETED
    val scheduledDate: String?,
    val locationCity: String?,
    val servicePrice: Double?,
    val servicePriceUnit: String?,
    val serviceDuration: Int?,
    val currency: String,
    val customerNotes: String?,
    val bookingFeeAmount: Double?,
    val bookingFeeCurrency: String?,
    val bookingFeeRefundable: Boolean?,
    val totalAmount: Double?,
    val isNegotiated: Boolean = false,
    val confirmedAt: String?,
    val declinedAt: String?,
    val cancelledAt: String?,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)