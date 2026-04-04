package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Request password reset OTP
     * @param email User's email
    @return Result<Unit> - Success or failure
     */
    suspend operator fun invoke(email: String): Result<Unit> {
        return try {
            val response = repository.requestPasswordReset(email)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}