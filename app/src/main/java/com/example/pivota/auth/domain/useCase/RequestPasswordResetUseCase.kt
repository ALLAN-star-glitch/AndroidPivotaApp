package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Request password reset OTP
     * @param email User's email
     * @return ApiResult<Unit> - Success or error
     */
    suspend operator fun invoke(email: String): ApiResult<Unit> {
        return when (val result = repository.requestPasswordReset(email)) {
            is ApiResult.Success -> {
                val response = result.data

                if (response.success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown,
                        technicalMessage = response.message ?: "Password reset request failed"
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