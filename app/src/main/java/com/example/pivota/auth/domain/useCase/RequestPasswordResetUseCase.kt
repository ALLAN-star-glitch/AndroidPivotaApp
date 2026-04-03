package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.requestPasswordReset(email)
    }
}