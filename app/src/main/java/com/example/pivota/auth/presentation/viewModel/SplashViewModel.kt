package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.core.auth.TokenManager
import com.example.pivota.core.navigation.*
import com.example.pivota.core.preferences.PivotaDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val tokenManager: TokenManager,
    private val dataStore: PivotaDataStore
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination: StateFlow<Any?> = _startDestination.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkNavigationState()
    }

    private fun checkNavigationState() {
        viewModelScope.launch {
            _isLoading.value = true

            val isGuestMode = dataStore.isGuestModeEnabled()
            val hasValidSession = tokenManager.hasValidSession()

            println("🔍 [SplashViewModel] hasValidSession: $hasValidSession, isGuestMode: $isGuestMode")

            when {
                isGuestMode -> {
                    println("👤 [SplashViewModel] Guest mode enabled")
                    _startDestination.value = GuestDashboard
                }
                hasValidSession -> {
                    // Always go to Dashboard, even if backend is down
                    // Try to refresh token in background without blocking
                    tryRefreshTokenInBackground()

                    // ✅ Navigate to Dashboard immediately - don't wait for refresh
                    _startDestination.value = Dashboard
                }
                else -> {
                    println("🆕 [SplashViewModel] New user")
                    _startDestination.value = Welcome
                }
            }

            _isLoading.value = false
        }
    }

    private fun tryRefreshTokenInBackground() {
        viewModelScope.launch {
            val tokenAge = dataStore.getTokenAge()
            val shouldRefresh = tokenAge > 12 * 60 * 1000L

            if (shouldRefresh) {
                println("🔄 [Background] Attempting token refresh...")
                val refreshSuccess = tokenManager.refreshToken()
                if (refreshSuccess) {
                    tokenManager.startAutoRefresh()
                    println("✅ [Background] Token refreshed successfully")
                } else {
                    // ✅ Don't show error - just log it
                    println("⚠️ [Background] Token refresh failed - backend may be down, using cached session")
                }
            } else {
                println("✅ [Background] Token is still valid (${tokenAge / 1000}s old)")
                tokenManager.startAutoRefresh()
            }
        }
    }
}