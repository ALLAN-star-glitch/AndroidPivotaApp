package com.example.pivota.dashboard.domain
data class Listing(
    val id: String,
    val title: String,
    val type: ListingType,      // Jobs / Housing / Services
    val status: ListingFilter,  // Active / Pending / Closed
    val description: String
)
