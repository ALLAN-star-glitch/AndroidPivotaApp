package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import javax.inject.Inject

class VerifyMfaLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Verify MFA code and complete login
     * @param email User's email
     * @param code MFA verification code
     * @return ApiResult<LoginResponse.Authenticated> - Authenticated user with tokens
     */
    suspend operator fun invoke(email: String, code: String): ApiResult<LoginResponse.Authenticated> {
        return when (val result = repository.verifyMfaLogin(email, code)) {
            is ApiResult.Success -> {
                val response = result.data

                if (response.success && response.data != null && response.data.accessToken != null) {
                    val data = response.data

                    // Note: The complete User object with all JWT fields is already saved in the repository
                    // For now, create a basic user since the repository handles the full save
                    val user = User(
                        email = email,
                        isAuthenticated = true,
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken
                    )

                    ApiResult.Success(
                        LoginResponse.Authenticated(
                            user = user,
                            message = response.message,
                            accessToken = data.accessToken,
                            refreshToken = data.refreshToken ?: ""
                        )
                    )
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown,
                        technicalMessage = response.message ?: "MFA verification failed"
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