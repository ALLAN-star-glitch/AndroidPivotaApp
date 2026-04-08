package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Login user - first stage
     * @param email User's email
     * @param password User's password
     * @return Result<LoginResponse> - MFA required or error
     */
    suspend operator fun invoke(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = repository.login(email, password)

            if (response.success && response.data != null) {
                val data = response.data

                // Check if MFA is required (first stage)
                if (data.message == "MFA_REQUIRED") {
                    Result.success(
                        LoginResponse.MfaRequired(
                            email = email,
                            uuid = data.uuid ?: ""
                        )
                    )
                } else {
                    // This case shouldn't happen as per API flow, but handle it
                    Result.failure(Exception("Invalid login response: ${response.message}"))
                }
            } else {
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}