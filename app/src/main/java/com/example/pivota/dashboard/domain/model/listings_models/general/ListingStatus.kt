package com.example.pivota.dashboard.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class ListingStatus {
    @SerialName("ACTIVE")
    ACTIVE,

    @SerialName("PENDING")
    PENDING,

    @SerialName("CLOSED")
    CLOSED,

    @SerialName("AVAILABLE")
    AVAILABLE,

    @SerialName("RENTED")
    RENTED,

    @SerialName("INACTIVE")
    INACTIVE,

    @SerialName("SOLD")
    SOLD,

    @SerialName("PAUSED")
    PAUSED,

    @SerialName("REJECTED")
    REJECTED,

    @SerialName("EXPIRED")
    EXPIRED,

    @SerialName("ARCHIVED")
    ARCHIVED,

    @SerialName("DRAFT")
    DRAFT
}