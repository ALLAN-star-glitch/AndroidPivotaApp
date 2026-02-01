package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithMfaUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String): Result<User> {
        return repository.loginWithMfa(email, code).onSuccess { user ->
            // Once the backend confirms the 2FA code is valid and returns the user/tokens,
            // we persist them to the local Room database and DataStore.
            repository.saveAuthenticatedUser(user)
        }
    }
}