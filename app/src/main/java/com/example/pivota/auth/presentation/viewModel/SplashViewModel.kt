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

            val hasValidSession = tokenManager.hasValidSession()
            val isGuestMode = dataStore.isGuestModeEnabled()

            println("🔍 [SplashViewModel] hasValidSession: $hasValidSession, isGuestMode: $isGuestMode")

            if (hasValidSession) {
                // Check if token needs refresh
                val tokenAge = dataStore.getTokenAge()
                val shouldRefresh = tokenAge > 12 * 60 * 1000L

                if (shouldRefresh) {
                    println("🔄 [SplashViewModel] Token is ${tokenAge / 1000}s old, refreshing...")
                    val refreshSuccess = tokenManager.refreshToken()

                    if (refreshSuccess) {
                        println("✅ [SplashViewModel] Token refreshed successfully")
                        tokenManager.startAutoRefresh()
                        _startDestination.value = Dashboard
                    } else {
                        println("❌ [SplashViewModel] Token refresh failed")
                        _startDestination.value = Welcome
                    }
                } else {
                    println("✅ [SplashViewModel] Token is valid (${tokenAge / 1000}s old)")
                    tokenManager.startAutoRefresh()
                    _startDestination.value = Dashboard
                }
            } else if (isGuestMode) {
                println("👤 [SplashViewModel] Guest mode enabled")
                _startDestination.value = GuestDashboard
            } else {
                println("🆕 [SplashViewModel] New user, showing welcome")
                _startDestination.value = Welcome
            }

            _isLoading.value = false
        }
    }
}