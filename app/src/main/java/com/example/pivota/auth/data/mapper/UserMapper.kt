package com.example.pivota.auth.data.mapper

import com.example.pivota.auth.data.local.UserEntity
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.model.UserRole

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name, // Map real name to real name
        isOrganization = this.isOrganization,
        // Safe conversion: defaults to INDIVIDUAL if the string is weird
        role = try {
            UserRole.valueOf(this.role)
        } catch (e: Exception) {
            UserRole.INDIVIDUAL
        }
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        email = this.email,
        name = this.name, // Use the user's actual name here
        isOrganization = this.isOrganization,
        role = this.role.name // Enum to String
    )
}