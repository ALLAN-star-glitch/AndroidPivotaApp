package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyMfaLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Verify MFA code and complete login
     * @param email User's email
     * @param code MFA verification code
     * @return Result<LoginResponse.Authenticated> - Authenticated user with tokens
     */
    suspend operator fun invoke(email: String, code: String): Result<LoginResponse.Authenticated> {
        return try {
            val response = repository.verifyMfaLogin(email, code)

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

                Result.success(
                    LoginResponse.Authenticated(
                        user = user,
                        message = response.message,
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken ?: ""
                    )
                )
            } else {
                Result.failure(Exception(response.message ?: "MFA verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}