package com.example.pivota.dashboard.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class EmployerType {
    @SerialName("INDIVIDUAL")
    INDIVIDUAL,

    @SerialName("ORGANIZATION")
    ORGANIZATION
}