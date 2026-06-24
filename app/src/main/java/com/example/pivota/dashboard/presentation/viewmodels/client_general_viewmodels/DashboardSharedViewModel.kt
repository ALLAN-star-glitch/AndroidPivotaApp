package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.core.auth.TokenManager
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.database.entity.UserEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.preferences.PivotaDataStore
import com.example.pivota.dashboard.domain.model.profile_models.CompleteProfile
import com.example.pivota.dashboard.domain.model.profile_models.ProfileAccount
import com.example.pivota.dashboard.domain.model.profile_models.ProfileCompletion
import com.example.pivota.dashboard.domain.model.profile_models.ProfileUser
import com.example.pivota.dashboard.domain.model.profile_models.AccountStatus
import com.example.pivota.dashboard.domain.model.profile_models.AccountType
import com.example.pivota.dashboard.domain.model.profile_models.UserStatus
import com.example.pivota.dashboard.domain.useCase.GetProfileUseCase
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import com.example.pivota.dashboard.presentation.state.DashboardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

// ======================================================
// BANNER TYPES
// ======================================================

enum class BannerType {
    NONE,
    NO_INTERNET,
    BACKEND_DOWN,
    RECOVERING,
    BACKEND_RECOVERED
}

@HiltViewModel
class DashboardSharedViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val userDao: UserDao,
    private val tokenManager: TokenManager,
    private val datastore: PivotaDataStore,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    // ======================================================
    // COMBINED DASHBOARD STATE
    // ======================================================

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent: StateFlow<Boolean> = _logoutEvent.asStateFlow()
    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    // Add state for tracking retry status
    private val _isRetrying = MutableStateFlow(false)
    val isRetrying: StateFlow<Boolean> = _isRetrying.asStateFlow()

    private val _retryResult = MutableSharedFlow<RetryResult>()
    val retryResult: SharedFlow<RetryResult> = _retryResult.asSharedFlow()

    private val _commonServicesState = MutableStateFlow<CommonServicesUiState>(CommonServicesUiState.Loading)
    val commonServicesState: StateFlow<CommonServicesUiState> = _commonServicesState.asStateFlow()

    // ======================================================
    // PROFILE STATE
    // ======================================================

    private val _profileState = MutableStateFlow<ProfileLoadState>(ProfileLoadState.Loading)
    val profileState: StateFlow<ProfileLoadState> = _profileState.asStateFlow()

    // ======================================================
    // HEADER STATE
    // ======================================================

    private val _headerState = MutableStateFlow<HeaderState>(HeaderState.Loading)
    val headerState: StateFlow<HeaderState> = _headerState.asStateFlow()

    // ======================================================
    // OFFLINE/WARNING STATE
    // ======================================================

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _offlineMessage = MutableStateFlow<String?>(null)
    val offlineMessage: StateFlow<String?> = _offlineMessage.asStateFlow()

    // ======================================================
    // BANNER TYPE STATE (NEW)
    // ======================================================

    private val _bannerType = MutableStateFlow<BannerType>(BannerType.NONE)
    val bannerType: StateFlow<BannerType> = _bannerType.asStateFlow()

    private val _isBackendDown = MutableStateFlow(false)
    val isBackendDown: StateFlow<Boolean> = _isBackendDown.asStateFlow()

    private val _isInternetDown = MutableStateFlow(false)
    val isInternetDown: StateFlow<Boolean> = _isInternetDown.asStateFlow()

    // Manual retry state
    private val _isManualRetrying = MutableStateFlow(false)
    val isManualRetrying: StateFlow<Boolean> = _isManualRetrying.asStateFlow()

    // ======================================================
    // TAB STATE
    // ======================================================

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Cache management
    private var lastFetchTime = 0L
    private val STALE_THRESHOLD_MS = 5 * 60 * 1000 // 5 minutes

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
        }
    }

    @Volatile
    private var isFetching = false

    private var currentUserId: String = ""
    private var hasEverLoadedProfile = false

    private var lastNetworkMessage: String? = null
    private var isRecoveringBannerShowing = false


    init {
        viewModelScope.launch {
            // Step 1: Load cached profile from Room immediately (instant display)
            loadCachedProfileFromRoom()

            // Step 2: Try to fetch fresh data in background
            refreshProfileInBackground()
        }

        // ✅ Listen for backend recovery and auto-refresh profile
        viewModelScope.launch {
            tokenManager.recoveryEvent.collect { recoveryType ->
                when (recoveryType) {
                    TokenManager.RecoveryType.BACKEND_RECOVERED -> {
                        println("🔄 [DashboardSharedViewModel] Backend recovered! Auto-refreshing profile...")
                        if (_bannerType.value != BannerType.RECOVERING) {
                            _bannerType.value = BannerType.RECOVERING
                            _offlineMessage.value = TokenManager.MSG_BACKEND_RECOVERED
                            _isOffline.value = true
                            _isBackendDown.value = false
                            _isInternetDown.value = false
                            lastNetworkMessage = TokenManager.MSG_BACKEND_RECOVERED
                        }

                        delay(2000.milliseconds)
                        refreshProfileInBackground()

                        delay(1000.milliseconds)
                        _bannerType.value = BannerType.NONE
                        _offlineMessage.value = null
                        _isOffline.value = false
                        lastNetworkMessage = null
                        isRecoveringBannerShowing = false
                    }
                    TokenManager.RecoveryType.NETWORK_RECOVERED -> {
                        println("🔄 [DashboardSharedViewModel] Network recovered, checking service...")
                        if (_bannerType.value != BannerType.RECOVERING) {
                            _bannerType.value = BannerType.RECOVERING
                            _offlineMessage.value = "Network restored. Checking service availability..."
                            _isOffline.value = true
                            _isBackendDown.value = false
                            _isInternetDown.value = false
                            lastNetworkMessage = "Network restored. Checking service availability..."
                            isRecoveringBannerShowing = true
                        }

                        delay(3000.milliseconds)
                        if (tokenManager.getCurrentStatus() == TokenManager.ServiceStatus.AVAILABLE) {
                            _bannerType.value = BannerType.NONE
                            _offlineMessage.value = null
                            _isOffline.value = false
                            lastNetworkMessage = null
                            isRecoveringBannerShowing = false
                        } else {
                            _bannerType.value = BannerType.BACKEND_DOWN
                            _offlineMessage.value = TokenManager.MSG_BACKEND_DOWN
                            _isBackendDown.value = true
                            lastNetworkMessage = TokenManager.MSG_BACKEND_DOWN
                            isRecoveringBannerShowing = false
                        }
                    }
                    TokenManager.RecoveryType.MANUAL_RETRY -> {
                        println("🔄 [DashboardSharedViewModel] Manual retry completed")
                    }
                }
            }
        }

        // ✅ Listen for network error events
        viewModelScope.launch {
            tokenManager.networkErrorEvent.collect { errorMessage ->
                println("🔴🔴🔴 [DashboardSharedViewModel] Received network error: $errorMessage")

                // ✅ Skip if same message was already processed
                if (errorMessage == lastNetworkMessage && errorMessage.isNotEmpty()) {
                    println("📡 [DashboardSharedViewModel] Skipping duplicate message: $errorMessage")
                    return@collect
                }

                when {
                    errorMessage.isEmpty() -> {
                        _bannerType.value = BannerType.NONE
                        _offlineMessage.value = null
                        _isOffline.value = false
                        _isBackendDown.value = false
                        _isInternetDown.value = false
                        lastNetworkMessage = null
                        isRecoveringBannerShowing = false
                    }
                    errorMessage.contains("No internet") || errorMessage == TokenManager.MSG_NO_INTERNET -> {
                        isRecoveringBannerShowing = false
                        _bannerType.value = BannerType.NO_INTERNET
                        _offlineMessage.value = errorMessage
                        _isOffline.value = true
                        _isBackendDown.value = false
                        _isInternetDown.value = true
                        lastNetworkMessage = errorMessage
                        println("📡 [DashboardSharedViewModel] Banner: NO_INTERNET")
                    }
                    errorMessage.contains("Service temporarily unavailable") || errorMessage == TokenManager.MSG_BACKEND_DOWN -> {
                        isRecoveringBannerShowing = false
                        _bannerType.value = BannerType.BACKEND_DOWN
                        _offlineMessage.value = errorMessage
                        _isOffline.value = true
                        _isBackendDown.value = true
                        _isInternetDown.value = false
                        lastNetworkMessage = errorMessage
                        println("📡 [DashboardSharedViewModel] Banner: BACKEND_DOWN")
                    }
                    errorMessage.contains("restored") || errorMessage == TokenManager.MSG_NETWORK_RECOVERED || errorMessage == TokenManager.MSG_BACKEND_RECOVERED -> {
                        // ✅ Only show RECOVERING if not already showing
                        if (!isRecoveringBannerShowing) {
                            isRecoveringBannerShowing = true
                            _bannerType.value = BannerType.RECOVERING
                            _offlineMessage.value = errorMessage
                            _isOffline.value = true
                            _isBackendDown.value = false
                            _isInternetDown.value = false
                            lastNetworkMessage = errorMessage
                            println("📡 [DashboardSharedViewModel] Banner: RECOVERING")
                        } else {
                            println("📡 [DashboardSharedViewModel] Skipping RECOVERING - already showing")
                        }
                        // Auto-dismiss after 3 seconds
                        delay(3000.milliseconds)
                        if (_bannerType.value == BannerType.RECOVERING) {
                            _bannerType.value = BannerType.NONE
                            _offlineMessage.value = null
                            _isOffline.value = false
                            lastNetworkMessage = null
                            isRecoveringBannerShowing = false
                        }
                    }
                    else -> {
                        if (errorMessage.isNotBlank()) {
                            _offlineMessage.value = errorMessage
                            _isOffline.value = true
                            lastNetworkMessage = errorMessage
                            isRecoveringBannerShowing = false
                            println("📡 [DashboardSharedViewModel] Banner should now show with message: $errorMessage")
                        }
                    }
                }
            }
        }

        // ✅ Listen for backend status changes
        viewModelScope.launch {
            tokenManager.backendStatusEvent.collect { status ->
                println("📡 [DashboardSharedViewModel] Backend status: isAvailable=${status.isAvailable}, status=${status.status}")

                val currentBanner = _bannerType.value

                when (status.status) {
                    TokenManager.ServiceStatus.AVAILABLE -> {
                        if (currentBanner != BannerType.NONE) {
                            _bannerType.value = BannerType.NONE
                            _offlineMessage.value = null
                            _isOffline.value = false
                            _isBackendDown.value = false
                            _isInternetDown.value = false
                            lastNetworkMessage = null
                            isRecoveringBannerShowing = false
                        }
                    }
                    TokenManager.ServiceStatus.INTERNET_DOWN -> {
                        if (currentBanner != BannerType.NO_INTERNET) {
                            isRecoveringBannerShowing = false
                            _bannerType.value = BannerType.NO_INTERNET
                            _offlineMessage.value = TokenManager.MSG_NO_INTERNET
                            _isOffline.value = true
                            _isBackendDown.value = false
                            _isInternetDown.value = true
                            lastNetworkMessage = TokenManager.MSG_NO_INTERNET
                        }
                    }
                    TokenManager.ServiceStatus.BACKEND_DOWN -> {
                        // ✅ Don't override RECOVERING banner
                        if (currentBanner == BannerType.RECOVERING) {
                            println("📡 [DashboardSharedViewModel] Keeping RECOVERING banner, ignoring BACKEND_DOWN")
                            return@collect
                        }
                        if (currentBanner != BannerType.BACKEND_DOWN) {
                            isRecoveringBannerShowing = false
                            _bannerType.value = BannerType.BACKEND_DOWN
                            _offlineMessage.value = TokenManager.MSG_BACKEND_DOWN
                            _isOffline.value = true
                            _isBackendDown.value = true
                            _isInternetDown.value = false
                            lastNetworkMessage = TokenManager.MSG_BACKEND_DOWN
                        }
                    }
                    TokenManager.ServiceStatus.RECOVERING -> {
                        // ✅ Skip if RECOVERING banner is already showing
                        if (isRecoveringBannerShowing) {
                            println("📡 [DashboardSharedViewModel] Skipping RECOVERING from status - already showing")
                            return@collect
                        }
                        if (currentBanner != BannerType.RECOVERING && currentBanner != BannerType.NONE) {
                            isRecoveringBannerShowing = true
                            _bannerType.value = BannerType.RECOVERING
                            _offlineMessage.value = "Connection restored! Refreshing your data..."
                            _isOffline.value = true
                            _isBackendDown.value = false
                            _isInternetDown.value = false
                            lastNetworkMessage = "Connection restored! Refreshing your data..."
                            println("📡 [DashboardSharedViewModel] Banner: RECOVERING (from status)")
                        }
                        // Auto-dismiss after 3 seconds
                        delay(3000.milliseconds)
                        if (_bannerType.value == BannerType.RECOVERING) {
                            _bannerType.value = BannerType.NONE
                            _offlineMessage.value = null
                            _isOffline.value = false
                            lastNetworkMessage = null
                            isRecoveringBannerShowing = false
                        }
                    }
                }
            }
        }
    }

    /**
     * Load cached profile from Room database - shows UI instantly
     */
    private suspend fun loadCachedProfileFromRoom() {
        try {
            val userEntity = userDao.getAuthenticatedUser()

            if (userEntity != null) {
                if (!userEntity.completeProfileJson.isNullOrEmpty()) {
                    val cachedProfile = json.decodeFromString<CompleteProfile>(userEntity.completeProfileJson)
                    updateStatesWithProfile(cachedProfile)
                    hasEverLoadedProfile = true

                    val cacheAge = System.currentTimeMillis() - userEntity.completeProfileLastUpdated
                    if (cacheAge > 24 * 60 * 60 * 1000L) {
                        val hoursOld = cacheAge / (1000 * 60 * 60)
                        _offlineMessage.value = "Using cached data from $hoursOld hours ago"
                        _isOffline.value = true
                        _bannerType.value = BannerType.BACKEND_DOWN
                    }

                    println("📦 Loaded complete profile from Room (${cacheAge / 1000}s old)")
                } else {
                    // User exists but no complete profile - create minimal one
                    val minimalProfile = createMinimalProfileFromUser(userEntity)
                    updateStatesWithProfile(minimalProfile)
                    _offlineMessage.value = "Loading full profile..."
                    _isOffline.value = true
                    println("📦 Using minimal profile from existing user")
                }
            } else {
                // No user at all
                setDefaultProfileState()
                _offlineMessage.value = "Please sign in to access your profile"
                _isOffline.value = true
                println("⚠️ No user found in Room")
            }
        } catch (e: Exception) {
            println("⚠️ Failed to load cached profile: ${e.message}")
            setDefaultProfileState()
        }
    }

    // Reset logout event after handling
    fun resetLogoutEvent() {
        _logoutEvent.value = false
    }

    // Logout with confirmation
    fun onLogoutClicked() {
        _showLogoutDialog.value = true
    }

    fun onLogoutConfirmed() {
        _showLogoutDialog.value = false

        // Clear UI state instantly
        _headerState.value = HeaderState.Loading
        _profileState.value = ProfileLoadState.Loading
        _dashboardState.value = DashboardState.Loading

        // Launch network logout in a separate coroutine that won't be cancelled
        CoroutineScope(Dispatchers.IO).launch {
            try {
                println("🌐 [LOGOUT] Starting network logout...")

                // Get refresh token before clearing
                val refreshToken = datastore.getRefreshToken()
                println("🌐 [LOGOUT] Refresh token: ${refreshToken?.take(20)}...")

                if (refreshToken != null) {
                    val result = authUseCases.logout(refreshToken)

                    when (result) {
                        is ApiResult.Success -> {
                            println("✅ [LOGOUT] Network logout successful")
                        }
                        is ApiResult.Error -> {
                            println("⚠️ [LOGOUT] Network logout error: ${result.networkError.message}")
                        }
                        else -> {}
                    }
                } else {
                    println("⚠️ [LOGOUT] No refresh token found")
                }
            } catch (e: Exception) {
                println("❌ [LOGOUT] Exception: ${e.message}")
            } finally {
                // Clear storage after network call
                try {
                    userDao.deleteAll()
                    tokenManager.clearLocalSession()
                    datastore.markOnboardingComplete(false)
                    datastore.saveGuestModeEnabled(false)
                    println("✅ [LOGOUT] Storage cleared")
                } catch (e: Exception) {
                    println("❌ [LOGOUT] Storage clear error: ${e.message}")
                }
            }
        }

        // Navigate instantly - network call continues in background
        _logoutEvent.value = true
    }

    fun onLogoutCancelled() {
        _showLogoutDialog.value = false
    }

    /**
     * Refresh profile in background - doesn't block UI
     */
    private suspend fun refreshProfileInBackground() {
        if (isFetching) {
            println("⏳ Refresh already in progress")
            return
        }

        isFetching = true

        try {
            // 8 second timeout for background refresh
            val result = withTimeoutOrNull(8000L.milliseconds) {
                getProfileUseCase()
            }

            when (result) {
                is ApiResult.Success -> {
                    val profile = result.data
                    currentUserId = profile.user.id

                    // Update UI with fresh data
                    updateStatesWithProfile(profile)

                    // Cache in Room
                    cacheProfileInRoom(profile)

                    hasEverLoadedProfile = true
                    _isOffline.value = false
                    _offlineMessage.value = null
                    _bannerType.value = BannerType.NONE
                    _isBackendDown.value = false
                    _isInternetDown.value = false

                    println("✅ Profile refreshed and cached successfully")
                }

                is ApiResult.Error -> {
                    val networkError = result.networkError

                    if (networkError is NetworkError.Unauthorized) {
                        _headerState.value = HeaderState.AuthError(networkError.userFriendlyMessage)
                        _profileState.value = ProfileLoadState.AuthError(networkError.userFriendlyMessage)
                        println("❌ Auth error - needs re-login: ${networkError.userFriendlyMessage}")
                    }
                    else if (!hasEverLoadedProfile) {
                        // No cache available and fetch failed
                        val message = networkError.userFriendlyMessage
                        _offlineMessage.value = message
                        _isOffline.value = true
                        _bannerType.value = BannerType.BACKEND_DOWN
                        println("⚠️ Network error, no cache available: $message")
                    }
                    else {
                        // Have cache but refresh failed - keep showing cache with warning
                        val message = "Using cached data. ${networkError.userFriendlyMessage}"
                        _offlineMessage.value = message
                        _isOffline.value = true
                        println("⚠️ Refresh failed, keeping cached data: $message")
                    }
                }

                null -> {
                    // Timeout occurred
                    if (!hasEverLoadedProfile) {
                        _offlineMessage.value = "Loading timeout. Using cached data if available."
                        _isOffline.value = true
                        _bannerType.value = BannerType.BACKEND_DOWN
                        println("⚠️ Timeout, using cached data")
                    } else {
                        _offlineMessage.value = "Connection timeout. Using cached data."
                        _isOffline.value = true
                        println("⚠️ Timeout, keeping cached data")
                    }
                }

                else -> {
                    println("⚠️ Unknown result: $result")
                }
            }
        } catch (e: Exception) {
            println("❌ Background refresh failed: ${e.message}")
            if (!hasEverLoadedProfile) {
                _offlineMessage.value = "Unable to load profile. Some features may be limited."
                _isOffline.value = true
                _bannerType.value = BannerType.BACKEND_DOWN
            }
        } finally {
            isFetching = false
        }
    }

    /**
     * Cache profile in Room database
     */
    private suspend fun cacheProfileInRoom(profile: CompleteProfile) {
        try {
            val profileJson = json.encodeToString(profile)
            var existingUser = userDao.getAuthenticatedUser()

            if (existingUser == null) {
                // Create new user entity
                val newUser = UserEntity(
                    uuid = profile.user.id,
                    email = profile.user.email,
                    firstName = profile.user.firstName,
                    lastName = profile.user.lastName,
                    userName = profile.user.fullName,
                    phone = profile.user.phoneNumber,
                    profileImage = profile.profileImageUrl,
                    isAuthenticated = true,
                    isOnboardingComplete = true,
                    role = profile.user.role,
                    accountType = profile.account.type.name,
                    accountId = profile.account.id,
                    accountName = profile.account.name,
                    completeProfileJson = profileJson,
                    completeProfileLastUpdated = System.currentTimeMillis()
                )
                userDao.insertUser(newUser)
                println("✅ New user created and cached in Room")
            } else {
                // Update existing user
                val updatedUser = existingUser.copy(
                    completeProfileJson = profileJson,
                    completeProfileLastUpdated = System.currentTimeMillis(),
                    firstName = profile.user.firstName,
                    lastName = profile.user.lastName,
                    userName = profile.user.fullName,
                    phone = profile.user.phoneNumber,
                    profileImage = profile.profileImageUrl,
                    role = profile.user.role,
                    accountType = profile.account.type.name,
                    accountName = profile.account.name
                )
                userDao.insertUser(updatedUser)
                println("✅ Existing user updated in Room")
            }
        } catch (e: Exception) {
            println("⚠️ Failed to cache profile in Room: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Create minimal profile from UserEntity for fallback
     */
    private fun createMinimalProfileFromUser(userEntity: UserEntity): CompleteProfile {
        val profileUser = ProfileUser(
            id = userEntity.uuid,
            userCode = userEntity.uuid.take(8),
            email = userEntity.email,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            fullName = userEntity.getFullName(),
            phoneNumber = userEntity.phone,
            profileImageUrl = userEntity.profileImage,
            status = UserStatus.ACTIVE,
            role = userEntity.role ?: "user",
            scope = null,
            planName = null
        )

        val profileAccount = ProfileAccount(
            id = userEntity.accountId ?: "",
            code = userEntity.uuid.take(8),
            name = userEntity.accountName ?: userEntity.getDisplayName(),
            type = if (userEntity.accountType == "ORGANIZATION")
                AccountType.ORGANIZATION
            else
                AccountType.INDIVIDUAL,
            status = AccountStatus.ACTIVE,
            isVerified = false,
            verifiedFeatures = emptyList(),
            createdAt = "",
            updatedAt = ""
        )

        val profileCompletion = ProfileCompletion(
            accountCompleted = true,
            profileCompleted = 30,
            documentsCompleted = 0
        )

        return CompleteProfile(
            user = profileUser,
            account = profileAccount,
            individualProfile = null,
            organizationProfile = null,
            professionalProfile = null,
            jobSeekerProfile = null,
            agentProfile = null,
            housingSeekerProfile = null,
            propertyOwnerProfile = null,
            beneficiaryProfile = null,
            employerProfile = null,
            verifications = emptyList(),
            completion = profileCompletion,
            createdAt = "",
            updatedAt = "",
            planName = null,
            scope = null
        )
    }

    /**
     * Update all UI states with profile data
     */
    private fun updateStatesWithProfile(profile: CompleteProfile) {
        println("🔍 [DEBUG] Profile user scope: ${profile.user.scope}")
        println("🔍 [DEBUG] Profile user planName: ${profile.user.planName}")
        println("🔍 [DEBUG] Profile root planName: ${profile.planName}")
        println("🔍 [DEBUG] Profile root scope: ${profile.scope}")

        _dashboardState.value = DashboardState.Success(profile)
        _profileState.value = ProfileLoadState.Success(profile)

        val shortName = profile.user.firstName.ifEmpty {
            profile.user.fullName.split(" ").firstOrNull() ?: "User"
        }

        _headerState.value = HeaderState.Success(
            headerUser = HeaderUser(
                id = profile.user.id,
                name = profile.displayName,
                shortName = shortName,
                avatarUrl = profile.profileImageUrl,
                isVerified = profile.account.isVerified,
                role = profile.user.role,
                accountType = profile.account.type.name,
                scope = profile.user.scope,
                planName = profile.planName ?: profile.user.planName
            )
        )
    }

    /**
     * Set default profile state when nothing is available
     */
    private fun setDefaultProfileState() {
        val defaultUser = ProfileUser(
            id = "default",
            userCode = "",
            email = "",
            firstName = "User",
            lastName = "",
            fullName = "User",
            phoneNumber = null,
            profileImageUrl = null,
            status = UserStatus.ACTIVE,
            role = "user",
            scope = null,
            planName = null
        )

        val defaultAccount = ProfileAccount(
            id = "",
            code = "",
            name = "User",
            type = AccountType.INDIVIDUAL,
            status = AccountStatus.ACTIVE,
            isVerified = false,
            verifiedFeatures = emptyList(),
            createdAt = "",
            updatedAt = ""
        )

        val defaultCompletion = ProfileCompletion(
            accountCompleted = false,
            profileCompleted = 0,
            documentsCompleted = 0
        )

        val defaultProfile = CompleteProfile(
            user = defaultUser,
            account = defaultAccount,
            individualProfile = null,
            organizationProfile = null,
            professionalProfile = null,
            jobSeekerProfile = null,
            agentProfile = null,
            housingSeekerProfile = null,
            propertyOwnerProfile = null,
            beneficiaryProfile = null,
            employerProfile = null,
            verifications = emptyList(),
            completion = defaultCompletion,
            createdAt = "",
            updatedAt = "",
            planName = null,
            scope = null
        )

        updateStatesWithProfile(defaultProfile)
    }

    // ======================================================
    // PUBLIC METHODS FOR UI
    // ======================================================

    /**
     * Force refresh profile data (ignore cache)
     */
    fun refreshProfile() {
        viewModelScope.launch {
            _offlineMessage.value = "Refreshing..."
            refreshProfileInBackground()
        }
    }

    /**
     * Dismiss offline warning message
     */
    fun dismissOfflineMessage() {
        _offlineMessage.value = null
        _bannerType.value = BannerType.NONE
        _isBackendDown.value = false
        _isInternetDown.value = false
        println("📡 [DashboardSharedViewModel] Offline message dismissed")
    }

    /**
     * Update offline message (keeps offline state as true)
     */
    fun updateOfflineMessage(message: String) {
        _offlineMessage.value = message
        _isOffline.value = true
        println("📡 [DashboardSharedViewModel] Offline message updated: $message")
    }

    /**
     * Update offline state with custom message and offline flag
     */
    fun updateOfflineState(message: String, isOffline: Boolean) {
        _offlineMessage.value = message
        _isOffline.value = isOffline
        println("📡 [DashboardSharedViewModel] Offline state updated: isOffline=$isOffline, message=$message")
    }

    /**
     * Select a tab by index
     */
    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    /**
     * Get current header user (synchronous, may be null)
     */
    fun getCurrentHeaderUser(): HeaderUser? {
        return when (val state = _headerState.value) {
            is HeaderState.Success -> state.headerUser
            else -> null
        }
    }

    /**
     * Get current profile (synchronous, may be null)
     */
    fun getCurrentProfile(): CompleteProfile? {
        return when (val state = _profileState.value) {
            is ProfileLoadState.Success -> state.profile
            else -> null
        }
    }

    /**
     * Check if profile data is available
     */
    fun hasProfileData(): Boolean {
        return _profileState.value is ProfileLoadState.Success
    }

    fun isLoading(): Boolean = _profileState.value is ProfileLoadState.Loading

    fun getErrorMessage(): String? {
        return when (val state = _profileState.value) {
            is ProfileLoadState.Error -> state.message
            is ProfileLoadState.AuthError -> state.message
            else -> null
        }
    }

    fun isAuthError(): Boolean {
        return _profileState.value is ProfileLoadState.AuthError ||
                _headerState.value is HeaderState.AuthError
    }

    fun hasError(): Boolean {
        return _profileState.value is ProfileLoadState.Error
    }

    /**
     * Debug method to check Room cache
     */
    suspend fun debugRoomCache() {
        val user = userDao.getAuthenticatedUser()
        println("🔍 [DEBUG] User in Room: ${user != null}")
        println("🔍 [DEBUG] User email: ${user?.email}")
        println("🔍 [DEBUG] completeProfileJson exists: ${user?.completeProfileJson != null}")
        println("🔍 [DEBUG] completeProfileJson length: ${user?.completeProfileJson?.length ?: 0}")

        if (user?.completeProfileJson != null) {
            try {
                val profile = json.decodeFromString<CompleteProfile>(user.completeProfileJson)
                println("🔍 [DEBUG] Cached profile name: ${profile.displayName}")
            } catch (e: Exception) {
                println("🔍 [DEBUG] Failed to parse: ${e.message}")
            }
        }
    }

    /**
     * Reset all states (useful for logout)
     */
    fun reset() {
        _dashboardState.value = DashboardState.Loading
        _profileState.value = ProfileLoadState.Loading
        _headerState.value = HeaderState.Loading
        _selectedTab.value = 0
        lastFetchTime = 0L
        isFetching = false
        hasEverLoadedProfile = false
        _isOffline.value = false
        _offlineMessage.value = null
        _bannerType.value = BannerType.NONE
        _isBackendDown.value = false
        _isInternetDown.value = false
        _isManualRetrying.value = false
    }

    /**
     * Perform logout - call API and clear all data
     */
    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                println("🚨 [DashboardSharedViewModel] User requested logout")

                // Clear Room database user data first
                userDao.deleteAll()

                // Reset ViewModel states
                reset()

                // Call TokenManager to handle API logout and local cleanup
                tokenManager.logout {
                    onComplete()
                }

            } catch (e: Exception) {
                println("❌ [DashboardSharedViewModel] Error during logout: ${e.message}")
                // Still try to clear local session
                tokenManager.clearLocalSession()
                onComplete()
            }
        }
    }

    /**
     * Quick logout with cleanup only (no API call)
     */
    fun logoutLocalOnly(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            userDao.deleteAll()
            reset()
            tokenManager.clearLocalSession()
            onComplete()
        }
    }

    fun updateCommonServicesState(state: CommonServicesUiState) {
        _commonServicesState.value = state
    }

    // ======================================================
    // MANUAL RETRY
    // ======================================================

// DashboardSharedViewModel.kt

    fun manualRetry(onResult: (RetryResult) -> Unit = {}) {
        viewModelScope.launch {
            if (_isRetrying.value) {
                println("⚠️ [DashboardViewModel] Manual retry already in progress")
                onResult(RetryResult.AlreadyInProgress)
                return@launch
            }

            _isRetrying.value = true
            println("🔄 [DashboardViewModel] Manual retry initiated")

            try {
                // ✅ Check network state before retry
                val hasNetwork = tokenManager.isNetworkAvailable()

                if (!hasNetwork) {
                    println("❌ [DashboardViewModel] No network connection, retry failed")
                    _isRetrying.value = false
                    // ✅ Keep the banner visible with same message
                    _offlineMessage.value = TokenManager.MSG_NO_INTERNET
                    _isOffline.value = true
                    _bannerType.value = BannerType.NO_INTERNET

                    val result = RetryResult.Failed
                    _retryResult.emit(result)
                    onResult(result)
                    return@launch
                }

                // Call TokenManager's manual retry
                val success = tokenManager.manualRetry()

                val result = if (success) {
                    println("✅ [DashboardViewModel] Manual retry successful")
                    // Refresh profile after successful retry
                    refreshProfileInBackground()
                    RetryResult.Success
                } else {
                    println("❌ [DashboardViewModel] Manual retry failed")
                    // ✅ Keep banner visible on failure
                    RetryResult.Failed
                }

                _retryResult.emit(result)
                onResult(result)
            } catch (e: Exception) {
                println("❌ [DashboardViewModel] Manual retry exception: ${e.message}")
                val result = RetryResult.Error(e.message ?: "Unknown error")
                _retryResult.emit(result)
                onResult(result)
            } finally {
                _isRetrying.value = false
            }
        }
    }

    /**
     * Quick manual retry without UI feedback (for internal use)
     */
    suspend fun manualRetrySync(): Boolean {
        return try {
            tokenManager.manualRetry()
        } catch (e: Exception) {
            println("❌ [DashboardSharedViewModel] Manual retry sync failed: ${e.message}")
            false
        }
    }
}

// ======================================================
// STATE SEALED CLASSES
// ======================================================

/**
 * Load state for full profile data
 */
sealed class ProfileLoadState {
    data object Loading : ProfileLoadState()
    data class Success(val profile: CompleteProfile) : ProfileLoadState()
    data class Error(
        val message: String,
        val isRecoverable: Boolean = true
    ) : ProfileLoadState()
    data class AuthError(val message: String) : ProfileLoadState()
}

/**
 * Load state for header (minimal) data
 */
sealed class HeaderState {
    data object Loading : HeaderState()
    data class Success(val headerUser: HeaderUser) : HeaderState()
    data class Error(
        val message: String,
        val isRecoverable: Boolean = true
    ) : HeaderState()
    data class AuthError(val message: String) : HeaderState()
}

/**
 * Minimal user data for header display
 */
data class HeaderUser(
    val id: String,
    val name: String,
    val shortName: String,
    val avatarUrl: String?,
    val isVerified: Boolean,
    val role: String,
    val scope: String?,
    val planName: String? = null,
    val accountType: String
) {
    val displayInitial: String
        get() = shortName.take(1).uppercase()

    val roleDisplayName: String
        get() = when {
            role.equals("admin", ignoreCase = true) -> "Administrator"
            accountType == "ORGANIZATION" -> "Organization"
            else -> "Member"
        }
}

