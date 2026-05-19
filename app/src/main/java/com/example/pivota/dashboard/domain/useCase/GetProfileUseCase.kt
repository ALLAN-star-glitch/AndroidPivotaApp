package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.profile_models.CompleteProfile
import com.example.pivota.dashboard.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    /**
     * Fetch complete user profile
     * Returns account information, user details, purpose-specific profiles, verifications, and completion status
     *
     * @return ApiResult<CompleteProfile> - Contains all profile data if successful
     */
    suspend operator fun invoke(): ApiResult<CompleteProfile> {
        return when (val result = repository.fetchProfile()) {
            is ApiResult.Success -> {
                ApiResult.Success(result.data)
            }
            is ApiResult.Error -> {
                // Pass through the network error (e.g., server unreachable, no internet, unauthorized)
                ApiResult.Error(
                    networkError = result.networkError,
                    technicalMessage = result.technicalMessage
                )
            }
            ApiResult.Loading -> {
                ApiResult.Loading
            }
        }
    }
}