package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Logout user and clear local session
     * @param refreshToken User's refresh token
     * @return Result<Unit>
     */
    suspend operator fun invoke(refreshToken: String): Result<Unit> {
        return try {
            val response = repository.logout(refreshToken)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}