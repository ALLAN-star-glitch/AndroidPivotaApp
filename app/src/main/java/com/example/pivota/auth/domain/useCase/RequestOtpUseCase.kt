package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Request OTP for email verification
     * @param email User's email address
     * @param purpose Purpose of OTP: "EMAIL_VERIFICATION", "LOGIN_2FA", "PASSWORD_RESET", etc.
     * @param phone Optional phone number for signup validation (required for EMAIL_VERIFICATION)
     * @return Result<Unit> - Success or failure
     */
    suspend operator fun invoke(email: String, purpose: String, phone: String? = null): Result<Unit> {
        return try {
            val response = repository.requestOtp(email, purpose, phone)

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