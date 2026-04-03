package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(refreshToken: String): Result<Unit> {
        return repository.logout(refreshToken)
    }
}