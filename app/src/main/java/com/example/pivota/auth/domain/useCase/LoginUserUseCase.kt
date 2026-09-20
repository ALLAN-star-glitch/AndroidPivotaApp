package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Login user — first stage.
     *
     * @param email    User's email
     * @param password User's password
     * @return [ApiResult.Success] wrapping either:
     *         - [LoginResponse.MfaRequired]   → navigate to MFA screen
     *         - [LoginResponse.Authenticated] → navigate to home
     *         Or [ApiResult.Error] for network / backend failures.
     */
    suspend operator fun invoke(
        email: String,
        password: String
    ): ApiResult<LoginResponse> {
        return repository.login(email, password)
    }
}