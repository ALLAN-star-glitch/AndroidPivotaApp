package com.example.pivota.dashboard.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class ListingType {
    @SerialName("JOBS")
    JOBS,

    @SerialName("HOUSING")
    HOUSING,

    @SerialName("SERVICES")
    SERVICES
}