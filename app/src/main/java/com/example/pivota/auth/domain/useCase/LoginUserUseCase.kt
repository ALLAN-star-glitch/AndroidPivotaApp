package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Login user - first stage
     * @param email User's email
     * @param password User's password
     * @return ApiResult<LoginResponse> - MFA required or error
     */
    suspend operator fun invoke(email: String, password: String): ApiResult<LoginResponse> {
        return when (val result = repository.login(email, password)) {
            is ApiResult.Success -> {
                val response = result.data

                if (response.success && response.data != null) {
                    val data = response.data

                    // Check if MFA is required (first stage)
                    if (data.message == "MFA_REQUIRED") {
                        ApiResult.Success(
                            LoginResponse.MfaRequired(
                                email = email,
                                uuid = data.uuid ?: ""
                            )
                        )
                    } else {
                        // This case shouldn't happen as per API flow, but handle it
                        ApiResult.Error(
                            networkError = NetworkError.Unknown,
                            technicalMessage = "Invalid login response: ${response.message}"
                        )
                    }
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown,
                        technicalMessage = response.message ?: "Login failed"
                    )
                }
            }
            is ApiResult.Error -> {
                // Pass through the network error
                result
            }
            ApiResult.Loading -> {
                ApiResult.Loading
            }
        }
    }
}