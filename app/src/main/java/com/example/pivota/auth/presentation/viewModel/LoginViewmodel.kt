package com.example.pivota.auth.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.presentation.state.LoginUiState
import com.example.pivota.core.auth.TokenManager
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.database.entity.UserEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.getUserFriendlyMessage
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
    private val authUseCases: AuthUseCases,
    private val datastore: PivotaDataStore,
    private val userDao: UserDao,
    private val tokenManager: TokenManager
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

    // Signal to close the OTP dialog when OTP sending fails
    private val _shouldCloseDialog = MutableStateFlow(false)
    val shouldCloseDialog: StateFlow<Boolean> = _shouldCloseDialog.asStateFlow()

    // Google Sign-In State
    private val _googleSignInState = MutableStateFlow<GoogleSignInState>(GoogleSignInState.Idle)
    val googleSignInState: StateFlow<GoogleSignInState> = _googleSignInState.asStateFlow()

    sealed class GoogleSignInState {
        object Idle : GoogleSignInState()
        object Loading : GoogleSignInState()
        data class Success(val user: User, val accessToken: String, val refreshToken: String) : GoogleSignInState()
        data class Error(val message: String) : GoogleSignInState()
    }

    private fun showSnackbar(message: String, type: SnackbarType = SnackbarType.ERROR) {
        _snackbarMessage.value = message
        _snackbarType.value = type
        viewModelScope.launch {
            delay(4000)
            clearSnackbar()
        }
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

    fun updateOtpFull(value: String) {
        _otpValues.value = List(6) { index -> value.getOrNull(index)?.toString() ?: "" }
    }

    private fun clearOtp() {
        _otpValues.value = List(6) { "" }
    }

    fun resetDialogCloseFlag() {
        _shouldCloseDialog.value = false
    }

    // ==================== HELPER METHODS FOR DATA PERSISTENCE ====================

    // In LoginViewModel.kt
    private suspend fun saveUserSession(user: User, accessToken: String, refreshToken: String) {
        try {
            // Use saveTokensWithTimestamp instead of saveTokens
            datastore.saveTokensWithTimestamp(accessToken, refreshToken)
            datastore.saveUserEmail(user.email)
            datastore.markOnboardingComplete(true)
            datastore.saveGuestModeEnabled(false)

            // Save to Room Database
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser == null) {
                val userEntity = UserEntity(
                    uuid = user.uuid,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    userName = user.userName,
                    phone = user.personalPhone,
                    profileImage = user.profileImage,
                    isAuthenticated = true,
                    isOnboardingComplete = true,
                    hasSeenWelcomeScreen = true,
                    primaryPurpose = user.primaryPurpose,
                    role = user.role,
                    accountType = user.accountType,
                    accountId = user.accountId,
                    accountName = user.accountName,
                    organizationUuid = user.organizationUuid,
                    planSlug = user.planSlug,
                    tokenId = user.tokenId,
                    updatedAt = System.currentTimeMillis()
                )
                userDao.insertUser(userEntity)
            }

            println("✅ User session saved with timestamp")
        } catch (e: Exception) {
            println("❌ Error saving user session: ${e.message}")
        }
    }

    // ==================== GOOGLE SIGN-IN ====================

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _googleSignInState.value = GoogleSignInState.Loading
            println("🔍 [LoginViewModel] Google Sign-In started")

            val result = authUseCases.googleSignIn(idToken, null)

            when (result) {
                is ApiResult.Success -> {
                    val loginResponse = result.data

                    when (loginResponse) {
                        is LoginResponse.Authenticated -> {
                            println("🔍 [LoginViewModel] User authenticated: ${loginResponse.user.email}")
                            val user = loginResponse.user

                            // Save to DataStore and Room
                            saveUserSession(user, loginResponse.accessToken, loginResponse.refreshToken)

                            _googleSignInState.value = GoogleSignInState.Success(
                                user = user,
                                accessToken = loginResponse.accessToken,
                                refreshToken = loginResponse.refreshToken
                            )
                        }
                        is LoginResponse.MfaRequired -> {
                            println("🔍 [LoginViewModel] MFA Required for Google Sign-In")
                            _googleSignInState.value = GoogleSignInState.Error(
                                "MFA verification required. Please check your email."
                            )
                        }
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    println("🔍 [LoginViewModel] Google Sign-In Error: $errorMessage")
                    _googleSignInState.value = GoogleSignInState.Error(errorMessage)
                    showSnackbar(errorMessage, SnackbarType.ERROR)
                }
                ApiResult.Loading -> {
                    println("🔍 [LoginViewModel] Google Sign-In Loading...")
                }
            }
        }
    }

    fun resetGoogleSignInState() {
        _googleSignInState.value = GoogleSignInState.Idle
    }

    // ==================== LOGIN FLOW ====================

    fun authenticateUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            showSnackbar("Email and password are required", SnackbarType.WARNING)
            return
        }

        _uiState.value = LoginUiState.Loading
        pendingEmail = email
        _shouldCloseDialog.value = false

        viewModelScope.launch {
            val result = authUseCases.loginUser(email, password)

            when (result) {
                is ApiResult.Success -> {
                    val loginResponse = result.data

                    when (loginResponse) {
                        is LoginResponse.MfaRequired -> {
                            println("🔍 [LoginViewModel] MFA Required for: ${loginResponse.email}")
                            _uiState.value = LoginUiState.OtpSent
                        }
                        is LoginResponse.Authenticated -> {
                            println("🔍 [LoginViewModel] Login successful for: ${loginResponse.user.email}")
                            val user = loginResponse.user

                            // Save to DataStore and Room
                            saveUserSession(user, loginResponse.accessToken, loginResponse.refreshToken)

                            _uiState.value = LoginUiState.Success(
                                user = user,
                                message = loginResponse.message ?: "Login successful",
                                accessToken = loginResponse.accessToken,
                                refreshToken = loginResponse.refreshToken
                            )
                        }
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    println("❌ [LoginViewModel] Login Error: $errorMessage")
                    _uiState.value = LoginUiState.Error(errorMessage)
                    showSnackbar(errorMessage, SnackbarType.ERROR)
                    _shouldCloseDialog.value = true
                }
                ApiResult.Loading -> {
                    // Already in loading state
                }
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
            val result = authUseCases.verifyMfaLogin(pendingEmail, code)

            when (result) {
                is ApiResult.Success -> {
                    val loginResponse = result.data

                    when (loginResponse) {
                        is LoginResponse.Authenticated -> {
                            println("🔍 [LoginViewModel] MFA verification successful for: ${loginResponse.user.email}")
                            val user = loginResponse.user

                            // Save to DataStore and Room
                            saveUserSession(user, loginResponse.accessToken, loginResponse.refreshToken)

                            _uiState.value = LoginUiState.Success(
                                user = user,
                                message = loginResponse.message ?: "Login successful",
                                accessToken = loginResponse.accessToken,
                                refreshToken = loginResponse.refreshToken
                            )
                        }
                        is LoginResponse.MfaRequired -> {
                            println("⚠️ [LoginViewModel] Still MFA required after verification")
                            val errorMessage = "MFA verification failed. Please try again."
                            _uiState.value = LoginUiState.Error(errorMessage)
                            showSnackbar(errorMessage, SnackbarType.ERROR)
                        }
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    println("❌ [LoginViewModel] MFA Verification Error: $errorMessage")
                    _uiState.value = LoginUiState.Error(errorMessage)
                    showSnackbar(errorMessage, SnackbarType.ERROR)
                }
                ApiResult.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            _resendCount.update { it + 1 }
            clearOtp()
            _shouldCloseDialog.value = false

            val result = authUseCases.loginUser(pendingEmail, "")

            when (result) {
                is ApiResult.Success -> {
                    val loginResponse = result.data
                    if (loginResponse is LoginResponse.MfaRequired) {
                        showSnackbar("New verification code sent!", SnackbarType.SUCCESS)
                        _uiState.value = LoginUiState.OtpSent
                    } else {
                        showSnackbar("Failed to resend code", SnackbarType.ERROR)
                        _shouldCloseDialog.value = true
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    showSnackbar(errorMessage, SnackbarType.ERROR)
                    _shouldCloseDialog.value = true
                }
                ApiResult.Loading -> {
                    // Loading state - do nothing
                }
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
        _shouldCloseDialog.value = false

        viewModelScope.launch {
            val result = authUseCases.requestPasswordReset(email)

            when (result) {
                is ApiResult.Success -> {
                    datastore.saveResetPasswordEmail(email)
                    showSnackbar("Verification code sent to your email!", SnackbarType.SUCCESS)
                    _uiState.value = LoginUiState.PasswordResetOtpSent
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    _uiState.value = LoginUiState.Error(errorMessage)
                    showSnackbar(errorMessage, SnackbarType.ERROR)
                    _shouldCloseDialog.value = true
                }
                ApiResult.Loading -> {
                    // Already in loading state
                }
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

            val result = authUseCases.resetPassword(email, code, resetNewPassword)

            when (result) {
                is ApiResult.Success -> {
                    datastore.clearResetPasswordEmail()
                    _uiState.value = LoginUiState.PasswordResetSuccess("Password reset successful")
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    _uiState.value = LoginUiState.Error(errorMessage)
                    showSnackbar(errorMessage, SnackbarType.ERROR)
                }
                ApiResult.Loading -> {
                    // Already in loading state
                }
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
            _shouldCloseDialog.value = false

            val result = authUseCases.requestPasswordReset(email)

            when (result) {
                is ApiResult.Success -> {
                    _resendCount.update { it + 1 }
                    clearOtp()
                    showSnackbar("New verification code sent!", SnackbarType.SUCCESS)
                    _uiState.value = LoginUiState.PasswordResetOtpSent
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    _uiState.value = LoginUiState.Error(errorMessage)
                    showSnackbar(errorMessage, SnackbarType.ERROR)
                    _shouldCloseDialog.value = true
                }
                ApiResult.Loading -> {
                    // Already in loading state
                }
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
        return getStoredUser()
    }


    suspend fun getAccessToken(): String? {
        return datastore.getAccessToken()
    }

    suspend fun getRefreshToken(): String? {
        return datastore.getRefreshToken()
    }

    fun logout() {
        viewModelScope.launch {
            // Stop auto refresh
            tokenManager.stopAutoRefresh()

            // Call logout API
            val refreshToken = datastore.getRefreshToken()
            if (refreshToken != null) {
                val result = authUseCases.logout(refreshToken)
                when (result) {
                    is ApiResult.Success -> {
                        println("✅ Logout successful")
                    }
                    is ApiResult.Error -> {
                        println("❌ Logout failed: ${result.getUserFriendlyMessage()}")
                    }
                    ApiResult.Loading -> {}
                }
            }

            // Clear all data
            datastore.clearSession()
            datastore.clearGuestMode()
            resetState()
        }
    }

    fun updateOtpDigit(index: Int, value: String) {
        if (index !in 0..5) return
        val current = _otpValues.value.toMutableList()
        current[index] = value
        _otpValues.value = current
    }

    fun showErrorMessage(message: String) {
        _snackbarMessage.value = message
        _snackbarType.value = SnackbarType.ERROR
        viewModelScope.launch {
            delay(4000)
            clearSnackbar()
        }
    }

    fun showInfoMessage(message: String) {
        _snackbarMessage.value = message
        _snackbarType.value = SnackbarType.INFO
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
        _shouldCloseDialog.value = false
    }
}