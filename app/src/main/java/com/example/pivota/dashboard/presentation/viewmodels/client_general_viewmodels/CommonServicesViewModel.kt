package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.useCase.GetCommonServicesUseCase
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class CommonServicesViewModel @Inject constructor(
    private val getCommonServicesUseCase: GetCommonServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommonServicesUiState>(CommonServicesUiState.Loading)
    val uiState: StateFlow<CommonServicesUiState> = _uiState.asStateFlow()

    private var hasLoaded = false
    private var hasReceivedData = false
    private var currentIsTablet = false
    private var currentDisplayCount = 0
    private var isLoading = false
    private var currentJob: kotlinx.coroutines.Job? = null

    init {
        loadCommonServices()
    }

    fun loadCommonServices(isTablet: Boolean = false) {
        val displayCount = if (isTablet) 12 else 8

        // If tablet state changed, cancel the current load and reload
        if (currentIsTablet != isTablet && isLoading) {
            println("📱 [ViewModel] Tablet state changed from $currentIsTablet to $isTablet - cancelling current load")
            currentJob?.cancel()
            isLoading = false
        }

        // Check if we already have the correct data loaded
        val hasCorrectData = hasLoaded &&
                _uiState.value is CommonServicesUiState.Success &&
                currentIsTablet == isTablet &&
                currentDisplayCount == displayCount

        if (hasCorrectData) {
            println("📱 [ViewModel] Already have correct data ($displayCount items), skipping")
            return
        }

        println("📱 [ViewModel] Loading common services - isTablet=$isTablet, displayCount=$displayCount")

        // Cancel any existing job
        currentJob?.cancel()
        isLoading = true
        currentIsTablet = isTablet
        currentDisplayCount = displayCount
        hasReceivedData = false

        val perPillarCount = displayCount / 3
        val remainder = displayCount % 3

        currentJob = getCommonServicesUseCase()
            .onEach { allServices ->
                if (allServices.isNotEmpty()) {
                    hasReceivedData = true

                    val housingServices = allServices.filter { it.vertical == "HOUSING" }
                    val jobsServices = allServices.filter { it.vertical == "JOBS" }
                    val socialServices = allServices.filter { it.vertical == "SOCIAL_SUPPORT" }

                    val selected = mutableListOf<DiscoveryCategory>()

                    selected.addAll(housingServices.take(perPillarCount + if (remainder > 0) 1 else 0))
                    selected.addAll(jobsServices.take(perPillarCount + if (remainder > 1) 1 else 0))
                    selected.addAll(socialServices.take(perPillarCount))

                    val finalServices = selected.take(displayCount)

                    _uiState.value = CommonServicesUiState.Success(finalServices)
                    println("🔄 UI Updated with fresh data at ${System.currentTimeMillis()}")
                    hasLoaded = true
                    isLoading = false

                    println("✅ Loaded ${finalServices.size} common services (isTablet=$isTablet)")
                    println("📊 Distribution - HOUSING: ${finalServices.count { it.vertical == "HOUSING" }}, JOBS: ${finalServices.count { it.vertical == "JOBS" }}, SOCIAL: ${finalServices.count { it.vertical == "SOCIAL_SUPPORT" }}")
                } else if (hasReceivedData) {
                    isLoading = false
                    _uiState.value = CommonServicesUiState.Error("No services available")
                    println("⚠️ No common services found")
                } else {
                    println("⏳ Database empty, waiting for network data...")
                }
            }
            .catch { error ->
                isLoading = false
                if (hasReceivedData) {
                    println("⚠️ Error but keeping cached data: ${error.message}")
                } else {
                    _uiState.value = CommonServicesUiState.Error(error.message ?: "Failed to load services")
                    println("❌ Failed to load common services: ${error.message}")
                }
            }
            .launchIn(viewModelScope)
    }

    fun refresh(isTablet: Boolean = false) {
        println("📱 [ViewModel] Refresh called - isTablet=$isTablet")
        hasLoaded = false
        hasReceivedData = false
        currentIsTablet = isTablet
        currentDisplayCount = if (isTablet) 12 else 8
        isLoading = false
        currentJob?.cancel()
        _uiState.value = CommonServicesUiState.Loading
        forceNetworkRefresh()
    }

    private fun forceNetworkRefresh() {
        viewModelScope.launch {
            getCommonServicesUseCase.refresh()
            delay(500)
            loadCommonServices(currentIsTablet)
        }
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
}