package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Individual Signup
     * @param user User object containing registration data
     * @param code 6-digit OTP code
     * @param password User's password
     * @return Result<Unit> - Success or failure (no tokens returned, user must login separately)
     */
    suspend fun signupIndividual(user: User, code: String, password: String): Result<Unit> {
        return repository.signupIndividual(user, code, password)
    }
}