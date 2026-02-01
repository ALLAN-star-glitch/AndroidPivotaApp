package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case to mark the Welcome screen as seen.
 * Triggered when clicking "Get Started" or "Login".
 */
class SetWelcomeSeenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.setWelcomeScreenSeen()
    }
}