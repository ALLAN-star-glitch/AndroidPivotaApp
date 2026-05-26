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
    val verticals: String, // Store as JSON string
    val basePrice: Double,
    val priceUnit: String,
    val currency: String,
    val locationCity: String,
    val locationNeighborhood: String?,
    val availability: String?, // Store as JSON string
    val yearsExperience: Int,
    val hourlyRate: Double,
    val serviceAreas: String, // Store as JSON string
    val status: String,
    val averageRating: Double,
    val reviewCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "service_offerings_cache_metadata")
data class ServiceOfferingsCacheMetadataEntity(
    @PrimaryKey
    val categoryId: String,
    val lastUpdated: Long,
    val totalCount: Int,
    val etag: String? = null
)