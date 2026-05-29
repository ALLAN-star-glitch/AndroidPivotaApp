package com.example.pivota.dashboard.domain.model.listings_models.professionals

data class PricingUnitsByCategory(
    val categoryId: String,
    val categoryName: String,
    val vertical: String,
    val allowedUnits: List<PricingUnitOption>
)

data class PricingUnitOption(
    val unit: String,
    val label: String,
    val description: String,
    val minPrice: Double,
    val maxPrice: Double?,
    val experienceRequired: Boolean,
    val notesRequired: Boolean,
    val currency: String
)