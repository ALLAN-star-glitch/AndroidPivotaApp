package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.data.remote.dto.SignupSuccessDataDto
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Individual Signup
     * @param user User object containing registration data
     * @param code 6-digit OTP code
     * @param password User's password
     * @return Result<SignupSuccessDataDto> - Contains message, tokens, and redirect info
     */
    suspend operator fun invoke(
        user: User,
        code: String,
        password: String
    ): Result<SignupSuccessDataDto> {
        return try {
            val response = repository.signupIndividual(user, code, password)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}