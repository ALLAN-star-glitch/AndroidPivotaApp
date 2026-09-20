package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.TokenRefreshResult
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Refresh the access token using the current refresh token.
     *
     * The repository is responsible for:
     *  - calling the backend,
     *  - unwrapping the response envelope,
     *  - persisting the new tokens via [PivotaDataStore].
     *
     * @param refreshToken Current refresh token.
     * @return [TokenRefreshResult] with the new access/refresh pair, or [ApiResult.Error].
     */
    suspend operator fun invoke(refreshToken: String): ApiResult<TokenRefreshResult> {
        return repository.refreshToken(refreshToken)
    }
}