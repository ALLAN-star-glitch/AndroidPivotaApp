package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class VerifyMfaLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Verify the MFA code and complete login.
     *
     * The repository is responsible for:
     *  - calling the backend,
     *  - unwrapping the response envelope,
     *  - mapping the DTO to [LoginResponse],
     *  - persisting the authenticated user (only when [LoginResponse.Authenticated]).
     *
     * @param email User's email.
     * @param code  MFA verification code.
     * @return [LoginResponse.Authenticated] on success, [ApiResult.Error] otherwise.
     */
    suspend operator fun invoke(
        email: String,
        code: String
    ): ApiResult<LoginResponse> {
        return repository.verifyMfaLogin(email, code)
    }
}