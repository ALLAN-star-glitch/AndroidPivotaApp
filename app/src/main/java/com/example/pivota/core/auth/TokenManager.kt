package com.example.pivota.core.auth

import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.getUserFriendlyMessage
import com.example.pivota.core.preferences.PivotaDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: PivotaDataStore,
    private val authUseCases: AuthUseCases
) : TokenProvider {  // Implement TokenProvider
    private val mutex = Mutex()
    private var refreshJob: kotlinx.coroutines.Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Track refresh failures
    private var consecutiveFailures = 0
    private var lastRefreshAttempt = 0L

    // Flag to track if refresh is in progress
    private var isRefreshing = false

    // Emit logout events when token refresh fails permanently
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    // Emit network error events (for UI to show banner if needed)
    private val _networkErrorEvent = MutableSharedFlow<String>()
    val networkErrorEvent: SharedFlow<String> = _networkErrorEvent.asSharedFlow()

    companion object {
        private const val REFRESH_INTERVAL_MS = 12 * 60 * 1000L // 12 minutes
        private const val RETRY_COOLDOWN_MS = 60 * 1000L // 1 minute cooldown between retries
        private const val MAX_CONSECUTIVE_FAILURES = 5 // Max failures before forced logout
        private const val REFRESH_TIMEOUT_MS = 30000L // 30 second timeout for refresh
    }

    // Start automatic token refresh
    fun startAutoRefresh() {
        if (refreshJob?.isActive == true) {
            println("🔄 [TokenManager] Auto-refresh already running")
            return
        }

        // Reset failure counter on new session
        consecutiveFailures = 0
        isRefreshing = false

        refreshJob = scope.launch {
            println("🔄 [TokenManager] Token auto-refresh loop started")
            while (isActive) {
                try {
                    delay(REFRESH_INTERVAL_MS)
                    if (isActive) {
                        refreshTokenIfNeeded()
                    }
                } catch (e: Exception) {
                    println("⚠️ [TokenManager] Error in refresh loop: ${e.message}")
                    // Continue the loop instead of crashing
                }
            }
            println("🔄 [TokenManager] Token auto-refresh loop ended")
        }
        println("🔄 [TokenManager] Token auto-refresh started")
    }

    // Stop automatic refresh (call on logout)
    suspend fun stopAutoRefresh() {
        try {
            refreshJob?.cancelAndJoin()
        } catch (e: Exception) {
            println("⚠️ [TokenManager] Error stopping refresh: ${e.message}")
            refreshJob?.cancel()
        }
        refreshJob = null
        consecutiveFailures = 0
        isRefreshing = false
        println("🔄 [TokenManager] Token auto-refresh stopped")
    }

    // Check if auto-refresh is running
    fun isAutoRefreshActive(): Boolean = refreshJob?.isActive == true

    // Check and refresh if needed
    suspend fun refreshTokenIfNeeded(): Boolean {
        // Don't try to refresh if we're already refreshing
        if (isRefreshing) {
            println("⚠️ [TokenManager] Refresh already in progress, skipping")
            return false
        }

        return mutex.withLock {
            val shouldRefresh = dataStore.shouldRefreshToken()
            if (!shouldRefresh) return false

            // Check cooldown to prevent rapid retries
            val timeSinceLastAttempt = System.currentTimeMillis() - lastRefreshAttempt
            if (timeSinceLastAttempt < RETRY_COOLDOWN_MS && consecutiveFailures > 0) {
                println("⚠️ [TokenManager] Skipping refresh - too soon after failure (${timeSinceLastAttempt}ms)")
                return false
            }

            return refreshToken()
        }
    }

    // Force refresh token
    suspend fun refreshToken(): Boolean {
        // Don't allow concurrent refreshes
        if (isRefreshing) {
            println("⚠️ [TokenManager] Refresh already in progress, skipping")
            return false
        }

        isRefreshing = true
        lastRefreshAttempt = System.currentTimeMillis()

        try {
            val refreshToken = dataStore.getRefreshToken()
            if (refreshToken == null) {
                println("⚠️ [TokenManager] No refresh token available")
                return false
            }

            println("🔄 [TokenManager] Attempting token refresh...")

            // Add timeout to prevent hanging
            val result = withTimeoutOrNull(REFRESH_TIMEOUT_MS) {
                authUseCases.refreshToken(refreshToken)
            }

            if (result == null) {
                println("❌ [TokenManager] Token refresh timed out after ${REFRESH_TIMEOUT_MS}ms")
                handleRefreshTimeout()
                return false
            }

            return when (result) {
                is ApiResult.Success -> {
                    consecutiveFailures = 0
                    val newAccessToken = result.data.first
                    val newRefreshToken = result.data.second ?: refreshToken
                    dataStore.saveTokensWithTimestamp(newAccessToken, newRefreshToken)
                    println("✅ [TokenManager] Token refreshed successfully")
                    true
                }
                is ApiResult.Error -> {
                    handleRefreshFailure(result)
                    false
                }
                ApiResult.Loading -> {
                    println("⚠️ [TokenManager] Refresh still loading...")
                    false
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            println("⚠️ [TokenManager] Token refresh was cancelled")
            return false
        } catch (e: Exception) {
            handleRefreshException(e)
            return false
        } finally {
            isRefreshing = false
        }
    }

    private suspend fun handleRefreshTimeout() {
        consecutiveFailures++
        println("❌ [TokenManager] Token refresh timeout ($consecutiveFailures/$MAX_CONSECUTIVE_FAILURES)")

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            println("🚨 [TokenManager] Max consecutive failures reached due to timeouts - forcing logout")
            forceLogout()
        }
    }

    private suspend fun handleRefreshFailure(result: ApiResult.Error) {
        val errorMessage = result.getUserFriendlyMessage()

        if (isAuthenticationError(result)) {
            println("❌ [TokenManager] Refresh token invalid/expired - forcing immediate logout")
            forceLogout()
            return
        }

        if (isNetworkError(result)) {
            println("⚠️ [TokenManager] Network/connection issue during refresh - will retry later")
            println("   Message: $errorMessage")
            _networkErrorEvent.emit(errorMessage)
            return
        }

        consecutiveFailures++
        println("❌ [TokenManager] Token refresh failed ($consecutiveFailures/$MAX_CONSECUTIVE_FAILURES): $errorMessage")

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            println("🚨 [TokenManager] Max consecutive failures reached - forcing logout")
            forceLogout()
        }
    }

    private suspend fun handleRefreshException(e: Exception) {
        val errorMessage = e.message ?: "Unknown error"

        if (isNetworkException(e)) {
            println("⚠️ [TokenManager] Network exception during refresh: $errorMessage")
            _networkErrorEvent.emit("Connection issue. Will retry automatically.")
            return
        }

        consecutiveFailures++
        println("❌ [TokenManager] Token refresh exception ($consecutiveFailures/$MAX_CONSECUTIVE_FAILURES): $errorMessage")

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            println("🚨 [TokenManager] Max consecutive failures reached - forcing logout")
            forceLogout()
        }
    }

    private fun isAuthenticationError(result: ApiResult.Error): Boolean {
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
                message.contains("no internet") ||
                message.contains("failed to connect") ||
                message.contains("connection refused")
    }

    private fun isNetworkException(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("connect") ||
                message.contains("timeout") ||
                message.contains("network") ||
                message.contains("socket") ||
                message.contains("host") ||
                message.contains("cancellation") // Don't treat cancellation as network error
    }

    private suspend fun forceLogout() {
        println("🚨 [TokenManager] Force logout initiated")

        try {
            // Clear all session data
            dataStore.clearSession()
            dataStore.clearGuestMode()

            // Stop auto-refresh
            stopAutoRefresh()

            // Reset failure counter
            consecutiveFailures = 0
            isRefreshing = false

            // Emit logout event to trigger navigation to login screen
            _logoutEvent.emit(Unit)

            println("🚨 [TokenManager] User forcefully logged out due to token refresh failures")
        } catch (e: Exception) {
            println("❌ [TokenManager] Error during force logout: ${e.message}")
        }
    }

    // Get valid token (auto-refresh if needed)
    suspend fun getValidToken(): String? {
        try {
            refreshTokenIfNeeded()
            return dataStore.getAccessToken()
        } catch (e: kotlinx.coroutines.CancellationException) {
            println("⚠️ [TokenManager] getValidToken was cancelled")
            return dataStore.getAccessToken() // Return existing token on cancellation
        } catch (e: Exception) {
            println("❌ [TokenManager] Error getting valid token: ${e.message}")
            return dataStore.getAccessToken()
        }
    }

    // Check if user has a valid session
    suspend fun hasValidSession(): Boolean {
        val accessToken = dataStore.getAccessToken()
        val refreshToken = dataStore.getRefreshToken()
        val hasTokens = accessToken != null && refreshToken != null
        println("🔍 [TokenManager] hasValidSession: tokens present = $hasTokens")
        return hasTokens
    }

    // Check if user has a valid session (non-suspend version for quick checks)
    suspend fun hasValidSessionSync(): Boolean {
        // Note: This is a simplified version - use with caution
        return dataStore.getAccessToken() != null && dataStore.getRefreshToken() != null
    }

    // Clear session on logout
    suspend fun clearSession() {
        try {
            stopAutoRefresh()
            dataStore.clearSession()
            dataStore.clearGuestMode()
            consecutiveFailures = 0
            isRefreshing = false
            println("🔐 [TokenManager] Session cleared manually")
        } catch (e: Exception) {
            println("❌ [TokenManager] Error clearing session: ${e.message}")
        }
    }

    // Get token age
    suspend fun getTokenAge(): Long {
        return dataStore.getTokenAge()
    }

    // Get current access token (without refresh check)
    suspend fun getCurrentToken(): String? {
        return dataStore.getAccessToken()
    }

    // Get current refresh token
    suspend fun getCurrentRefreshToken(): String? {
        return dataStore.getRefreshToken()
    }

    override suspend fun getAccessToken(): String? {
        return dataStore.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.getRefreshToken()
    }
}