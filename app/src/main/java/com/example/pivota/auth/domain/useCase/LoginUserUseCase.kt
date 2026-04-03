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
     * @return Result<LoginResponse> - MFA required or authenticated with tokens
     */
    suspend operator fun invoke(email: String, password: String): Result<LoginResponse> {
        return repository.login(email, password)
    }
}