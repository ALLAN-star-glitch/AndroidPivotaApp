package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.SignupResult
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Individual signup.
     *
     * The repository is responsible for:
     *  - calling the backend,
     *  - unwrapping the response envelope,
     *  - deciding the branch (auto-login / payment required / manual login)
     *    and persisting user state accordingly.
     *
     * @param user     Registration data.
     * @param code     6-digit OTP code.
     * @param password User's password.
     * @return [SignupResult] with message, optional tokens/redirect, or [ApiResult.Error].
     */
    suspend operator fun invoke(
        user: User,
        code: String,
        password: String
    ): ApiResult<SignupResult> {
        return repository.signupIndividual(user, code, password)
    }
}