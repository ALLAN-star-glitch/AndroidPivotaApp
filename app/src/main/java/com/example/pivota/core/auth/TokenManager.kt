package com.example.pivota.core.auth

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.core.health.GatewayHealthChecker
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.getUserFriendlyMessage
import com.example.pivota.core.network.useCase.HealthUseCase
import com.example.pivota.core.preferences.PivotaDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: PivotaDataStore,
    private val authUseCases: dagger.Lazy<AuthUseCases>,
    private val gatewayHealthChecker: GatewayHealthChecker,
    private val healthUseCase: HealthUseCase,
    @ApplicationContext private val context: Context
) : TokenProvider {

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val mutex = Mutex()
    private var refreshJob: kotlinx.coroutines.Job? = null
    private var healthCheckJob: kotlinx.coroutines.Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ✅ Cooldown ONLY for token refresh, NOT for network detection
    private var lastTokenRefreshTime = 0L
    private val TOKEN_REFRESH_COOLDOWN_MS = 30000L // 30 seconds between token refreshes

    // Track refresh failures
    private var consecutiveFailures = 0
    private var lastRefreshAttempt = 0L

    // Flag to track if refresh is in progress
    private var isRefreshing = false

    // Track network and backend availability
    private var isNetworkAvailable = true
    private var isBackendAvailable = true
    private var lastSuccessfulRefresh = 0L
    private var consecutiveBackendErrors = 0

    // ✅ Track current service status for different messages
    private var currentStatus: ServiceStatus = ServiceStatus.AVAILABLE

    // Emit logout events when token refresh fails permanently
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    // Emit network error events (for UI to show banner if needed)
    private val _networkErrorEvent = MutableSharedFlow<String>()
    val networkErrorEvent: SharedFlow<String> = _networkErrorEvent.asSharedFlow()

    // Emit backend status events
    private val _backendStatusEvent = MutableSharedFlow<BackendStatus>()
    val backendStatusEvent: SharedFlow<BackendStatus> = _backendStatusEvent.asSharedFlow()

    // Emit recovery events
    private val _recoveryEvent = MutableSharedFlow<RecoveryType>()
    val recoveryEvent: SharedFlow<RecoveryType> = _recoveryEvent.asSharedFlow()

    companion object {
        private const val REFRESH_INTERVAL_MS = 12 * 60 * 1000L // 12 minutes
        private const val RETRY_COOLDOWN_MS = 60 * 1000L // 1 minute cooldown between retries
        private const val MAX_CONSECUTIVE_FAILURES = 5 // Max failures before forced logout
        private const val REFRESH_TIMEOUT_MS = 30000L // 30 second timeout for refresh
        private const val HEALTH_CHECK_INTERVAL_MS = 30 * 1000L // Check health every 30 seconds
        private const val HEALTH_CHECK_TIMEOUT_MS = 5000L // 5 second timeout for health check
        private const val MAX_BACKEND_ERRORS_BEFORE_COOLDOWN = 3

        // ✅ Different messages for different scenarios
        const val MSG_NO_INTERNET = "No internet connection. Please check your network."
        const val MSG_BACKEND_DOWN = "We are experiencing technical downtime. Our team is working on it."
        const val MSG_NETWORK_RECOVERED = "Network restored! Reconnecting..."
        const val MSG_BACKEND_RECOVERED = "Service restored! Refreshing your data..."
        const val MSG_RETRYING = "Retrying connection..."
        const val MSG_CONNECTING = "Connecting to service..."
    }

    // ✅ Service status enum for different states
    enum class ServiceStatus {
        AVAILABLE,
        INTERNET_DOWN,
        BACKEND_DOWN,
        RECOVERING
    }

    enum class RecoveryType {
        NETWORK_RECOVERED,
        BACKEND_RECOVERED,
        MANUAL_RETRY
    }

    data class BackendStatus(
        val isAvailable: Boolean,
        val status: ServiceStatus = ServiceStatus.AVAILABLE,
        val lastError: String? = null,
        val consecutiveErrors: Int = 0,
        val lastSuccessTime: Long = 0
    )

    /**
     * Check if device actually has internet connectivity using ConnectivityManager
     */
    private fun isNetworkActuallyAvailable(): Boolean {
        val connectivityManager = ContextCompat.getSystemService(
            context,
            ConnectivityManager::class.java
        ) ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
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
        isNetworkAvailable = isNetworkActuallyAvailable()
        isBackendAvailable = true
        currentStatus = ServiceStatus.AVAILABLE
        lastSuccessfulRefresh = System.currentTimeMillis()
        consecutiveBackendErrors = 0

        // Register network callback for instant network recovery
        registerNetworkCallback()

        refreshJob = scope.launch {
            println("🔄 [TokenManager] Token auto-refresh loop started")

            var lastHealthCheck = 0L

            while (isActive) {
                try {
                    val currentTime = System.currentTimeMillis()

                    // Periodic health check
                    if (currentTime - lastHealthCheck >= HEALTH_CHECK_INTERVAL_MS) {
                        lastHealthCheck = currentTime
                        performHealthCheck()
                    }

                    delay(REFRESH_INTERVAL_MS)

                    if (!isActive) continue

                    // Check conditions before refreshing
                    when {
                        !isNetworkAvailable -> {
                            println("⚠️ [TokenManager] No network, skipping refresh")
                            continue
                        }
                        !isBackendAvailable -> {
                            println("⚠️ [TokenManager] Backend appears down, skipping refresh")
                            continue
                        }
                        else -> {
                            refreshTokenIfNeeded()
                        }
                    }
                } catch (e: Exception) {
                    println("⚠️ [TokenManager] Error in refresh loop: ${e.message}")
                }
            }
            println("🔄 [TokenManager] Token auto-refresh loop ended")
        }

        // Start health check job
        startHealthCheckJob()

        println("🔄 [TokenManager] Token auto-refresh started")
    }

    private fun startHealthCheckJob() {
        healthCheckJob?.cancel()
        healthCheckJob = scope.launch {
            while (isActive) {
                delay(HEALTH_CHECK_INTERVAL_MS.milliseconds)
                performHealthCheck()
            }
        }
    }

    private fun registerNetworkCallback() {
        val connectivityManager = ContextCompat.getSystemService(
            context,
            ConnectivityManager::class.java
        ) ?: run {
            println("⚠️ [TokenManager] ConnectivityManager not available")
            return
        }

        println("✅ [TokenManager] Registering network callback")

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                println("🌐 [TokenManager] 🔥 NETWORK CALLBACK FIRED - Internet available!")
                // ✅ INSTANT - no delay for network detection
                isNetworkAvailable = true
                println("✅ [TokenManager] Network is available: true")

                scope.launch {
                    // ✅ Small delay only for stability, not for detection
                    delay(500.milliseconds)

                    // ✅ Double-check network is actually available
                    if (!isNetworkActuallyAvailable()) {
                        println("⚠️ [TokenManager] Network callback fired but no actual network")
                        isNetworkAvailable = false
                        return@launch
                    }

                    println("✅ [TokenManager] Network confirmed available")

                    // ✅ If we were in INTERNET_DOWN state, transition to RECOVERING
                    if (currentStatus == ServiceStatus.INTERNET_DOWN) {
                        currentStatus = ServiceStatus.RECOVERING
                        _networkErrorEvent.emit(MSG_NETWORK_RECOVERED)
                        _backendStatusEvent.emit(BackendStatus(
                            isAvailable = false,
                            status = ServiceStatus.RECOVERING,
                            lastError = MSG_NETWORK_RECOVERED,
                            consecutiveErrors = consecutiveBackendErrors
                        ))
                    }

                    // ✅ Check token refresh cooldown before refreshing
                    val now = System.currentTimeMillis()
                    if (now - lastTokenRefreshTime < TOKEN_REFRESH_COOLDOWN_MS) {
                        val secondsLeft = (TOKEN_REFRESH_COOLDOWN_MS - (now - lastTokenRefreshTime)) / 1000
                        println("⚠️ [TokenManager] Token refresh skipped - cooldown active (${secondsLeft}s remaining)")
                        return@launch
                    }

                    val backendHealthy = checkBackendHealth()

                    if (backendHealthy) {
                        isBackendAvailable = true
                        currentStatus = ServiceStatus.RECOVERING
                        println("✅ [TokenManager] Network and backend available")

                        _backendStatusEvent.emit(BackendStatus(
                            isAvailable = true,
                            status = ServiceStatus.RECOVERING,
                            lastSuccessTime = System.currentTimeMillis()
                        ))
                        _recoveryEvent.emit(RecoveryType.NETWORK_RECOVERED)
                        _networkErrorEvent.emit(MSG_NETWORK_RECOVERED)

                        lastTokenRefreshTime = System.currentTimeMillis()
                        println("🔄 [TokenManager] Refreshing token after network recovery...")
                        refreshToken()

                        currentStatus = ServiceStatus.AVAILABLE
                        _backendStatusEvent.emit(BackendStatus(
                            isAvailable = true,
                            status = ServiceStatus.AVAILABLE,
                            lastSuccessTime = System.currentTimeMillis()
                        ))
                        _networkErrorEvent.emit("")
                    } else {
                        println("⚠️ [TokenManager] Network available but backend still down")
                        currentStatus = ServiceStatus.BACKEND_DOWN
                        _backendStatusEvent.emit(BackendStatus(
                            isAvailable = false,
                            status = ServiceStatus.BACKEND_DOWN,
                            lastError = MSG_BACKEND_DOWN,
                            consecutiveErrors = consecutiveBackendErrors
                        ))
                        _recoveryEvent.emit(RecoveryType.NETWORK_RECOVERED)
                        _networkErrorEvent.emit(MSG_BACKEND_DOWN)
                    }
                }
            }

            override fun onLost(network: Network) {
                println("⚠️ [TokenManager] 🔥 NETWORK CALLBACK FIRED - Internet lost!")
                // ✅ INSTANT - immediately update network state
                isNetworkAvailable = false
                currentStatus = ServiceStatus.INTERNET_DOWN

                scope.launch {
                    _backendStatusEvent.emit(BackendStatus(
                        isAvailable = false,
                        status = ServiceStatus.INTERNET_DOWN,
                        lastError = MSG_NO_INTERNET,
                        consecutiveErrors = 0
                    ))
                    _networkErrorEvent.emit(MSG_NO_INTERNET)
                    println("📡 [TokenManager] Emitted NO_INTERNET message")
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
        println("✅ [TokenManager] Network callback registered successfully")
    }

    private fun unregisterNetworkCallback() {
        networkCallback?.let {
            val connectivityManager = ContextCompat.getSystemService(
                context,
                ConnectivityManager::class.java
            )
            connectivityManager?.unregisterNetworkCallback(it)
        }
        networkCallback = null
    }

    private suspend fun performHealthCheck() {
        val previousBackendState = isBackendAvailable

        // ✅ Check network state
        val networkAvailable = isNetworkActuallyAvailable()
        println("🔍 [TokenManager] performHealthCheck - networkAvailable: $networkAvailable")

        if (!networkAvailable) {
            println("⚠️ [TokenManager] No network connection, skipping health check")
            // ✅ Set INTERNET_DOWN status if not already
            if (currentStatus != ServiceStatus.INTERNET_DOWN) {
                currentStatus = ServiceStatus.INTERNET_DOWN
                _backendStatusEvent.emit(BackendStatus(
                    isAvailable = false,
                    status = ServiceStatus.INTERNET_DOWN,
                    lastError = MSG_NO_INTERNET,
                    consecutiveErrors = 0
                ))
                _networkErrorEvent.emit(MSG_NO_INTERNET)
            }
            return
        }

        // Check backend health
        val backendHealthy = checkBackendHealth()

        if (backendHealthy != isBackendAvailable) {
            isBackendAvailable = backendHealthy

            if (backendHealthy && !previousBackendState) {
                // ✅ Backend recovered!
                println("✅ [TokenManager] Backend is back online!")
                currentStatus = ServiceStatus.RECOVERING

                _backendStatusEvent.emit(BackendStatus(
                    isAvailable = true,
                    status = ServiceStatus.RECOVERING,
                    lastSuccessTime = System.currentTimeMillis()
                ))
                _recoveryEvent.emit(RecoveryType.BACKEND_RECOVERED)
                _networkErrorEvent.emit(MSG_BACKEND_RECOVERED)

                consecutiveBackendErrors = 0

                // ✅ Check token refresh cooldown
                val now = System.currentTimeMillis()
                if (now - lastTokenRefreshTime >= TOKEN_REFRESH_COOLDOWN_MS) {
                    println("🔄 [TokenManager] Refreshing token after backend recovery...")
                    delay(2000)
                    refreshToken()
                    lastTokenRefreshTime = System.currentTimeMillis()
                } else {
                    val secondsLeft = (TOKEN_REFRESH_COOLDOWN_MS - (now - lastTokenRefreshTime)) / 1000
                    println("⚠️ [TokenManager] Token refresh skipped - cooldown active (${secondsLeft}s remaining)")
                }

                currentStatus = ServiceStatus.AVAILABLE
                _backendStatusEvent.emit(BackendStatus(
                    isAvailable = true,
                    status = ServiceStatus.AVAILABLE,
                    lastSuccessTime = System.currentTimeMillis()
                ))
                delay(3000)
                _networkErrorEvent.emit("")

            } else if (!backendHealthy && previousBackendState) {
                // ✅ Backend went down (network is still available)
                println("❌ [TokenManager] Backend appears to be down")
                currentStatus = ServiceStatus.BACKEND_DOWN

                _backendStatusEvent.emit(BackendStatus(
                    isAvailable = false,
                    status = ServiceStatus.BACKEND_DOWN,
                    lastError = MSG_BACKEND_DOWN,
                    consecutiveErrors = consecutiveBackendErrors
                ))
                _networkErrorEvent.emit(MSG_BACKEND_DOWN)
            }
        }
    }

    private suspend fun checkBackendHealth(): Boolean {
        // ✅ Step 1: Check actual network connectivity FIRST
        val networkAvailable = isNetworkActuallyAvailable()
        println("🔍 [TokenManager] checkBackendHealth - isNetworkActuallyAvailable: $networkAvailable")

        if (!networkAvailable) {
            println("⚠️ [TokenManager] No network connection detected")
            currentStatus = ServiceStatus.INTERNET_DOWN
            _backendStatusEvent.emit(BackendStatus(
                isAvailable = false,
                status = ServiceStatus.INTERNET_DOWN,
                lastError = MSG_NO_INTERNET,
                consecutiveErrors = consecutiveBackendErrors
            ))
            _networkErrorEvent.emit(MSG_NO_INTERNET)
            return false
        }

        // ✅ Step 2: DNS check (only if network is available)
        val isGatewayHealthy = gatewayHealthChecker.isGatewayHealthy()
        if (!isGatewayHealthy) {
            println("⚠️ [TokenManager] DNS resolution failed")
            consecutiveBackendErrors++
            return false
        }

        // ✅ Step 3: HTTP health check
        return try {
            val result = withTimeoutOrNull(HEALTH_CHECK_TIMEOUT_MS.milliseconds) {
                healthUseCase()
            }

            if (result == null) {
                println("⚠️ [TokenManager] Health check timeout - service unreachable")
                consecutiveBackendErrors++
                return false
            }

            when (result) {
                is ApiResult.Success -> {
                    val healthData = result.data
                    if (healthData.status == "ok") {
                        if (consecutiveBackendErrors > 0) {
                            println("✅ [TokenManager] Service recovered!")
                            consecutiveBackendErrors = 0
                        }
                        true
                    } else {
                        println("⚠️ [TokenManager] Health check status: ${healthData.status}")
                        consecutiveBackendErrors++
                        false
                    }
                }
                is ApiResult.Error -> {
                    // ✅ Check if it's a network error
                    if (result.networkError is NetworkError.NoInternet ||
                        result.networkError is NetworkError.Timeout) {
                        println("⚠️ [TokenManager] Network/connection issue detected")
                        currentStatus = ServiceStatus.INTERNET_DOWN
                        _backendStatusEvent.emit(BackendStatus(
                            isAvailable = false,
                            status = ServiceStatus.INTERNET_DOWN,
                            lastError = MSG_NO_INTERNET,
                            consecutiveErrors = consecutiveBackendErrors
                        ))
                        _networkErrorEvent.emit(MSG_NO_INTERNET)
                    } else {
                        println("⚠️ [TokenManager] Health check error: ${result.networkError.userFriendlyMessage}")
                        consecutiveBackendErrors++
                    }
                    false
                }
                ApiResult.Loading -> false
            }
        } catch (e: Exception) {
            println("⚠️ [TokenManager] Health check failed: ${e.message}")
            consecutiveBackendErrors++
            false
        }
    }

    // Stop automatic refresh (call on logout)
    suspend fun stopAutoRefresh() {
        try {
            refreshJob?.cancelAndJoin()
            healthCheckJob?.cancelAndJoin()
            unregisterNetworkCallback()
        } catch (e: Exception) {
            println("⚠️ [TokenManager] Error stopping refresh: ${e.message}")
            refreshJob?.cancel()
            healthCheckJob?.cancel()
        }
        refreshJob = null
        healthCheckJob = null
        consecutiveFailures = 0
        isRefreshing = false
        currentStatus = ServiceStatus.AVAILABLE
        println("🔄 [TokenManager] Token auto-refresh stopped")
    }

    // Check if auto-refresh is running
    fun isAutoRefreshActive(): Boolean = refreshJob?.isActive == true

    // Check and refresh if needed
    suspend fun refreshTokenIfNeeded(): Boolean {
        if (isRefreshing) {
            println("⚠️ [TokenManager] Refresh already in progress, skipping")
            return false
        }

        if (!isNetworkAvailable) {
            println("⚠️ [TokenManager] No network, skipping refresh")
            return false
        }

        if (!isBackendAvailable) {
            println("⚠️ [TokenManager] Backend unavailable, skipping refresh")
            return false
        }

        return mutex.withLock {
            val shouldRefresh = dataStore.shouldRefreshToken()
            if (!shouldRefresh) {
                if (consecutiveFailures > 0 && isNetworkAvailable && isBackendAvailable) {
                    val timeSinceLastAttempt = System.currentTimeMillis() - lastRefreshAttempt
                    if (timeSinceLastAttempt >= RETRY_COOLDOWN_MS) {
                        println("🔄 [TokenManager] Retrying failed refresh after cooldown")
                        return refreshToken()
                    }
                }
                return false
            }

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
        // ✅ Check token refresh cooldown
        val now = System.currentTimeMillis()
        if (now - lastTokenRefreshTime < TOKEN_REFRESH_COOLDOWN_MS) {
            val secondsLeft = (TOKEN_REFRESH_COOLDOWN_MS - (now - lastTokenRefreshTime)) / 1000
            println("⚠️ [TokenManager] Refresh skipped - cooldown active (${secondsLeft}s remaining)")
            return false
        }

        if (isRefreshing) {
            println("⚠️ [TokenManager] Refresh already in progress, skipping")
            return false
        }

        // Check actual network state
        val hasNetwork = isNetworkActuallyAvailable()
        if (!hasNetwork) {
            println("⚠️ [TokenManager] No network connection (actual check), skipping refresh")
            isNetworkAvailable = false
            currentStatus = ServiceStatus.INTERNET_DOWN
            _networkErrorEvent.emit(MSG_NO_INTERNET)
            return false
        }
        isNetworkAvailable = true

        if (!isBackendAvailable) {
            println("⚠️ [TokenManager] Backend appears down, skipping refresh")
            currentStatus = ServiceStatus.BACKEND_DOWN
            _networkErrorEvent.emit(MSG_BACKEND_DOWN)
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

            val result = withTimeoutOrNull(REFRESH_TIMEOUT_MS.milliseconds) {
                authUseCases.get().refreshToken(refreshToken)
            }

            if (result == null) {
                println("❌ [TokenManager] Token refresh timed out after ${REFRESH_TIMEOUT_MS}ms")
                handleRefreshTimeout()
                return false
            }

            return when (result) {
                is ApiResult.Success -> {
                    handleRefreshSuccess(result.data.first, result.data.second ?: refreshToken)
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

    private suspend fun handleRefreshSuccess(accessToken: String, refreshToken: String) {
        consecutiveFailures = 0
        consecutiveBackendErrors = 0
        lastSuccessfulRefresh = System.currentTimeMillis()
        isBackendAvailable = true
        isNetworkAvailable = true
        currentStatus = ServiceStatus.AVAILABLE
        lastTokenRefreshTime = System.currentTimeMillis()

        dataStore.saveTokensWithTimestamp(accessToken, refreshToken)
        println("✅ [TokenManager] Token refreshed successfully")

        if (consecutiveFailures > 0 || !isBackendAvailable) {
            _recoveryEvent.emit(RecoveryType.BACKEND_RECOVERED)
        }
    }

    private suspend fun handleRefreshTimeout() {
        consecutiveBackendErrors++
        isBackendAvailable = false
        isNetworkAvailable = true
        currentStatus = ServiceStatus.BACKEND_DOWN

        println("❌ [TokenManager] Token refresh timeout ($consecutiveBackendErrors/$MAX_BACKEND_ERRORS_BEFORE_COOLDOWN)")

        if (consecutiveBackendErrors >= MAX_BACKEND_ERRORS_BEFORE_COOLDOWN) {
            _networkErrorEvent.emit("Service is slow or unavailable. Using cached data.")
        } else {
            _networkErrorEvent.emit("Connection timeout. Will retry automatically.")
        }
    }

    private suspend fun handleRefreshFailure(result: ApiResult.Error) {
        val errorMessage = result.getUserFriendlyMessage()

        if (isAuthenticationError(result)) {
            println("❌ [TokenManager] Refresh token invalid/expired - forcing immediate logout")
            forceLogout()
            return
        }

        if (isBackendError(result)) {
            consecutiveBackendErrors++
            isBackendAvailable = false
            isNetworkAvailable = true
            currentStatus = ServiceStatus.BACKEND_DOWN
            println("⚠️ [TokenManager] Backend error detected ($consecutiveBackendErrors/$MAX_BACKEND_ERRORS_BEFORE_COOLDOWN): $errorMessage")

            _backendStatusEvent.emit(BackendStatus(
                isAvailable = false,
                status = ServiceStatus.BACKEND_DOWN,
                lastError = errorMessage,
                consecutiveErrors = consecutiveBackendErrors
            ))

            val userMessage = if (consecutiveBackendErrors >= MAX_BACKEND_ERRORS_BEFORE_COOLDOWN) {
                "Service temporarily unavailable. Will retry automatically when service is restored."
            } else {
                "Unable to reach the server. The service may be temporarily unavailable."
            }
            _networkErrorEvent.emit(userMessage)
            return
        }

        if (isNetworkError(result)) {
            println("⚠️ [TokenManager] Network/connection issue during refresh - will retry later")
            println("   Message: $errorMessage")
            isNetworkAvailable = false
            isBackendAvailable = false
            currentStatus = ServiceStatus.INTERNET_DOWN
            _networkErrorEvent.emit(errorMessage)
            lastRefreshAttempt = System.currentTimeMillis() - (RETRY_COOLDOWN_MS - 5000)
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
        when (e) {
            is CancellationException -> {
                println("ℹ️ [TokenManager] Token refresh cancelled (normal during navigation)")
                return
            }
            else -> {
                if (isBackendException(e)) {
                    consecutiveBackendErrors++
                    isBackendAvailable = false
                    isNetworkAvailable = true
                    currentStatus = ServiceStatus.BACKEND_DOWN
                    println("⚠️ [TokenManager] Backend error detected ($consecutiveBackendErrors/$MAX_BACKEND_ERRORS_BEFORE_COOLDOWN): ${e.message}")

                    _backendStatusEvent.emit(BackendStatus(
                        isAvailable = false,
                        status = ServiceStatus.BACKEND_DOWN,
                        lastError = e.message ?: "Backend service unavailable",
                        consecutiveErrors = consecutiveBackendErrors
                    ))

                    val userMessage = if (consecutiveBackendErrors >= MAX_BACKEND_ERRORS_BEFORE_COOLDOWN) {
                        "Service temporarily unavailable. Will retry automatically when service is restored."
                    } else {
                        "Unable to reach the server. The service may be temporarily unavailable."
                    }
                    _networkErrorEvent.emit(userMessage)
                    return
                }

                if (isNetworkException(e)) {
                    println("⚠️ [TokenManager] Network/connection issue: ${e.message}")
                    isNetworkAvailable = false
                    isBackendAvailable = false
                    currentStatus = ServiceStatus.INTERNET_DOWN
                    _networkErrorEvent.emit("No internet connection. Will retry when online.")
                    return
                }

                consecutiveFailures++
                println("❌ [TokenManager] Token refresh exception ($consecutiveFailures/$MAX_CONSECUTIVE_FAILURES): ${e.message}")

                if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                    println("🚨 [TokenManager] Max consecutive failures reached - forcing logout")
                    forceLogout()
                }
            }
        }
    }

    private fun isBackendError(result: ApiResult.Error): Boolean {
        val message = result.getUserFriendlyMessage().lowercase()
        val errorString = result.toString().lowercase()

        return message.contains("500") ||
                message.contains("502") ||
                message.contains("503") ||
                message.contains("504") ||
                message.contains("service unavailable") ||
                message.contains("internal server error") ||
                message.contains("bad gateway") ||
                message.contains("connection refused") ||
                message.contains("failed to connect") ||
                errorString.contains("500") ||
                errorString.contains("503") ||
                errorString.contains("connectexception")
    }

    private fun isBackendException(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("connectexception") ||
                message.contains("failed to connect") ||
                (message.contains("timeout") && !message.contains("connect")) ||
                message.contains("connection refused") ||
                e.javaClass.simpleName.contains("ConnectException") ||
                e.javaClass.simpleName.contains("SocketTimeoutException")
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
        return message.contains("no internet") ||
                (message.contains("network") && message.contains("unreachable")) ||
                message.contains("wifi") ||
                message.contains("mobile data")
    }

    private fun isNetworkException(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("no internet") ||
                (message.contains("network") && message.contains("unreachable")) ||
                (message.contains("wifi") && message.contains("disabled")) ||
                (message.contains("mobile data") && message.contains("disabled")) ||
                e.javaClass.simpleName.contains("UnknownHostException") ||
                (message.contains("unknownhost") && !message.contains("10.0.2.2"))
    }

    suspend fun forceLogout() {
        println("🚨 [TokenManager] Force logout initiated")

        try {
            dataStore.clearSession()
            dataStore.clearGuestMode()
            stopAutoRefresh()
            consecutiveFailures = 0
            isRefreshing = false
            currentStatus = ServiceStatus.AVAILABLE
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
            return dataStore.getAccessToken()
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

    // Clear session on logout
    suspend fun clearSession() {
        try {
            stopAutoRefresh()
            dataStore.clearSession()
            dataStore.clearGuestMode()
            consecutiveFailures = 0
            isRefreshing = false
            currentStatus = ServiceStatus.AVAILABLE
            println("🔐 [TokenManager] Session cleared manually")
        } catch (e: Exception) {
            println("❌ [TokenManager] Error clearing session: ${e.message}")
        }
    }

    // Get token age
    suspend fun getTokenAge(): Long {
        return dataStore.getTokenAge()
    }

    /**
     * Get current network availability
     */
    fun isNetworkAvailable(): Boolean = isNetworkAvailable

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

    /**
     * Manual retry for when network/backend recovers
     */
    suspend fun manualRetry(): Boolean {
        println("🔄 [TokenManager] Manual retry triggered")

        resetFailureState()
        performHealthCheck()

        if (!isNetworkAvailable) {
            println("⚠️ [TokenManager] No network connection, manual retry failed")
            currentStatus = ServiceStatus.INTERNET_DOWN
            _networkErrorEvent.emit(MSG_NO_INTERNET)
            return false
        }

        if (!isBackendAvailable) {
            println("⚠️ [TokenManager] Backend unavailable, manual retry failed")
            currentStatus = ServiceStatus.BACKEND_DOWN
            _networkErrorEvent.emit(MSG_BACKEND_DOWN)
            return false
        }

        val result = refreshToken()

        if (result) {
            println("✅ [TokenManager] Manual retry successful")
            currentStatus = ServiceStatus.AVAILABLE
            _recoveryEvent.emit(RecoveryType.MANUAL_RETRY)
            _networkErrorEvent.emit("")
        } else {
            println("❌ [TokenManager] Manual retry failed")
        }

        return result
    }

    /**
     * Reset failure states
     */
    fun resetFailureState() {
        consecutiveFailures = 0
        consecutiveBackendErrors = 0
        lastRefreshAttempt = 0L
        isRefreshing = false
        isNetworkAvailable = isNetworkActuallyAvailable()
        isBackendAvailable = true
        currentStatus = ServiceStatus.AVAILABLE
        println("🔄 [TokenManager] Failure state reset")
    }

    /**
     * Get current backend status
     */
    fun getBackendStatus(): BackendStatus {
        return BackendStatus(
            isAvailable = isBackendAvailable,
            status = currentStatus,
            consecutiveErrors = consecutiveBackendErrors,
            lastSuccessTime = lastSuccessfulRefresh
        )
    }

    /**
     * Get current service status
     */
    fun getCurrentStatus(): ServiceStatus = currentStatus

    /**
     * Call logout API to invalidate refresh token on server
     */
    suspend fun logout(onComplete: () -> Unit = {}) {
        try {
            println("🚨 [TokenManager] Logging out - calling API...")

            val refreshToken = dataStore.getRefreshToken()

            if (refreshToken != null) {
                val result = authUseCases.get().logout(refreshToken)
                when (result) {
                    is ApiResult.Success -> {
                        println("✅ [TokenManager] Logout API call successful")
                    }
                    is ApiResult.Error -> {
                        println("⚠️ [TokenManager] Logout API failed: ${result.networkError.userFriendlyMessage}")
                    }
                    else -> {}
                }
            } else {
                println("⚠️ [TokenManager] No refresh token found, skipping API call")
            }

            clearLocalSession()
            onComplete()

        } catch (e: Exception) {
            println("❌ [TokenManager] Exception during logout: ${e.message}")
            clearLocalSession()
            onComplete()
        }
    }

    /**
     * Clear local session data without calling API
     */
    suspend fun clearLocalSession() {
        try {
            println("🔐 [TokenManager] Clearing local session...")

            stopAutoRefresh()
            dataStore.clearSession()
            dataStore.clearGuestMode()
            consecutiveFailures = 0
            isRefreshing = false
            currentStatus = ServiceStatus.AVAILABLE
            _logoutEvent.emit(Unit)

            println("✅ [TokenManager] Local session cleared successfully")
        } catch (e: Exception) {
            println("❌ [TokenManager] Error clearing local session: ${e.message}")
        }
    }
}