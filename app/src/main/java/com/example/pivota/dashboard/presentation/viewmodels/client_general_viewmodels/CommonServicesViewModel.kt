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
    private var hasReceivedData = false  // Track if we've ever received data
    private var currentIsTablet = false

    init {
        loadCommonServices()
    }

    fun loadCommonServices(isTablet: Boolean = false) {
        if (hasLoaded && _uiState.value is CommonServicesUiState.Success && currentIsTablet == isTablet) {
            return
        }

        currentIsTablet = isTablet
        hasReceivedData = false

        val displayCount = if (isTablet) 12 else 8
        val perPillarCount = displayCount / 3  // 8/3=2, 12/3=4
        val remainder = displayCount % 3       // 8%3=2, 12%3=0

        getCommonServicesUseCase()
            .onEach { allServices ->
                if (allServices.isNotEmpty()) {
                    hasReceivedData = true

                    // Group by vertical
                    val housingServices = allServices.filter { it.vertical == "HOUSING" }
                    val jobsServices = allServices.filter { it.vertical == "JOBS" }
                    val socialServices = allServices.filter { it.vertical == "SOCIAL_SUPPORT" }

                    // Take from each pillar with distribution
                    val selected = mutableListOf<DiscoveryCategory>()

                    // For mobile (8 items): 3 from HOUSING, 3 from JOBS, 2 from SOCIAL
                    // For tablet (12 items): 4 from each pillar
                    selected.addAll(housingServices.take(perPillarCount + if (remainder > 0) 1 else 0))
                    selected.addAll(jobsServices.take(perPillarCount + if (remainder > 1) 1 else 0))
                    selected.addAll(socialServices.take(perPillarCount))

                    // Ensure we have exactly displayCount items
                    val finalServices = selected.take(displayCount)

                    _uiState.value = CommonServicesUiState.Success(finalServices)
                    println("🔄 UI Updated with fresh data at ${System.currentTimeMillis()}")
                    hasLoaded = true

                    println("✅ Loaded ${finalServices.size} common services (isTablet=$isTablet)")
                    println("📊 Distribution - HOUSING: ${finalServices.count { it.vertical == "HOUSING" }}, JOBS: ${finalServices.count { it.vertical == "JOBS" }}, SOCIAL: ${finalServices.count { it.vertical == "SOCIAL_SUPPORT" }}")
                } else if (hasReceivedData) {
                    _uiState.value = CommonServicesUiState.Error("No services available")
                    println("⚠️ No common services found")
                } else {
                    println("⏳ Database empty, waiting for network data...")
                }
            }
            .catch { error ->
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
        hasLoaded = false
        hasReceivedData = false
        currentIsTablet = isTablet
        _uiState.value = CommonServicesUiState.Loading
        forceNetworkRefresh()
    }

    private fun forceNetworkRefresh() {
        viewModelScope.launch {
            getCommonServicesUseCase.refresh()
            // Small delay to allow cache to update
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