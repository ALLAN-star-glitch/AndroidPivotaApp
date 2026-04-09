package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Logout user and clear local session
     * @param refreshToken User's refresh token
     * @return ApiResult<Unit> - Success or error
     */
    suspend operator fun invoke(refreshToken: String): ApiResult<Unit> {
        return when (val result = repository.logout(refreshToken)) {
            is ApiResult.Success -> {
                val response = result.data

                if (response.success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown,
                        technicalMessage = response.message ?: "Logout failed"
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