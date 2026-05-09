package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.profile_models.CompleteProfile

sealed class DashboardState {
    data object Loading : DashboardState()
    data class Success(val profile: CompleteProfile) : DashboardState()
    data class Error(
        val message: String,
        val isRecoverable: Boolean = true
    ) : DashboardState()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}