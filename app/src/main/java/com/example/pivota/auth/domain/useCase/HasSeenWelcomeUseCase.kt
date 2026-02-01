package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case used by the Splash Screen to decide
 * whether to show the Welcome screen.
 */
class HasSeenWelcomeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.hasSeenWelcomeScreen()
    }
}