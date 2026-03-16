package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.ListingStatus

data class HousingListingUiModel(
    val id: String,
    val price: String,
    val title: String,
    val location: String,
    val propertyType: String,
    val rating: Double,
    val isVerified: Boolean,
    val description: String,
    val isForSale: Boolean,
    val imageRes: Int?,
    val bedrooms: Int,
    val bathrooms: Int,
    val squareMeters: Int,
    val status: ListingStatus,
    val views: Int,
    val messages: Int,
    val requests: Int
)