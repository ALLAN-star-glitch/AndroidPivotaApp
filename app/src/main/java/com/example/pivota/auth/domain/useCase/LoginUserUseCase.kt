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
                val response = result.data  // This is LoginResponseDto

                // ✅ CRITICAL: Check the 'success' field from backend
                if (!response.success) {
                    // Backend returned error with 200 status code
                    return ApiResult.Error(
                        networkError = NetworkError.Unauthorized(
                            originalMessage = response.message ?: "Invalid credentials",
                            userFriendlyMessage = response.message ?: "Invalid email or password"
                        ),
                        technicalMessage = response.message
                    )
                }

                // Now handle successful case
                if (response.data != null) {
                    val data = response.data
                    if (data.message == "MFA_REQUIRED") {
                        ApiResult.Success(
                            LoginResponse.MfaRequired(
                                email = email,
                                uuid = data.uuid ?: ""
                            )
                        )
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message ?: "Invalid login response"
                            ),
                            technicalMessage = response.message ?: "Invalid login response"
                        )
                    }
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown(
                            originalMessage = response.message ?: "Login failed"
                        ),
                        technicalMessage = response.message ?: "Login failed"
                    )
                }
            }
            is ApiResult.Error -> {
                // This handles real HTTP errors (non-200 status codes)
                result
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}