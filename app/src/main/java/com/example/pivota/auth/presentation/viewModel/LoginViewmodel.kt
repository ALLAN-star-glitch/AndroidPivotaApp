package com.example.pivota.auth.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.auth.presentation.state.LoginUiState
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.preferences.PivotaDataStore
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
    private val authRepository: AuthRepository,
    private val datastore: PivotaDataStore,
    private val userDao: UserDao
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _agreeTerms = MutableStateFlow(false)
    val agreeTerms: StateFlow<Boolean> = _agreeTerms.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _otpValues = MutableStateFlow(List(6) { "" })
    val otpValues: StateFlow<List<String>> = _otpValues.asStateFlow()

    private val _resendCount = MutableStateFlow(0)
    val resendCount: StateFlow<Int> = _resendCount.asStateFlow()

    private var pendingEmail: String = ""
    private var resetNewPassword: String = ""

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _snackbarType = MutableStateFlow(SnackbarType.INFO)
    val snackbarType: StateFlow<SnackbarType> = _snackbarType.asStateFlow()

    private fun showSnackbar(message: String, type: SnackbarType = SnackbarType.ERROR) {
        _snackbarMessage.value = message
        _snackbarType.value = type
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateAgreeTerms(agreed: Boolean) {
        _agreeTerms.value = agreed
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun updateOtpDigit(index: Int, value: String) {
        if (index !in 0..5) return
        val current = _otpValues.value.toMutableList()
        current[index] = value
        _otpValues.value = current
    }

    private fun clearOtp() {
        _otpValues.value = List(6) { "" }
    }

    // ==================== LOGIN FLOW ====================

    fun authenticateUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            showSnackbar("Email and password are required", SnackbarType.WARNING)
            return
        }

        _uiState.value = LoginUiState.Loading
        pendingEmail = email

        viewModelScope.launch {
            val response = authRepository.login(email, password)

            if (response.success && response.data != null) {
                val data = response.data

                // Stage 1: MFA Required (User is NOT authenticated yet)
                if (data.message == "MFA_REQUIRED") {
                    _uiState.value = LoginUiState.OtpSent
                } else {
                    _uiState.value = LoginUiState.Error("Invalid login response")
                }
            } else {
                _uiState.value = LoginUiState.Error(response.message)
            }
        }
    }

    fun verifyMfaLogin(code: String) {
        if (code.length < 6) {
            showSnackbar("Please enter the 6-digit verification code", SnackbarType.WARNING)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val response = authRepository.verifyMfaLogin(pendingEmail, code)

            if (response.success && response.data != null) {
                val data = response.data

                if (data.accessToken != null && data.refreshToken != null) {
                    // Save tokens to DataStore (simple data)
                    datastore.saveTokens(data.accessToken, data.refreshToken)
                    datastore.saveUserEmail(pendingEmail)

                    // Get the complete user from Room database (already saved by repository with all JWT fields)
                    val userEntity = userDao.getUserByEmail(pendingEmail)

                    val user = if (userEntity != null) {
                        // Convert Room entity to Domain User model with complete data
                        User(
                            uuid = userEntity.uuid,
                            email = userEntity.email,
                            firstName = userEntity.firstName,
                            lastName = userEntity.lastName,
                            userName = userEntity.userName,
                            personalPhone = userEntity.phone,
                            profileImage = userEntity.profileImage,
                            accessToken = data.accessToken,
                            refreshToken = data.refreshToken,
                            isAuthenticated = true,
                            primaryPurpose = userEntity.primaryPurpose,
                            // JWT payload fields
                            role = userEntity.role,
                            accountType = userEntity.accountType,
                            accountId = userEntity.accountId,
                            accountName = userEntity.accountName,
                            organizationUuid = userEntity.organizationUuid,
                            planSlug = userEntity.planSlug,
                            tokenId = userEntity.tokenId
                        )
                    } else {
                        // Fallback if user not found in Room (should not happen)
                        User(
                            email = pendingEmail,
                            isAuthenticated = true,
                            accessToken = data.accessToken,
                            refreshToken = data.refreshToken
                        )
                    }

                    _uiState.value = LoginUiState.Success(
                        user = user,
                        message = response.message,
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken
                    )
                } else {
                    _uiState.value = LoginUiState.Error("Invalid MFA verification response")
                }
            } else {
                _uiState.value = LoginUiState.Error(response.message)
            }
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            _resendCount.update { it + 1 }
            clearOtp()

            val response = authRepository.login(pendingEmail, "")

            if (response.success && response.data?.message == "MFA_REQUIRED") {
                showSnackbar("New verification code sent!", SnackbarType.SUCCESS)
                _uiState.value = LoginUiState.OtpSent
            } else {
                showSnackbar(response.message, SnackbarType.ERROR)
            }
        }
    }

    // ==================== PASSWORD RESET FLOW ====================

    fun updateResetNewPassword(newPassword: String) {
        resetNewPassword = newPassword
    }

    suspend fun getCachedResetEmail(): String {
        return datastore.getResetPasswordEmail() ?: ""
    }

    fun requestPasswordReset(email: String) {
        if (email.isBlank()) {
            showSnackbar("Please enter your email address", SnackbarType.WARNING)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val response = authRepository.requestPasswordReset(email)

            if (response.success) {
                datastore.saveResetPasswordEmail(email)
                showSnackbar("Verification code sent to your email!", SnackbarType.SUCCESS)
                _uiState.value = LoginUiState.PasswordResetOtpSent
            } else {
                _uiState.value = LoginUiState.Error(response.message)
            }
        }
    }

    fun verifyAndResetPassword(code: String) {
        if (code.length < 6) {
            showSnackbar("Please enter the 6-digit verification code", SnackbarType.WARNING)
            return
        }

        if (resetNewPassword.isBlank()) {
            showSnackbar("Please enter your new password", SnackbarType.WARNING)
            return
        }

        if (resetNewPassword.length < 8) {
            showSnackbar("Password must be at least 8 characters", SnackbarType.WARNING)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val email = datastore.getResetPasswordEmail() ?: run {
                showSnackbar("Session expired. Please start over.", SnackbarType.ERROR)
                _uiState.value = LoginUiState.Idle
                return@launch
            }

            val response = authRepository.resetPassword(email, code, resetNewPassword)

            if (response.success) {
                datastore.clearResetPasswordEmail()
                _uiState.value = LoginUiState.PasswordResetSuccess(response.message)
            } else {
                _uiState.value = LoginUiState.Error(response.message)
            }
        }
    }

    fun resendPasswordResetOtp() {
        viewModelScope.launch {
            val email = datastore.getResetPasswordEmail()
            if (email.isNullOrBlank()) {
                showSnackbar("Please start over", SnackbarType.WARNING)
                return@launch
            }

            _uiState.value = LoginUiState.Loading

            val response = authRepository.requestPasswordReset(email)

            if (response.success) {
                _resendCount.update { it + 1 }
                clearOtp()
                showSnackbar("New verification code sent!", SnackbarType.SUCCESS)
                _uiState.value = LoginUiState.PasswordResetOtpSent
            } else {
                _uiState.value = LoginUiState.Error(response.message)
            }
        }
    }

    fun clearResetPasswordCache() {
        viewModelScope.launch {
            datastore.clearResetPasswordEmail()
        }
        resetNewPassword = ""
        clearOtp()
    }

    // ==================== SESSION MANAGEMENT ====================

    suspend fun isUserLoggedIn(): Boolean {
        val accessToken = datastore.getAccessToken()
        val userEmail = datastore.getUserEmail()
        return accessToken != null && userEmail != null
    }

    suspend fun getStoredUser(): User? {
        val accessToken = datastore.getAccessToken()
        val refreshToken = datastore.getRefreshToken()
        val email = datastore.getUserEmail()

        return if (accessToken != null && refreshToken != null && email != null) {
            // Try to get complete user from Room first
            val userEntity = userDao.getUserByEmail(email)
            if (userEntity != null) {
                User(
                    uuid = userEntity.uuid,
                    email = userEntity.email,
                    firstName = userEntity.firstName,
                    lastName = userEntity.lastName,
                    userName = userEntity.userName,
                    personalPhone = userEntity.phone,
                    profileImage = userEntity.profileImage,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    isAuthenticated = true,
                    primaryPurpose = userEntity.primaryPurpose,
                    role = userEntity.role,
                    accountType = userEntity.accountType,
                    accountId = userEntity.accountId,
                    accountName = userEntity.accountName,
                    organizationUuid = userEntity.organizationUuid,
                    planSlug = userEntity.planSlug,
                    tokenId = userEntity.tokenId
                )
            } else {
                // Fallback to basic user
                User(
                    email = email,
                    isAuthenticated = true,
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            }
        } else {
            null
        }
    }

    suspend fun getStoredUserFlow(): User? {
        // This is a one-time fetch, not a flow
        return getStoredUser()
    }

    fun logout() {
        viewModelScope.launch {
            val refreshToken = datastore.getRefreshToken()
            if (refreshToken != null) {
                authRepository.logout(refreshToken)
            }
            datastore.clearSession()
            // Clear user from Room (optional - you might want to keep for offline access)
            // val email = datastore.getUserEmail()
            // email?.let { userDao.deleteUser(it) }
            resetState()
        }
    }

    fun showErrorMessage(message: String) {
        _snackbarMessage.value = message
        _snackbarType.value = SnackbarType.ERROR
        viewModelScope.launch {
            delay(4000)
            clearSnackbar()
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        clearOtp()
        _resendCount.value = 0
        pendingEmail = ""
        clearSnackbar()
    }
}