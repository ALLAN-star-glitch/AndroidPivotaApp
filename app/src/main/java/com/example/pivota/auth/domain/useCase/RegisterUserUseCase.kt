package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository

class RegisterUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User): Result<Unit> {
        // Business logic: Validation or role assignment logic starts here
        return repository.saveUser(user)
    }
}