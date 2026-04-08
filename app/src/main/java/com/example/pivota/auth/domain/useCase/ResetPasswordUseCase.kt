package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Reset password using verification code
     * @param email User's email
     * @param code OTP verification code
     * @param newPassword New password to set
     * @return Result<String> - Success message or error
     */
    suspend operator fun invoke(email: String, code: String, newPassword: String): Result<String> {
        return try {
            val response = repository.resetPassword(email, code, newPassword)

            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}