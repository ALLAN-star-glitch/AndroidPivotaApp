package com.example.pivota.auth.presentation.state

import com.example.pivota.auth.domain.model.User

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    object OtpSent : LoginUiState
    object PasswordResetOtpSent : LoginUiState
    data class PasswordResetSuccess(val message: String) : LoginUiState
    data class Success(
        val user: User,
        val message: String,
        val accessToken: String,
        val refreshToken: String
    ) : LoginUiState
    data class Error(val message: String) : LoginUiState
}