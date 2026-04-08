package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Refresh access token using refresh token
     * @param refreshToken الحالي
     * @return Result<Pair<String, String>> (accessToken, refreshToken)
     */
    suspend operator fun invoke(refreshToken: String): Result<Pair<String, String>> {
        return try {
            val response = repository.refreshToken(refreshToken)

            if (response.success && response.data != null) {
                val accessToken = response.data.accessToken
                val newRefreshToken = response.data.refreshToken

                Result.success(accessToken to newRefreshToken)
            } else {
                Result.failure(Exception(response.message))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}