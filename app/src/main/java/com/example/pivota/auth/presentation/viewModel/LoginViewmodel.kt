package com.example.pivota.auth.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.presentation.state.LoginUiState
import com.example.pivota.core.presentations.composables.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    /* ---------------- UI STATE ---------------- */

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /* ---------------- SNACKBAR STATE ---------------- */

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _snackbarType = MutableStateFlow(SnackbarType.INFO)
    val snackbarType: StateFlow<SnackbarType> = _snackbarType.asStateFlow()

    /* ---------------- FORM STATE (survives config changes) ---------------- */

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _agreeTerms = MutableStateFlow(false)
    val agreeTerms: StateFlow<Boolean> = _agreeTerms.asStateFlow()

    /* ---------------- OTP STATE ---------------- */

    private val _otpValues = MutableStateFlow(List(6) { "" })
    val otpValues: StateFlow<List<String>> = _otpValues.asStateFlow()

    private val _resendCount = MutableStateFlow(0)
    val resendCount: StateFlow<Int> = _resendCount.asStateFlow()

    private var pendingEmail: String = ""
    private var pendingPassword: String = ""

    /* ---------------- SNACKBAR ACTIONS ---------------- */

    private fun showSnackbar(message: String, type: SnackbarType = SnackbarType.ERROR) {
        _snackbarMessage.value = message
        _snackbarType.value = type
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    /* ---------------- FORM ACTIONS ---------------- */

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateAgreeTerms(checked: Boolean) {
        _agreeTerms.value = checked
    }

    /* ---------------- LOGIN FLOW ---------------- */

    fun authenticateUser() {
        val email = _email.value
        val password = _password.value

        if (email.isBlank() || password.isBlank()) {
            showSnackbar("Email and password are required", SnackbarType.WARNING)
            return
        }

        if (!_agreeTerms.value) {
            showSnackbar("Please agree to the terms and conditions", SnackbarType.WARNING)
            return
        }

        _uiState.value = LoginUiState.Loading
        pendingEmail = email
        pendingPassword = password

        viewModelScope.launch {
            authUseCases.loginUser(email, password)
                .onSuccess { loginResponse ->
                    when (loginResponse) {
                        is LoginResponse.MfaRequired -> {
                            showSnackbar("Verification code sent to your email", SnackbarType.SUCCESS)
                            // Delay before showing OTP dialog to let snackbar be visible
                            delay(1500)
                            _uiState.value = LoginUiState.OtpSent
                        }
                        is LoginResponse.Authenticated -> {
                            showSnackbar("Login successful! Redirecting...", SnackbarType.SUCCESS)
                            _uiState.value = LoginUiState.Success(loginResponse.user)
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Idle
                    showSnackbar(error.message ?: "Login failed", SnackbarType.ERROR)
                    Log.e("LoginViewModel", "Login error: ${error.message}", error)
                }
        }
    }

    /* ---------------- RESEND OTP ---------------- */

    fun resendOtp() {
        if (pendingEmail.isEmpty() || pendingPassword.isEmpty()) return

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            authUseCases.loginUser(pendingEmail, pendingPassword)
                .onSuccess { loginResponse ->
                    when (loginResponse) {
                        is LoginResponse.MfaRequired -> {
                            _resendCount.update { it + 1 }
                            clearOtp()
                            showSnackbar("New verification code sent!", SnackbarType.SUCCESS)
                            // Delay before showing OTP dialog
                            delay(1500)
                            _uiState.value = LoginUiState.OtpSent
                        }
                        is LoginResponse.Authenticated -> {
                            showSnackbar("Login successful! Redirecting...", SnackbarType.SUCCESS)
                            _uiState.value = LoginUiState.Success(loginResponse.user)
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Idle
                    showSnackbar(error.message ?: "Failed to resend OTP", SnackbarType.ERROR)
                    Log.e("LoginViewModel", "Resend OTP error: ${error.message}", error)
                }
        }
    }

    /* ---------------- MFA VERIFICATION ---------------- */

    fun verifyMfaLogin(code: String) {
        if (code.length < 6) {
            showSnackbar("Please enter the 6-digit verification code", SnackbarType.WARNING)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            authUseCases.verifyMfaLogin(pendingEmail, code)
                .onSuccess { user ->
                    showSnackbar("Verification successful! Redirecting...", SnackbarType.SUCCESS)
                    _uiState.value = LoginUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Idle
                    showSnackbar(error.message ?: "Invalid verification code", SnackbarType.ERROR)
                    Log.e("LoginViewModel", "MFA verification error: ${error.message}", error)
                }
        }
    }

    /* ---------------- OTP INPUT HANDLING ---------------- */

    fun updateOtpDigit(index: Int, value: String) {
        if (index !in 0..5) return

        val current = _otpValues.value.toMutableList()
        current[index] = value
        _otpValues.value = current

        // Auto-verify when all digits are filled
        val fullCode = _otpValues.value.joinToString("")
        if (fullCode.length == 6) {
            verifyMfaLogin(fullCode)
        }
    }

    fun clearOtpDigit(index: Int) {
        if (index !in 0..5) return

        val current = _otpValues.value.toMutableList()
        current[index] = ""
        _otpValues.value = current
    }

    private fun clearOtp() {
        _otpValues.value = List(6) { "" }
    }

    /* ---------------- RESET ---------------- */

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        clearOtp()
        _resendCount.value = 0
        pendingEmail = ""
        pendingPassword = ""
        clearSnackbar()
    }
}