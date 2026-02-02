package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.AccountType
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

    // Helper for navigation to OTP Screen
    var pendingEmail: String = ""
        private set

    /**
     * Step 1: Initiates signup by caching data and requesting an OTP.
     */
    fun startSignup(
        email: String,
        password: String,
        phone: String,
        isOrganization: Boolean,
        firstName: String = "",
        lastName: String = "",
        organization: AccountType.Organization? = null
    ) {
        pendingPassword = password
        pendingIsOrganization = isOrganization
        pendingEmail = email // Store for the Verification screen navigation

        pendingUser = if (isOrganization && organization != null) {
            User(
                uuid = "",
                accountUuid = "",
                firstName = organization.adminFirstName,
                lastName = organization.adminLastName,
                email = organization.orgEmail,
                personalPhone = organization.orgPhone ?: "",
                accountType = organization // AccountType.Organization is already the type here
            )
        } else {
            User(
                uuid = "",
                accountUuid = "",
                firstName = firstName,
                lastName = lastName,
                email = email,
                personalPhone = phone,
                accountType = AccountType.Individual
            )
        }

        requestSignupOtp(email)
    }

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
            }.onFailure {
                _uiState.value = SignupUiState.Error(it.message ?: "Signup failed")
            }
        }
    }

    private fun clearCache() {
        pendingUser = null
        pendingPassword = ""
        pendingIsOrganization = false
        // Note: We don't clear pendingEmail yet because the OTP screen needs it
    }

    fun resetState() {
        _uiState.value = SignupUiState.Idle
    }
}