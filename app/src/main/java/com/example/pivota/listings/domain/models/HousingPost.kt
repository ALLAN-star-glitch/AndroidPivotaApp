package com.example.pivota.listings.domain.models

data class HousingPost(
    val images: List<String>,

    val currency: String,
    val price: Int,
    val priceRate: String,

    val title: String,
    val description: String,

    val country: String,
    val city: String,
    val neighbourhood: String,
    val address: String?,

    val bedrooms: Int,
    val bathrooms: Int,
    val furnished: Boolean,
    val type: String,

    val amenities: List<String>,
)