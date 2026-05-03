package com.example.pivota.dashboard.domain.model

import com.example.pivota.dashboard.domain.ListingFilter
import com.example.pivota.dashboard.domain.ListingType

data class Listing(
    val id: String,
    val title: String,
    val type: ListingType,      // Jobs / Housing / Services
    val status: ListingFilter,  // Active / Pending / Closed
    val description: String
)