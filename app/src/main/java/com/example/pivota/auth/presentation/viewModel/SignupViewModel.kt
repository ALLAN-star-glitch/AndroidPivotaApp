package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.AccountType
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.presentation.state.SignupUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

// --- Data class for storing form fields ---
data class RegistrationFormState(
    val accountType: String = "Individual",

    // Individual
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",

    // Organisation
    val orgName: String = "",
    val orgType: String = "",
    val orgEmail: String = "",
    val orgPhone: String = "",
    val orgAddress: String = "",
    val adminFirstName: String = "",
    val adminLastName: String = "",

    // Shared
    val password: String = "",
    val agreeTerms: Boolean = false
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    // --- UI state for loading, error, success ---
    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    // --- Form state for preserving fields ---
    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()

    // --- Pending registration data for OTP ---
    private var pendingUser: User? = null
    private var pendingPassword = ""
    private var pendingIsOrganization = false
    var pendingEmail: String = ""
        private set

    // --- Form field updaters ---
    fun updateAccountType(value: String) = updateForm { copy(accountType = value) }

    fun updateFirstName(value: String) = updateForm { copy(firstName = value) }
    fun updateLastName(value: String) = updateForm { copy(lastName = value) }
    fun updateEmail(value: String) = updateForm { copy(email = value) }
    fun updatePhone(value: String) = updateForm { copy(phone = value) }

    fun updateOrgName(value: String) = updateForm { copy(orgName = value) }
    fun updateOrgType(value: String) = updateForm { copy(orgType = value) }
    fun updateOrgEmail(value: String) = updateForm { copy(orgEmail = value) }
    fun updateOrgPhone(value: String) = updateForm { copy(orgPhone = value) }
    fun updateOrgAddress(value: String) = updateForm { copy(orgAddress = value) }
    fun updateAdminFirstName(value: String) = updateForm { copy(adminFirstName = value) }
    fun updateAdminLastName(value: String) = updateForm { copy(adminLastName = value) }

    fun updatePassword(value: String) = updateForm { copy(password = value) }
    fun updateAgreeTerms(value: Boolean) = updateForm { copy(agreeTerms = value) }

    private inline fun updateForm(block: RegistrationFormState.() -> RegistrationFormState) {
        _formState.value = _formState.value.block()
    }

    // --- Regex Validation helpers ---
    fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}$"
        return Pattern.matches(emailPattern, email.trim())
    }

    fun isPasswordValid(password: String): Boolean {
        return password.trim().length >= 6
    }

    // --- Start signup (OTP request) ---
    fun startSignup() {
        val form = _formState.value

        // Validate form before requesting OTP
        val errorMessage = when {
            !form.agreeTerms -> "You must agree to the terms."
            !isPasswordValid(form.password) -> "Password must be at least 6 characters."
            form.accountType == "Individual" && form.firstName.isBlank() -> "First name is required."
            form.accountType == "Individual" && form.lastName.isBlank() -> "Last name is required."
            form.accountType == "Individual" && !isEmailValid(form.email) -> "Invalid email."
            form.accountType == "Organisation" && form.orgName.isBlank() -> "Organisation name is required."
            form.accountType == "Organisation" && !isEmailValid(form.orgEmail) -> "Invalid organisation email."
            else -> null
        }

        if (errorMessage != null) {
            _uiState.value = SignupUiState.Error(errorMessage)
            return
        }

        val email = if (form.accountType == "Organisation") form.orgEmail.trim() else form.email.trim()
        val password = form.password.trim()
        val phone = if (form.accountType == "Organisation") form.orgPhone.trim() else form.phone.trim()

        pendingPassword = password
        pendingIsOrganization = form.accountType == "Organisation"
        pendingEmail = email

        pendingUser = if (pendingIsOrganization) {
            User(
                uuid = "",
                accountUuid = "",
                firstName = form.adminFirstName.trim(),
                lastName = form.adminLastName.trim(),
                email = email,
                personalPhone = phone,
                accountType = AccountType.Organization(
                    orgUuid = "",
                    orgName = form.orgName.trim(),
                    orgType = form.orgType.trim(),
                    orgEmail = email,
                    orgPhone = phone,
                    orgAddress = form.orgAddress.trim(),
                    adminFirstName = form.adminFirstName.trim(),
                    adminLastName = form.adminLastName.trim()
                )
            )
        } else {
            User(
                uuid = "",
                accountUuid = "",
                firstName = form.firstName.trim(),
                lastName = form.lastName.trim(),
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
                .onSuccess { _uiState.value = SignupUiState.OtpSent }
                .onFailure { _uiState.value = SignupUiState.Error(it.message ?: "Failed to send OTP") }
        }
    }

    fun verifyAndRegister(code: String) {
        val user = pendingUser ?: run {
            _uiState.value = SignupUiState.Error("Session expired. Please register again.")
            return
        }

        _uiState.value = SignupUiState.Loading
        viewModelScope.launch {
            val trimmedCode = code.trim()
            val result = if (pendingIsOrganization) {
                authUseCases.registerUser.signupOrganization(user, trimmedCode, pendingPassword)
            } else {
                authUseCases.registerUser.signupIndividual(user, trimmedCode, pendingPassword)
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
    }

    fun resetState() {
        _uiState.value = SignupUiState.Idle
    }
}
