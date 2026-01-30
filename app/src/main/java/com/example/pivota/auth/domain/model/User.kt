package com.example.pivota.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val isOrganization: Boolean,
    val role: UserRole
)

enum class UserRole {
    ADMIN, MEMBER, INDIVIDUAL
}