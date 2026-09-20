package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.PasswordResetResult
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Reset the user's password using the OTP code sent to their email.
     *
     * The repository is responsible for calling the backend and unwrapping the
     * response envelope; a failed envelope surfaces as [ApiResult.Error].
     *
     * @param email       User's email.
     * @param code        OTP verification code.
     * @param newPassword New password to set.
     * @return [PasswordResetResult] on success, [ApiResult.Error] otherwise.
     */
    suspend operator fun invoke(
        email: String,
        code: String,
        newPassword: String
    ): ApiResult<PasswordResetResult> {
        return repository.resetPassword(email, code, newPassword)
    }
}