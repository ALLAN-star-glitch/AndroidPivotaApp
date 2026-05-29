package com.example.pivota.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ======================================================
// PRICING UNITS BY CATEGORY RESPONSE DTO
// ======================================================

@Serializable
data class PricingUnitsByCategoryResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: Int,
    @SerialName("data") val data: PricingUnitsByCategoryDataDto? = null,
    @SerialName("status") val status: String
)

@Serializable
data class PricingUnitsByCategoryDataDto(
    @SerialName("categoryId") val categoryId: String,
    @SerialName("categoryName") val categoryName: String,
    @SerialName("vertical") val vertical: String,
    @SerialName("allowedUnits") val allowedUnits: List<PricingUnitOptionDto>
)

@Serializable
data class PricingUnitOptionDto(
    @SerialName("unit") val unit: String,
    @SerialName("label") val label: String,
    @SerialName("description") val description: String,
    @SerialName("minPrice") val minPrice: Double,
    @SerialName("maxPrice") val maxPrice: Double? = null,
    @SerialName("experienceRequired") val experienceRequired: Boolean,
    @SerialName("notesRequired") val notesRequired: Boolean,
    @SerialName("currency") val currency: String
)