package com.example.pivota.listings.domain.models

data class Service(
    val postedBy: String, // User name

    val title: String,
    val category: String,
    val description: String,
    val type: String,
    var basePrice: Double,
    val currency: String,
    val priceUnit: String,
    val yearsOfExperience: Int,
    val availability: String,
    val city: String,
    val neighbourhood: String?,
    val images: List<String>,
    val listing: String,
    val additionalNotes: String,
)