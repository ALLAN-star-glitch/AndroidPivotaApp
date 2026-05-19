package com.example.pivota.dashboard.domain.model.profile_models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class ProfessionalType {
    @SerialName("ORGANIZATION")
    ORGANIZATION,

    @SerialName("INDIVIDUAL")
    INDIVIDUAL
}