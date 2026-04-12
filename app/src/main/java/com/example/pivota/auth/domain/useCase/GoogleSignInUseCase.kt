package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.network.ApiResult
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Sign in or register using Google OAuth token
     *
     * @param idToken The Google ID token from the client
     * @param onboardingData Optional onboarding data collected from previous screens
     *                       (primaryPurpose, jobSeekerData, housingSeekerData, etc.)
     * @return ApiResult<LoginResponse> - Success with user data or error
     */
    suspend operator fun invoke(
        idToken: String,
        onboardingData: Map<String, Any?>? = null
    ): ApiResult<LoginResponse> {
        return repository.googleSignIn(idToken, onboardingData)
    }
}