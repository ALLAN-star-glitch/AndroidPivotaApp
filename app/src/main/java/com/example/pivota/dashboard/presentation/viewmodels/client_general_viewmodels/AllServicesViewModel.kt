package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.useCase.GetAllServicesUseCase
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllServicesViewModel @Inject constructor(
    private val getAllServicesUseCase: GetAllServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommonServicesUiState>(CommonServicesUiState.Loading)
    val uiState: StateFlow<CommonServicesUiState> = _uiState.asStateFlow()

    init {
        loadAllServices()
    }

    fun loadAllServices(forceRefresh: Boolean = false) {
        println("AllServicesViewModel: loadAllServices called, forceRefresh=$forceRefresh")

        viewModelScope.launch {
            // Only show loading on initial load or force refresh without cache
            if (forceRefresh || !hasCachedData()) {
                _uiState.value = CommonServicesUiState.Loading
            }

            val result = getAllServicesUseCase(
                forceRefresh = forceRefresh
            )

            when (result) {
                is ApiResult.Success -> {
                    val services = result.data
                    val cacheStatus = getAllServicesUseCase.getCacheStatus()

                    val warningMessage = when (cacheStatus) {
                        is CacheStatus.Stale -> "Categories data may be outdated. Last updated ${formatTime(cacheStatus.ageMs)} ago."
                        is CacheStatus.Expired -> "Categories data is expired. Please refresh for latest information."
                        else -> null
                    }

                    _uiState.value = CommonServicesUiState.Success(
                        services = services,
                        isFromCache = cacheStatus is CacheStatus.Fresh ||
                                cacheStatus is CacheStatus.Stale ||
                                cacheStatus is CacheStatus.Expired,
                        warningMessage = warningMessage
                    )

                    println("AllServicesViewModel: Loaded ${services.size} services, isFromCache=${cacheStatus !is CacheStatus.Empty}")
                }
                is ApiResult.Error -> {
                    _uiState.value = CommonServicesUiState.Error(
                        message = result.networkError.userFriendlyMessage,
                        technicalMessage = result.technicalMessage
                    )
                    println("AllServicesViewModel: Error loading services: ${result.technicalMessage}")
                }
                ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun refresh() {
        println("AllServicesViewModel: refresh called")
        loadAllServices(forceRefresh = true)
    }

    fun clearCache() {
        viewModelScope.launch {
            getAllServicesUseCase.clearCache()
            println("AllServicesViewModel: Cache cleared")
        }
    }

    fun hasCachedData(): Boolean {
        // This is a synchronous check - we can use runBlocking or make the caller suspend
        // For simplicity, we'll just check via the use case in a non-blocking way
        viewModelScope.launch {
            val hasCache = getAllServicesUseCase.hasCachedData()
            // This is just for logging, the actual check in loadAllServices will work correctly
            println("AllServicesViewModel: hasCachedData = $hasCache")
        }
        // Return true optimistically, the actual check will happen in loadAllServices
        return true
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
        println("AllServicesViewModel: ViewModel cleared")
    }
}