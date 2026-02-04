package com.example.pivota.auth.presentation.viewModel


import android.util.Log
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

    fun authenticateUser(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.loginUser(email, password)
                .onSuccess { _ -> _uiState.value = LoginUiState.OtpSent }
                .onFailure {
                    _uiState.value = LoginUiState.Error("Failed to login")
                    Log.d("Error during signup: ", it.message ?: "Error", it)
                }
        }
    }

    // Stage 1: Request 2FA OTP
    fun requestOtp(email: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.requestOtp(email, "2FA")
                .onSuccess { _uiState.value = LoginUiState.OtpSent }
                .onFailure {
                    _uiState.value = LoginUiState.Error("Request failed")
                    Log.d("Error during signup: ", it.message ?: "Error", it)
                }
        }
    }

    // Stage 2: Verify and Login
    fun verifyOtp(email: String, code: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.loginWithMfa(email, code, "2FA")
                .onSuccess { user -> _uiState.value = LoginUiState.Success(user) }
                .onFailure {
                    _uiState.value = LoginUiState.Error("Verification failed")
                    Log.d("Error during signup: ", it.message ?: "Error", it)
                }
        }
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }
}
