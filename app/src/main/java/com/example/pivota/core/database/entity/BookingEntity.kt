package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String,
    val externalId: String,
    val contractorId: String,
    val clientId: String,
    val serviceId: String? = null,
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
    // Cache metadata
    val lastUpdated: Long = System.currentTimeMillis(),
    val categoryId: String? = null  // For filtering by category if needed
)