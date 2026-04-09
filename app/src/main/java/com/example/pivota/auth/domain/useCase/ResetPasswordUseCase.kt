package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Reset password using verification code
     * @param email User's email
     * @param code OTP verification code
     * @param newPassword New password to set
     * @return ApiResult<String> - Success message or error
     */
    suspend operator fun invoke(email: String, code: String, newPassword: String): ApiResult<String> {
        return when (val result = repository.resetPassword(email, code, newPassword)) {
            is ApiResult.Success -> {
                val response = result.data

                if (response.success) {
                    ApiResult.Success(response.message ?: "Password reset successful")
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown,
                        technicalMessage = response.message ?: "Password reset failed"
                    )
                }
            }
            is ApiResult.Error -> {
                // Pass through the network error (e.g., server unreachable, no internet)
                result
            }
            ApiResult.Loading -> {
                ApiResult.Loading
            }
        }
    }
}