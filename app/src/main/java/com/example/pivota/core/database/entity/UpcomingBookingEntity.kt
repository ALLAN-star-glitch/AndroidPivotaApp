package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_bookings")
data class UpcomingBookingEntity(
    @PrimaryKey
    val id: String,
    val contractorId: String,
    val serviceTitle: String? = null,
    val scheduledDate: String? = null,
    val locationCity: String? = null,
    val clientName: String? = null,
    val clientPhone: String? = null,
    val agreedPrice: Double? = null,
    val currency: String,
    val lastUpdated: Long = System.currentTimeMillis()
)