package com.example.pivota.dashboard.domain.model.listings_models.general

import kotlinx.serialization.Serializable

@Serializable
data class ListingCategory(
    val id: String,
    val label: String, // "Job", "Housing", "Service"
)