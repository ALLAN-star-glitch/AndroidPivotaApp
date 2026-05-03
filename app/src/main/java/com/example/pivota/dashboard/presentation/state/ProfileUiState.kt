package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.CompleteProfile

/**
 * UI state for the Profile screen
 */
sealed class ProfileUiState {
    /**
     * Loading state - profile data is being fetched
     */
    data object Loading : ProfileUiState()

    /**
     * Success state - profile data loaded successfully
     * @param profile The complete profile data from the API
     */
    data class Success(
        val profile: CompleteProfile
    ) : ProfileUiState()

    /**
     * Error state - failed to load profile
     * @param message User-friendly error message
     * @param technicalMessage Technical error details (for logging)
     * @param isRecoverable Whether the error can be retried
     */
    data class Error(
        val message: String,
        val technicalMessage: String? = null,
        val isRecoverable: Boolean = true
    ) : ProfileUiState()

    /**
     * Helper to check if state is loading
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Helper to check if state is success
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Helper to check if state is error
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Get profile data if state is Success
     */
    val profileData: CompleteProfile?
        get() = when (this) {
            is Success -> profile
            else -> null
        }

    /**
     * Get error message if state is Error
     */
    val errorMessage: String?
        get() = when (this) {
            is Error -> message
            else -> null
        }
}