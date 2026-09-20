package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Logout user and clear local session.
     *
     * The repository is responsible for:
     *  - calling the backend to invalidate the refresh token,
     *  - clearing local tokens and Room data — even if the API call fails.
     *
     * @param refreshToken User's refresh token.
     * @return [ApiResult.Success] on completion, [ApiResult.Error] otherwise.
     */
    suspend operator fun invoke(refreshToken: String): ApiResult<Unit> {
        return repository.logout(refreshToken)
    }
}