package com.example.pivota.auth.presentation.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.presentation.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Stage 1: Request 2FA OTP
    fun requestOtp(email: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.requestOtp(email, "2FA")
                .onSuccess { _uiState.value = LoginUiState.OtpSent }
                .onFailure { _uiState.value = LoginUiState.Error(it.message ?: "Request failed") }
        }
    }

    // Stage 2: Verify and Login
    fun verifyOtp(email: String, code: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.loginWithMfa(email, code)
                .onSuccess { user -> _uiState.value = LoginUiState.Success(user) }
                .onFailure { _uiState.value = LoginUiState.Error(it.message ?: "Verification failed") }
        }
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }
}
