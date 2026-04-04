package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Request OTP for email verification
     * @param email User's email address
     * @param purpose Purpose of OTP: "SIGNUP" or "2FA" or "PASSWORD_RESET"
     * @return Result<Unit> - Success or failure
     */
    suspend operator fun invoke(email: String, purpose: String): Result<Unit> {
        return try {
            val response = repository.requestOtp(email, purpose)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to send OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}