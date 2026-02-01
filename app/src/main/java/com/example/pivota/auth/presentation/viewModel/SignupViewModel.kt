package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.presentation.state.SignupUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var pendingUser: User? = null
    private var pendingPassword = ""
    private var pendingIsOrganization = false

    /**
     * Step 1: Initiates signup by caching data and requesting an OTP.
     */
    fun startSignup(user: User, pass: String, isOrg: Boolean) {
        pendingUser = user
        pendingPassword = pass
        pendingIsOrganization = isOrg

        // Trigger the initial OTP request
        requestSignupOtp(user.email)
    }

    /**
     * Can be called for the initial request OR for "Resend OTP"
     * from the Verification Screen.
     */
    fun requestSignupOtp(email: String) {
        _uiState.value = SignupUiState.Loading
        viewModelScope.launch {
            authUseCases.requestOtp(email, "SIGNUP")
                .onSuccess {
                    _uiState.value = SignupUiState.OtpSent
                }
                .onFailure {
                    _uiState.value = SignupUiState.Error(it.message ?: "Failed to send OTP")
                }
        }
    }

    /**
     * Step 2: Finalizes registration.
     */
    fun verifyAndRegister(code: String) {
        val user = pendingUser ?: run {
            _uiState.value = SignupUiState.Error("Session expired. Please register again.")
            return
        }

        _uiState.value = SignupUiState.Loading
        viewModelScope.launch {
            val result = if (pendingIsOrganization) {
                authUseCases.registerUser.signupOrganization(user, code, pendingPassword)
            } else {
                authUseCases.registerUser.signupIndividual(user, code, pendingPassword)
            }

            result.onSuccess {
                clearCache()
                _uiState.value = SignupUiState.Success
            }
                .onFailure {
                    _uiState.value = SignupUiState.Error(it.message ?: "Signup failed")
                }
        }
    }

    private fun clearCache() {
        pendingUser = null
        pendingPassword = ""
        pendingIsOrganization = false
    }

    fun resetState() {
        _uiState.value = SignupUiState.Idle
    }
}