package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.example.pivota.auth.domain.model.User

@HiltViewModel
class SharedAuthViewModel @Inject constructor() : ViewModel() {

    private val _resetSuccessMessage = MutableStateFlow<String?>(null)
    val resetSuccessMessage: StateFlow<String?> = _resetSuccessMessage.asStateFlow()

    private val _loginSuccessMessage = MutableStateFlow<String?>(null)
    val loginSuccessMessage: StateFlow<String?> = _loginSuccessMessage.asStateFlow()

    // Add signup success message
    private val _signupSuccessMessage = MutableStateFlow<String?>(null)
    val signupSuccessMessage: StateFlow<String?> = _signupSuccessMessage.asStateFlow()

    // Add user data storage
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken: StateFlow<String?> = _refreshToken.asStateFlow()

    // Reset password methods
    fun setResetSuccessMessage(message: String) {
        println("🔍 [SharedAuthViewModel] Setting reset message: $message")
        _resetSuccessMessage.value = message
    }

    fun peekResetSuccessMessage(): String? {
        val message = _resetSuccessMessage.value
        println("🔍 [SharedAuthViewModel] Peeking reset message: $message")
        return message
    }

    fun clearResetSuccessMessage() {
        println("🔍 [SharedAuthViewModel] Clearing reset message")
        _resetSuccessMessage.value = null
    }

    // Login success methods
    fun setLoginSuccessMessage(message: String) {
        println("🔍 [SharedAuthViewModel] Setting login message: $message")
        _loginSuccessMessage.value = message
    }

    fun peekLoginSuccessMessage(): String? {
        val message = _loginSuccessMessage.value
        println("🔍 [SharedAuthViewModel] Peeking login message: $message")
        return message
    }

    fun clearLoginSuccessMessage() {
        println("🔍 [SharedAuthViewModel] Clearing login message")
        _loginSuccessMessage.value = null
    }

    //  Signup success methods
    fun setSignupSuccessMessage(message: String) {
        println("🔍 [SharedAuthViewModel] Setting signup message: $message")
        _signupSuccessMessage.value = message
    }

    fun peekSignupSuccessMessage(): String? {
        val message = _signupSuccessMessage.value
        println("🔍 [SharedAuthViewModel] Peeking signup message: $message")
        return message
    }

    fun clearSignupSuccessMessage() {
        println("🔍 [SharedAuthViewModel] Clearing signup message")
        _signupSuccessMessage.value = null
    }

    // User data methods
    fun setUser(user: User) {
        println("🔍 [SharedAuthViewModel] Setting user: ${user.email}")
        _user.value = user
    }

    fun peekUser(): User? {
        return _user.value
    }

    fun clearUser() {
        _user.value = null
    }

    // Token methods
    fun setUserTokens(accessToken: String, refreshToken: String) {
        println("🔍 [SharedAuthViewModel] Setting tokens")
        _accessToken.value = accessToken
        _refreshToken.value = refreshToken
    }

    fun peekAccessToken(): String? {
        return _accessToken.value
    }

    fun peekRefreshToken(): String? {
        return _refreshToken.value
    }

    fun clearTokens() {
        _accessToken.value = null
        _refreshToken.value = null
    }

    // Clear all auth data (for logout)
    fun clearAllAuthData() {
        clearResetSuccessMessage()
        clearLoginSuccessMessage()
        clearSignupSuccessMessage()  // ✅ Clear signup message
        clearUser()
        clearTokens()
    }


}