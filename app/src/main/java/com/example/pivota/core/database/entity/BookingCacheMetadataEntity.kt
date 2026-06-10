package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_cache_metadata")
data class BookingCacheMetadataEntity(
    @PrimaryKey
    val bookingId: String,  // Can be: actual booking ID, cache key for lists (e.g., "customer:clientId:status"), "booking_statuses", etc.
    val lastUpdated: Long,
    val totalCount: Int = 0  // For paginated lists
)