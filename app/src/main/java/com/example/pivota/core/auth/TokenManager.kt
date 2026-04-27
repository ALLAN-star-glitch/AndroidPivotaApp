package com.example.pivota.core.auth

import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.getUserFriendlyMessage
import com.example.pivota.core.preferences.PivotaDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: PivotaDataStore,
    private val authUseCases: AuthUseCases
) {
    private val mutex = Mutex()
    private var refreshJob: kotlinx.coroutines.Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Track refresh failures
    private var consecutiveFailures = 0
    private var lastRefreshAttempt = 0L

    // Emit logout events when token refresh fails permanently
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    companion object {
        private const val REFRESH_INTERVAL_MS = 12 * 60 * 1000L // 12 minutes
        private const val RETRY_COOLDOWN_MS = 60 * 1000L // 1 minute cooldown between retries
        private const val MAX_CONSECUTIVE_FAILURES = 5 // Max failures before forced logout
    }

    // Start automatic token refresh
    fun startAutoRefresh() {
        if (refreshJob?.isActive == true) return

        // Reset failure counter on new session
        consecutiveFailures = 0

        refreshJob = scope.launch {
            while (true) {
                delay(REFRESH_INTERVAL_MS)
                refreshTokenIfNeeded()
            }
        }
        println("🔄 Token auto-refresh started")
    }

    // Stop automatic refresh (call on logout)
    fun stopAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
        consecutiveFailures = 0
        println("🔄 Token auto-refresh stopped")
    }

    // Check and refresh if needed
    suspend fun refreshTokenIfNeeded(): Boolean {
        return mutex.withLock {
            val shouldRefresh = dataStore.shouldRefreshToken()
            if (!shouldRefresh) return false

            // Check cooldown to prevent rapid retries
            val timeSinceLastAttempt = System.currentTimeMillis() - lastRefreshAttempt
            if (timeSinceLastAttempt < RETRY_COOLDOWN_MS && consecutiveFailures > 0) {
                println("⚠️ Skipping refresh - too soon after failure (${timeSinceLastAttempt}ms)")
                return false
            }

            return refreshToken()
        }
    }

    // Force refresh token
    suspend fun refreshToken(): Boolean {
        lastRefreshAttempt = System.currentTimeMillis()
        val refreshToken = dataStore.getRefreshToken() ?: return false

        return try {
            val result = authUseCases.refreshToken(refreshToken)

            when (result) {
                is ApiResult.Success -> {
                    // Reset failure counter on success
                    consecutiveFailures = 0
                    val newAccessToken = result.data.first
                    val newRefreshToken = result.data.second ?: refreshToken

                    // Save new tokens with timestamp
                    dataStore.saveTokensWithTimestamp(newAccessToken, newRefreshToken)

                    println("✅ Token refreshed successfully")
                    true
                }

                is ApiResult.Error -> {
                    handleRefreshFailure(result)
                    false
                }

                ApiResult.Loading -> false
            }
        } catch (e: Exception) {
            handleRefreshException(e)
            false
        }
    }

    private suspend fun handleRefreshFailure(result: ApiResult.Error) {
        val errorMessage = result.getUserFriendlyMessage()

        // Check if it's an authentication error (refresh token invalid/expired)
        if (isAuthenticationError(result)) {
            println("❌ Refresh token invalid/expired - forcing immediate logout")
            forceLogout()
            return
        }

        // Check if it's a network error
        if (isNetworkError(result)) {
            println("⚠️ Network error during refresh - will retry later")
            // Don't count network errors towards failure limit
            return
        }

        // Server or other errors - count towards failure limit
        consecutiveFailures++
        println("❌ Token refresh failed ($consecutiveFailures/$MAX_CONSECUTIVE_FAILURES): $errorMessage")

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            println("🚨 Max consecutive failures reached - forcing logout")
            forceLogout()
        }
    }

    private suspend fun handleRefreshException(e: Exception) {
        consecutiveFailures++
        println("❌ Token refresh exception ($consecutiveFailures/$MAX_CONSECUTIVE_FAILURES): ${e.message}")

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            println("🚨 Max consecutive failures reached - forcing logout")
            forceLogout()
        }
    }

    private fun isAuthenticationError(result: ApiResult.Error): Boolean {
        // Check for 401 Unauthorized or similar auth errors
        val message = result.getUserFriendlyMessage().lowercase()
        return message.contains("unauthorized") ||
                message.contains("invalid token") ||
                message.contains("token expired") ||
                result.toString().contains("401")
    }

    private fun isNetworkError(result: ApiResult.Error): Boolean {
        val message = result.getUserFriendlyMessage().lowercase()
        return message.contains("network") ||
                message.contains("connection") ||
                message.contains("timeout") ||
                message.contains("unable to connect") ||
                message.contains("no internet")
    }

    private suspend fun forceLogout() {
        // Clear all session data
        dataStore.clearSession()
        dataStore.clearGuestMode()

        // Stop auto-refresh
        stopAutoRefresh()

        // Reset failure counter
        consecutiveFailures = 0

        // Emit logout event to trigger navigation to login screen
        _logoutEvent.emit(Unit)

        println("🚨 User forcefully logged out due to token refresh failures")
    }

    // Get valid token (auto-refresh if needed)
    suspend fun getValidToken(): String? {
        refreshTokenIfNeeded()
        return dataStore.getAccessToken()
    }

    // Check if user has a valid session
    suspend fun hasValidSession(): Boolean {
        val accessToken = dataStore.getAccessToken()
        val refreshToken = dataStore.getRefreshToken()
        val hasTokens = accessToken != null && refreshToken != null
        println("🔍 [TokenManager] hasValidSession: tokens present = $hasTokens")
        return hasTokens
    }

    // Clear session on logout
    suspend fun clearSession() {
        stopAutoRefresh()
        dataStore.clearSession()
        dataStore.clearGuestMode()
        consecutiveFailures = 0
        println("🔐 Session cleared manually")
    }

    // In TokenManager.kt
    suspend fun getTokenAge(): Long {
        return dataStore.getTokenAge()
    }
}