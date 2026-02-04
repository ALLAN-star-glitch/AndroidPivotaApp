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

    /* ---------------- UI STATE ---------------- */

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /* ---------------- OTP STATE ---------------- */

    private val _otpValues = MutableStateFlow(List(6) { "" })
    val otpValues = _otpValues.asStateFlow()

    private val _resendCount = MutableStateFlow(0)
    val resendCount = _resendCount.asStateFlow()

    /* ---------------- LOGIN FLOW ---------------- */

    fun authenticateUser(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.loginUser(email, password)
                .onSuccess {
                    // Password correct → request OTP
                    requestOtp(email)
                }
                .onFailure {
                    _uiState.value = LoginUiState.Error("Failed to login")
                    Log.e("LoginViewModel", it.message ?: "Login error", it)
                }
        }
    }

    /* ---------------- OTP REQUEST ---------------- */

    fun requestOtp(email: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.requestOtp(email, "2FA")
                .onSuccess {
                    _resendCount.value++
                    clearOtp()
                    _uiState.value = LoginUiState.OtpSent
                }
                .onFailure {
                    _uiState.value = LoginUiState.Error("Failed to send OTP")
                    Log.e("LoginViewModel", it.message ?: "OTP request error", it)
                }
        }
    }

    /* ---------------- OTP VERIFICATION ---------------- */

    fun verifyOtp(email: String, code: String) {
        if (code.length < 6) return

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authUseCases.loginWithMfa(email, code, "2FA")
                .onSuccess { user ->
                    _uiState.value = LoginUiState.Success(user)
                }
                .onFailure {
                    _uiState.value = LoginUiState.Error("Verification failed")
                    Log.e("LoginViewModel", it.message ?: "OTP verification error", it)
                }
        }
    }

    /* ---------------- OTP INPUT HANDLING ---------------- */

    fun updateOtpDigit(index: Int, value: String) {
        if (index !in 0..5) return
        _otpValues.value = _otpValues.value.toMutableList().also {
            it[index] = value
        }
    }

    fun clearOtpDigit(index: Int) {
        if (index !in 0..5) return
        _otpValues.value = _otpValues.value.toMutableList().also {
            it[index] = ""
        }
    }

    private fun clearOtp() {
        _otpValues.value = List(6) { "" }
    }

    /* ---------------- RESET ---------------- */

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        clearOtp()
        _resendCount.value = 0
    }
}
