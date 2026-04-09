package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Refresh access token using refresh token
     * @param refreshToken Current refresh token
     * @return ApiResult<Pair<String, String>> (accessToken, refreshToken)
     */
    suspend operator fun invoke(refreshToken: String): ApiResult<Pair<String, String>> {
        return when (val result = repository.refreshToken(refreshToken)) {
            is ApiResult.Success -> {
                val response = result.data

                if (response.success && response.data != null) {
                    val accessToken = response.data.accessToken
                    val newRefreshToken = response.data.refreshToken

                    ApiResult.Success(accessToken to newRefreshToken)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown,
                        technicalMessage = response.message ?: "Token refresh failed"
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