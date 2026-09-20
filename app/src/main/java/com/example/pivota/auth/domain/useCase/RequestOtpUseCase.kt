package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.OtpRequestResult
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Request an OTP for email verification.
     *
     * The repository is responsible for calling the backend and unwrapping the
     * response envelope; a failed envelope surfaces as [ApiResult.Error].
     *
     * @param email   User's email address.
     * @param purpose Purpose of OTP: "EMAIL_VERIFICATION", "LOGIN_2FA", "PASSWORD_RESET", etc.
     * @param phone   Optional phone number for signup validation.
     * @return [OtpRequestResult] on success, [ApiResult.Error] otherwise.
     */
    suspend operator fun invoke(
        email: String,
        purpose: String,
        phone: String? = null
    ): ApiResult<OtpRequestResult> {
        return repository.requestOtp(email, purpose, phone)
    }
}