package com.example.pivota.auth.presentation.state

import com.example.pivota.auth.domain.model.User

// In your SignupUiState
sealed class SignupUiState {
    object Idle : SignupUiState()
    object Loading : SignupUiState()
    object OtpSent : SignupUiState()

    data class Success(
        val message: String,
        val redirectTo: String? = null,
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val user: User? = null
    ) : SignupUiState()

    data class PaymentRequired(
        val message: String,
        val redirectUrl: String,
        val merchantReference: String?
    ) : SignupUiState()

    data class Error(val message: String) : SignupUiState()
}