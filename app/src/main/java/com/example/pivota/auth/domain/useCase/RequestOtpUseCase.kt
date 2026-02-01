package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * @param purpose "SIGNUP" or "2FA"
     */
    suspend operator fun invoke(email: String, purpose: String): Result<Unit> {
        return repository.requestOtp(email, purpose)
    }
}