package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.OtpRequestResult
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Request a password reset OTP for the given email.
     *
     * The repository is responsible for calling the backend and unwrapping the response
     * envelope — a failed envelope is surfaced as [ApiResult.Error] by `safeApiCall`.
     *
     * @param email User's email.
     * @return [OtpRequestResult] on success, [ApiResult.Error] otherwise.
     */
    suspend operator fun invoke(email: String): ApiResult<OtpRequestResult> {
        return repository.requestPasswordReset(email)
    }
}