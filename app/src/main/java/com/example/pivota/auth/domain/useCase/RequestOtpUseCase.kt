package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Request OTP for email verification
     * @param email User's email address
     * @param purpose Purpose of OTP: "EMAIL_VERIFICATION", "LOGIN_2FA", "PASSWORD_RESET", etc.
     * @param phone Optional phone number for signup validation
     * @return ApiResult<Unit> - Success or error
     */
    suspend operator fun invoke(email: String, purpose: String, phone: String? = null): ApiResult<Unit> {
        return when (val result = repository.requestOtp(email, purpose, phone)) {
            is ApiResult.Success -> {
                if (result.data.success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Error(
                        networkError = com.example.pivota.core.network.NetworkError.Unknown,
                        technicalMessage = result.data.message
                    )
                }
            }
            is ApiResult.Error -> result
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}