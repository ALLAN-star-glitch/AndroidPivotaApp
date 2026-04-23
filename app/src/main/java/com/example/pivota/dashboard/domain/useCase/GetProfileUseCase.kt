package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.auth.domain.model.CompleteProfileResult
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.dashboard.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository  // Changed from AuthRepository to ProfileRepository
) {
    /**
     * Fetch complete user profile
     * Returns account information, user details, purpose-specific profiles, verifications, and completion status
     *
     * @return ApiResult<CompleteProfileResult> - Contains all profile data if successful
     */
    suspend operator fun invoke(): ApiResult<CompleteProfileResult> {
        return when (val result = repository.fetchProfile()) {  // Now using ProfileRepository
            is ApiResult.Success -> {
                val profile = result.data
                ApiResult.Success(profile)
            }
            is ApiResult.Error -> {
                // Pass through the network error (e.g., server unreachable, no internet, unauthorized)
                result
            }
            ApiResult.Loading -> {
                ApiResult.Loading
            }
        }
    }
}