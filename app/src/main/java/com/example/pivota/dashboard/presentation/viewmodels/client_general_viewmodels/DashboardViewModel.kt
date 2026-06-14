package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.auth.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    private val _networkErrorEvent = MutableSharedFlow<String>()
    val networkErrorEvent: SharedFlow<String> = _networkErrorEvent.asSharedFlow()


    // Add recovery events from TokenManager
    private val _recoveryEvent = MutableSharedFlow<TokenManager.RecoveryType>()
    val recoveryEvent: SharedFlow<TokenManager.RecoveryType> = _recoveryEvent.asSharedFlow()

    init {
        // Collect logout events from TokenManager
        viewModelScope.launch {
            tokenManager.logoutEvent.collect {
                _logoutEvent.emit(it)
            }
        }

        // Collect network error events from TokenManager
        viewModelScope.launch {
            tokenManager.networkErrorEvent.collect { errorMessage ->
                _networkErrorEvent.emit(errorMessage)
            }
        }

        // Collect recovery events from TokenManager
        viewModelScope.launch {
            tokenManager.recoveryEvent.collect { recoveryType ->
                _recoveryEvent.emit(recoveryType)
            }
        }
    }

    fun startTokenRefresh() {
        viewModelScope.launch {
            try {
                tokenManager.startAutoRefresh()
                println("🔄 [DashboardViewModel] Token auto-refresh started")
            } catch (e: Exception) {
                println("❌ [DashboardViewModel] Error starting refresh: ${e.message}")
            }
        }
    }

    fun stopTokenRefresh() {
        viewModelScope.launch {
            try {
                tokenManager.stopAutoRefresh()
                println("🔄 [DashboardViewModel] Token auto-refresh stopped")
            } catch (e: Exception) {
                println("❌ [DashboardViewModel] Error stopping refresh: ${e.message}")
            }
        }
    }

    suspend fun refreshTokenNow(): Boolean {
        return try {
            val success = tokenManager.refreshToken()
            if (success) {
                println("✅ [DashboardViewModel] Manual token refresh successful")
            } else {
                println("❌ [DashboardViewModel] Manual token refresh failed")
            }
            success
        } catch (e: Exception) {
            println("❌ [DashboardViewModel] Error during manual refresh: ${e.message}")
            false
        }
    }


    /**
     * Simplified manual retry without callback
     */
    suspend fun manualRetrySuspend(): Boolean {
        return try {
            tokenManager.manualRetry()
        } catch (e: Exception) {
            println("❌ [DashboardViewModel] Manual retry exception: ${e.message}")
            false
        }
    }

    suspend fun getCurrentToken(): String? {
        return tokenManager.getCurrentToken()
    }

    suspend fun isTokenValid(): Boolean {
        return tokenManager.hasValidSession()
    }

    /**
     * Get current backend status
     */
    fun getBackendStatus(): TokenManager.BackendStatus {
        return tokenManager.getBackendStatus()
    }

    /**
     * Reset failure state (useful when user manually triggers retry)
     */
    fun resetFailureState() {
        tokenManager.resetFailureState()
        println("🔄 [DashboardViewModel] Failure state reset")
    }

    override fun onCleared() {
        super.onCleared()
        // Stop auto-refresh when ViewModel is cleared
        viewModelScope.launch {
            try {
                tokenManager.stopAutoRefresh()
                println("🔄 [DashboardViewModel] Token refresh stopped on ViewModel clear")
            } catch (e: Exception) {
                println("❌ [DashboardViewModel] Error stopping refresh on clear: ${e.message}")
            }
        }
    }
}

/**
 * Result of a manual retry attempt
 */
sealed class RetryResult {
    data object Success : RetryResult()
    data object Failed : RetryResult()
    data object AlreadyInProgress : RetryResult()
    data class Error(val message: String) : RetryResult()
}