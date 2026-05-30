package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.useCase.GetCommonServicesUseCase
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonServicesViewModel @Inject constructor(
    private val getCommonServicesUseCase: GetCommonServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommonServicesUiState>(CommonServicesUiState.Loading)
    val uiState: StateFlow<CommonServicesUiState> = _uiState.asStateFlow()

    private var currentIsTablet = false
    private var currentDisplayCount = 0
    private var isLoading = false

    init {
        loadCommonServices()
    }

    fun loadCommonServices(isTablet: Boolean = false, forceRefresh: Boolean = false) {
        val displayCount = if (isTablet) 12 else 8

        // If tablet state changed, force refresh
        val tabletStateChanged = currentIsTablet != isTablet
        val shouldForceRefresh = forceRefresh || tabletStateChanged

        // Check if we already have the correct data loaded
        val hasCorrectData = !shouldForceRefresh &&
                _uiState.value is CommonServicesUiState.Success &&
                currentIsTablet == isTablet &&
                currentDisplayCount == displayCount

        if (hasCorrectData && !forceRefresh) {
            println("CommonServicesViewModel: Already have correct data ($displayCount items), skipping")
            return
        }

        if (isLoading && !shouldForceRefresh) {
            println("CommonServicesViewModel: Already loading, skipping duplicate request")
            return
        }

        println("CommonServicesViewModel: Loading common services - isTablet=$isTablet, displayCount=$displayCount, forceRefresh=$forceRefresh")

        isLoading = true
        currentIsTablet = isTablet
        currentDisplayCount = displayCount

        viewModelScope.launch {
            // Only show loading if we don't have cached data or forcing refresh
            val hasCache = getCommonServicesUseCase.hasCachedData()
            if (shouldForceRefresh || !hasCache) {
                _uiState.value = CommonServicesUiState.Loading
            }

            val result = getCommonServicesUseCase(forceRefresh = shouldForceRefresh)

            when (result) {
                is ApiResult.Success -> {
                    val allServices = result.data
                    val cacheStatus = getCommonServicesUseCase.getCacheStatus()
                    val isFromCache = cacheStatus !is CacheStatus.Empty

                    if (allServices.isNotEmpty()) {
                        val selectedServices = selectServicesByVertical(
                            allServices = allServices,
                            displayCount = displayCount,
                            isTablet = isTablet
                        )

                        val warningMessage = when (cacheStatus) {
                            is CacheStatus.Stale -> "Services may be outdated. Last updated ${formatTime(cacheStatus.ageMs)} ago."
                            is CacheStatus.Expired -> "Services data is expired. Pull to refresh."
                            else -> null
                        }

                        _uiState.value = CommonServicesUiState.Success(
                            services = selectedServices,
                            isFromCache = isFromCache,
                            warningMessage = warningMessage
                        )

                        println("CommonServicesViewModel: Loaded ${selectedServices.size} common services")
                        println("Distribution - HOUSING: ${selectedServices.count { it.vertical == "HOUSING" }}, " +
                                "JOBS: ${selectedServices.count { it.vertical == "JOBS" }}, " +
                                "SOCIAL: ${selectedServices.count { it.vertical == "SOCIAL_SUPPORT" }}")
                    } else {
                        _uiState.value = CommonServicesUiState.Error("No services available")
                        println("CommonServicesViewModel: No services found")
                    }
                    isLoading = false
                }
                is ApiResult.Error -> {
                    // Check if we have any cached data to show
                    val hasCachedData = getCommonServicesUseCase.hasCachedData()
                    if (hasCachedData) {
                        // Try to load cached data
                        val cachedResult = getCommonServicesUseCase(forceRefresh = false)
                        if (cachedResult is ApiResult.Success && cachedResult.data.isNotEmpty()) {
                            val selectedServices = selectServicesByVertical(
                                allServices = cachedResult.data,
                                displayCount = displayCount,
                                isTablet = isTablet
                            )
                            _uiState.value = CommonServicesUiState.Success(
                                services = selectedServices,
                                isFromCache = true,
                                warningMessage = "Network error. Showing cached data."
                            )
                            println("CommonServicesViewModel: Showing cached data due to network error")
                        } else {
                            _uiState.value = CommonServicesUiState.Error(
                                message = result.networkError.userFriendlyMessage,
                                technicalMessage = result.technicalMessage
                            )
                        }
                    } else {
                        _uiState.value = CommonServicesUiState.Error(
                            message = result.networkError.userFriendlyMessage,
                            technicalMessage = result.technicalMessage
                        )
                    }
                    println("CommonServicesViewModel: Error loading services: ${result.technicalMessage}")
                    isLoading = false
                }
                ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun refresh(isTablet: Boolean = false) {
        println("CommonServicesViewModel: Refresh called - isTablet=$isTablet")
        loadCommonServices(isTablet = isTablet, forceRefresh = true)
    }

    fun getServiceById(id: String): DiscoveryCategory? {
        return when (val state = _uiState.value) {
            is CommonServicesUiState.Success -> state.services.find { it.id == id }
            else -> null
        }
    }

    fun getServicesByVertical(vertical: String): List<DiscoveryCategory> {
        return when (val state = _uiState.value) {
            is CommonServicesUiState.Success -> state.services.filter { it.vertical == vertical }
            else -> emptyList()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            getCommonServicesUseCase.clearCache()
            println("CommonServicesViewModel: Cache cleared")
        }
    }

    private fun selectServicesByVertical(
        allServices: List<DiscoveryCategory>,
        displayCount: Int,
        isTablet: Boolean
    ): List<DiscoveryCategory> {
        val perPillarCount = displayCount / 3
        val remainder = displayCount % 3

        val housingServices = allServices.filter { it.vertical == "HOUSING" }
        val jobsServices = allServices.filter { it.vertical == "JOBS" }
        val socialServices = allServices.filter { it.vertical == "SOCIAL_SUPPORT" }

        val selected = mutableListOf<DiscoveryCategory>()

        selected.addAll(housingServices.take(perPillarCount + if (remainder > 0) 1 else 0))
        selected.addAll(jobsServices.take(perPillarCount + if (remainder > 1) 1 else 0))
        selected.addAll(socialServices.take(perPillarCount))

        return selected.take(displayCount)
    }

    private fun formatTime(ms: Long): String {
        val hours = ms / (60 * 60 * 1000)
        val days = hours / 24

        return when {
            days > 0 -> "$days day(s)"
            hours > 0 -> "$hours hour(s)"
            else -> "${ms / (60 * 1000)} minute(s)"
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("CommonServicesViewModel: ViewModel cleared")
    }
}