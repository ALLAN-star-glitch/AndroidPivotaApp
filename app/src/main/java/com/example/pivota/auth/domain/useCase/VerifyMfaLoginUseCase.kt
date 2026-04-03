package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyMfaLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Verify MFA code and complete login
     * @param email User's email
     * @param code MFA/2FA code
     * @return Result<User> - Authenticated user with tokens
     */
    suspend operator fun invoke(email: String, code: String): Result<User> {
        return repository.verifyMfaLogin(email, code).onSuccess { user ->
            repository.saveAuthenticatedUser(user)
        }
    }
}