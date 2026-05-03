package com.example.pivota.dashboard.domain.model

data class ProfileUser(
    val id: String,
    val userCode: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val status: UserStatus,
    val role: String
) {
    val displayName: String get() = if (firstName.isNotBlank()) "$firstName $lastName".trim() else fullName
    val shortName: String get() = if (firstName.isNotBlank()) firstName else email.substringBefore("@")
}

enum class UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, PENDING;

    companion object {
        fun fromString(value: String): UserStatus = when (value.uppercase()) {
            "ACTIVE" -> ACTIVE
            "INACTIVE" -> INACTIVE
            "SUSPENDED" -> SUSPENDED
            "PENDING" -> PENDING
            else -> ACTIVE
        }
    }
}