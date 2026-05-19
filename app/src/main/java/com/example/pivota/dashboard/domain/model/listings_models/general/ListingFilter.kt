package com.example.pivota.dashboard.domain

import kotlinx.serialization.Serializable

@Serializable
enum class ListingFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    PENDING("Pending"),
    CLOSED("Closed")
}