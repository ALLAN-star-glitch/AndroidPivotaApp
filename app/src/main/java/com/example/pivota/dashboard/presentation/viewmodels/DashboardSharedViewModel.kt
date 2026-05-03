package com.example.pivota.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.dashboard.domain.model.CompleteProfile
import com.example.pivota.dashboard.domain.useCase.GetProfileUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Shared ViewModel for the Dashboard parent screen.
 * Fetches and caches profile data once for use across all dashboard tabs.
 *
 * This ViewModel is scoped to the DashboardScaffold and survives configuration changes.
 * Child screens (Dashboard, Discover, Profile) can access this ViewModel via hiltViewModel()
 * with the same parent scope.
 */
@HiltViewModel
class DashboardSharedViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    // ======================================================
    // PROFILE STATE (Full profile - used by ProfileScreen)
    // ======================================================

    private val _profileState = MutableStateFlow<ProfileLoadState>(ProfileLoadState.Loading)
    val profileState: StateFlow<ProfileLoadState> = _profileState.asStateFlow()

    // ======================================================
    // HEADER STATE (Minimal - used by ReusableHeader)
    // ======================================================

    private val _headerState = MutableStateFlow<HeaderState>(HeaderState.Loading)
    val headerState: StateFlow<HeaderState> = _headerState.asStateFlow()

    // ======================================================
    // TAB STATE (Shared across dashboard)
    // ======================================================

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Cache timestamp to avoid unnecessary refreshes
    private var lastFetchTime = 0L
    private val STALE_THRESHOLD_MS = 5 * 60 * 1000 // 5 minutes

    init {
        loadProfile()
    }

    /**
     * Load profile data. Called once when dashboard is created.
     * Subsequent calls within stale threshold will be ignored.
     */
    fun loadProfile(forceRefresh: Boolean = false) {
        val now = System.currentTimeMillis()

        // Skip if we have fresh data and not forcing refresh
        if (!forceRefresh &&
            _profileState.value is ProfileLoadState.Success &&
            now - lastFetchTime < STALE_THRESHOLD_MS) {
            return
        }

        viewModelScope.launch {
            _profileState.update { ProfileLoadState.Loading }
            _headerState.update { HeaderState.Loading }

            when (val result = getProfileUseCase()) {
                is ApiResult.Success -> {
                    val profile = result.data
                    lastFetchTime = System.currentTimeMillis()

                    _profileState.update { ProfileLoadState.Success(profile) }
                    _headerState.update {
                        HeaderState.Success(
                            headerUser = HeaderUser(
                                id = profile.user.id,
                                name = profile.displayName,
                                shortName = profile.user.shortName,
                                avatarUrl = profile.profileImageUrl,
                                isVerified = profile.account.isVerified,
                                role = profile.user.role,
                                accountType = profile.account.type.name
                            )
                        )
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = result.networkError.userFriendlyMessage
                    val isRecoverable = when (result.networkError) {
                        NetworkError.NoInternet -> true
                        NetworkError.Timeout -> true
                        NetworkError.ServerUnreachable -> true
                        else -> false
                    }

                    _profileState.update {
                        ProfileLoadState.Error(
                            message = errorMessage,
                            isRecoverable = isRecoverable
                        )
                    }
                    _headerState.update {
                        HeaderState.Error(
                            message = errorMessage,
                            isRecoverable = isRecoverable
                        )
                    }
                }
                ApiResult.Loading -> {
                    // Already handled by initial Loading state
                }
            }
        }
    }

    /**
     * Force refresh profile data (ignore cache)
     * Call this when user manually refreshes or after profile update
     */
    fun refreshProfile() {
        loadProfile(forceRefresh = true)
    }

    /**
     * Select a tab by index
     * @param index 0 = Dashboard, 1 = Discover, 2 = Profile
     */
    fun selectTab(index: Int) {
        _selectedTab.update { index }
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

    /**
     * Check if profile is currently loading
     */
    fun isLoading(): Boolean {
        return _profileState.value is ProfileLoadState.Loading
    }

    /**
     * Check if there was an error
     */
    fun hasError(): Boolean {
        return _profileState.value is ProfileLoadState.Error
    }

    /**
     * Reset all states (useful for logout)
     */
    fun reset() {
        _profileState.update { ProfileLoadState.Loading }
        _headerState.update { HeaderState.Loading }
        _selectedTab.update { 0 }
        lastFetchTime = 0L
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
}

// ======================================================
// MINIMAL HEADER MODEL
// ======================================================

/**
 * Minimal user data for header display.
 * This is a lightweight model that doesn't expose full profile details.
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