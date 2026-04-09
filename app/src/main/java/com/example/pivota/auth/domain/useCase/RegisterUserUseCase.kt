package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.data.remote.dto.SignupSuccessDataDto
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Individual Signup
     * @param user User object containing registration data
     * @param code 6-digit OTP code
     * @param password User's password
     * @return ApiResult<SignupSuccessDataDto> - Contains message, tokens, and redirect info
     */
    suspend operator fun invoke(
        user: User,
        code: String,
        password: String
    ): ApiResult<SignupSuccessDataDto> {
        return when (val result = repository.signupIndividual(user, code, password)) {
            is ApiResult.Success -> {
                val response = result.data

                if (response.success && response.data != null) {
                    ApiResult.Success(response.data)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown,
                        technicalMessage = response.message ?: "Signup failed"
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