package com.example.pivota.auth.presentation.state

sealed interface SignupUiState {
    object Idle : SignupUiState
    object Loading : SignupUiState
    object OtpSent : SignupUiState
    object Success : SignupUiState
    data class Error(val message: String) : SignupUiState
}

