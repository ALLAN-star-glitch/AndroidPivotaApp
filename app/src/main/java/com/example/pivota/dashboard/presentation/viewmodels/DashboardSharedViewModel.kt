package com.example.pivota.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.database.entity.UserEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.dashboard.domain.model.CompleteProfile
import com.example.pivota.dashboard.domain.model.ProfileAccount
import com.example.pivota.dashboard.domain.model.ProfileCompletion
import com.example.pivota.dashboard.domain.model.ProfileUser
import com.example.pivota.dashboard.domain.model.VerificationItem
import com.example.pivota.dashboard.domain.useCase.GetProfileUseCase
import com.example.pivota.dashboard.presentation.state.DashboardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class DashboardSharedViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val userDao: UserDao
) : ViewModel() {

    // ======================================================
    // COMBINED DASHBOARD STATE
    // ======================================================

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

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
    // TAB STATE
    // ======================================================

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Cache management
    private var lastFetchTime = 0L
    private val STALE_THRESHOLD_MS = 5 * 60 * 1000 // 5 minutes

    // ✅ Single Json instance for performance
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

    init {
        viewModelScope.launch {
            // Step 1: Load cached profile from Room immediately (instant display)
            loadCachedProfileFromRoom()

            // Step 2: Try to fetch fresh data in background
            refreshProfileInBackground()
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
                    // ✅ Use the single instance
                    val cachedProfile = json.decodeFromString<CompleteProfile>(userEntity.completeProfileJson)
                    updateStatesWithProfile(cachedProfile)
                    hasEverLoadedProfile = true

                    val cacheAge = System.currentTimeMillis() - userEntity.completeProfileLastUpdated
                    if (cacheAge > 24 * 60 * 60 * 1000L) {
                        val hoursOld = cacheAge / (1000 * 60 * 60)
                        _offlineMessage.value = "Using cached data from $hoursOld hours ago"
                        _isOffline.value = true
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
            val result = withTimeoutOrNull(8000L) {
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

                    println("✅ Profile refreshed and cached successfully")
                }

                is ApiResult.Error -> {
                    val networkError = result.networkError

                    // Check if this is an auth error (critical)
                    if (networkError is NetworkError.Unauthorized) {
                        // Critical - need to logout
                        _headerState.value = HeaderState.AuthError(networkError.userFriendlyMessage)
                        _profileState.value = ProfileLoadState.AuthError(networkError.userFriendlyMessage)
                        println("❌ Auth error - needs re-login: ${networkError.userFriendlyMessage}")
                    }
                    else if (!hasEverLoadedProfile) {
                        // No cache available and fetch failed
                        _offlineMessage.value = networkError.userFriendlyMessage
                        _isOffline.value = true
                        println("⚠️ Network error, no cache available: ${networkError.userFriendlyMessage}")
                    }
                    else {
                        // Have cache but refresh failed - keep showing cache with warning
                        _offlineMessage.value = "Using cached data. ${networkError.userFriendlyMessage}"
                        _isOffline.value = true
                        println("⚠️ Refresh failed, keeping cached data: ${networkError.userFriendlyMessage}")
                    }
                }

                null -> {
                    // Timeout occurred
                    if (!hasEverLoadedProfile) {
                        _offlineMessage.value = "Loading timeout. Using cached data if available."
                        _isOffline.value = true
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
            status = com.example.pivota.dashboard.domain.model.UserStatus.ACTIVE,
            role = userEntity.role ?: "user"
        )

        val profileAccount = ProfileAccount(
            id = userEntity.accountId ?: "",
            code = userEntity.uuid.take(8),
            name = userEntity.accountName ?: userEntity.getDisplayName(),
            type = if (userEntity.accountType == "ORGANIZATION")
                com.example.pivota.dashboard.domain.model.AccountType.ORGANIZATION
            else
                com.example.pivota.dashboard.domain.model.AccountType.INDIVIDUAL,
            status = com.example.pivota.dashboard.domain.model.AccountStatus.ACTIVE,
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
            updatedAt = ""
        )
    }

    /**
     * Update all UI states with profile data
     */
    private fun updateStatesWithProfile(profile: CompleteProfile) {
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
                accountType = profile.account.type.name
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
            status = com.example.pivota.dashboard.domain.model.UserStatus.ACTIVE,
            role = "user"
        )

        val defaultAccount = ProfileAccount(
            id = "",
            code = "",
            name = "User",
            type = com.example.pivota.dashboard.domain.model.AccountType.INDIVIDUAL,
            status = com.example.pivota.dashboard.domain.model.AccountStatus.ACTIVE,
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
            updatedAt = ""
        )

        updateStatesWithProfile(defaultProfile)
    }

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