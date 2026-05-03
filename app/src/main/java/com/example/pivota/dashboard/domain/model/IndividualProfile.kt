package com.example.pivota.dashboard.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IndividualProfile(
    val bio: String?,
    val gender: String?,
    val dateOfBirth: String?,
    val nationalId: String?,
    val profileImage: String?
) {
    val hasBio: Boolean get() = !bio.isNullOrBlank()
    val hasGender: Boolean get() = !gender.isNullOrBlank()
    val hasDateOfBirth: Boolean get() = !dateOfBirth.isNullOrBlank()
    val hasNationalId: Boolean get() = !nationalId.isNullOrBlank()
    val completionPercentage: Int get() = listOf(
        hasBio, hasGender, hasDateOfBirth, hasNationalId
    ).count { it } * 25
}