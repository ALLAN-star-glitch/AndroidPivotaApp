package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_stats")
data class BookingStatsEntity(
    @PrimaryKey
    val contractorId: String,
    val total: Int,
    val pending: Int,
    val confirmed: Int,
    val completed: Int,
    val cancelled: Int,
    val declined: Int,
    val upcoming: Int,
    val completedThisMonth: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)