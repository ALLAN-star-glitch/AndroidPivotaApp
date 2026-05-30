package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.useCase.GetOfferingsByCategoryUseCase
import com.example.pivota.dashboard.domain.useCase.GetServiceOfferingByIdUseCase
import com.example.pivota.dashboard.presentation.state.ServiceOfferingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceOfferingsViewModel @Inject constructor(
    private val getOfferingsByCategoryUseCase: GetOfferingsByCategoryUseCase,
    private val getServiceOfferingByIdUseCase: GetServiceOfferingByIdUseCase
) : ViewModel() {

    private val _offeringsState = MutableStateFlow<ServiceOfferingsUiState>(ServiceOfferingsUiState.Loading)
    val offeringsState: StateFlow<ServiceOfferingsUiState> = _offeringsState.asStateFlow()

    private val _serviceDetailsState = MutableStateFlow<ServiceDetailsState>(ServiceDetailsState.Loading)
    val serviceDetailsState: StateFlow<ServiceDetailsState> = _serviceDetailsState.asStateFlow()

    // Simple in-memory cache for quick access (repository also has Room cache)
    private val cachedOfferings = mutableMapOf<String, ServiceOffering>()

    private var currentCategoryId: String = ""
    private var currentLimit: Int = 20
    private var currentOffset: Int = 0
    private var currentCity: String? = null
    private var currentMinPrice: Double? = null
    private var currentMaxPrice: Double? = null
    private var hasMoreData: Boolean = true

    fun loadOfferings(
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        isLoadMore: Boolean = false,
        forceRefresh: Boolean = false
    ) {
        // Store current parameters for pagination
        currentCategoryId = categoryId
        currentLimit = limit
        currentOffset = offset
        currentCity = city
        currentMinPrice = minPrice
        currentMaxPrice = maxPrice

        viewModelScope.launch {
            if (!isLoadMore) {
                _offeringsState.update { ServiceOfferingsUiState.Loading }
            }

            when (val result = getOfferingsByCategoryUseCase(
                categoryId = categoryId,
                limit = limit,
                offset = offset,
                city = city,
                minPrice = minPrice,
                maxPrice = maxPrice,
                forceRefresh = forceRefresh
            )) {
                is ApiResult.Success -> {
                    val offerings = result.data.data
                    val pagination = result.data.pagination
                    val isFromCache = result.data.message == "Cached data"

                    hasMoreData = pagination?.hasMore ?: false

                    val currentState = _offeringsState.value
                    val existingOfferings = if (isLoadMore && currentState is ServiceOfferingsUiState.Success) {
                        currentState.offerings
                    } else {
                        emptyList()
                    }

                    val allOfferings = existingOfferings + offerings

                    // Cache individual offerings in memory
                    offerings.forEach { offering ->
                        cachedOfferings[offering.id] = offering
                    }

                    // Get cache status for warning messages
                    val cacheStatus = if (isFromCache) {
                        getOfferingsByCategoryUseCase.getCacheStatus(categoryId)
                    } else null

                    val warningMessage = when {
                        result.data.code == "CACHED" && cacheStatus is CacheStatus.Stale ->
                            "Showing cached data that may be outdated"
                        result.data.code == "CACHED" && cacheStatus is CacheStatus.Expired ->
                            "Showing cached data. Please refresh for latest information"
                        else -> null
                    }

                    _offeringsState.update {
                        ServiceOfferingsUiState.Success(
                            offerings = allOfferings,
                            hasMore = hasMoreData,
                            totalCount = pagination?.total ?: allOfferings.size,
                            isFromCache = isFromCache,
                            warningMessage = warningMessage
                        )
                    }
                }
                is ApiResult.Error -> {
                    _offeringsState.update {
                        ServiceOfferingsUiState.Error(
                            message = result.networkError.userFriendlyMessage,
                            technicalMessage = result.technicalMessage
                        )
                    }
                }
                ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun loadServiceOffering(serviceId: String, forceRefresh: Boolean = false) {
        // Check in-memory cache first (fastest)
        if (!forceRefresh && cachedOfferings[serviceId] != null) {
            val cachedOffering = cachedOfferings[serviceId]!!
            println("📦 [ViewModel] Using in-memory cached service offering: $serviceId")
            _serviceDetailsState.update { ServiceDetailsState.Success(serviceOffering = cachedOffering, isFromCache = true) }
            return
        }

        println("🌐 [ViewModel] Fetching service offering from network: $serviceId")

        viewModelScope.launch {
            _serviceDetailsState.update { ServiceDetailsState.Loading }

            val result = getServiceOfferingByIdUseCase(serviceId, forceRefresh)

            when (result) {
                is ApiResult.Success -> {
                    // Cache in memory
                    cachedOfferings[serviceId] = result.data
                    _serviceDetailsState.update {
                        ServiceDetailsState.Success(
                            serviceOffering = result.data,
                            isFromCache = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    // Check if we have any cached version even if expired
                    val cached = cachedOfferings[serviceId]
                    if (cached != null) {
                        _serviceDetailsState.update {
                            ServiceDetailsState.Success(
                                serviceOffering = cached,
                                isFromCache = true,
                                warningMessage = "Unable to refresh. Showing cached data."
                            )
                        }
                    } else {
                        _serviceDetailsState.update {
                            ServiceDetailsState.Error(
                                message = result.networkError.userFriendlyMessage,
                                technicalMessage = result.technicalMessage
                            )
                        }
                    }
                }
                ApiResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun loadMore() {
        if (hasMoreData && _offeringsState.value is ServiceOfferingsUiState.Success) {
            val nextOffset = currentOffset + currentLimit
            loadOfferings(
                categoryId = currentCategoryId,
                limit = currentLimit,
                offset = nextOffset,
                city = currentCity,
                minPrice = currentMinPrice,
                maxPrice = currentMaxPrice,
                isLoadMore = true
            )
        }
    }

    fun refreshOfferings() {
        currentOffset = 0
        hasMoreData = true
        loadOfferings(
            categoryId = currentCategoryId,
            limit = currentLimit,
            offset = 0,
            city = currentCity,
            minPrice = currentMinPrice,
            maxPrice = currentMaxPrice,
            isLoadMore = false,
            forceRefresh = true
        )
    }

    fun refreshServiceDetails(serviceId: String) {
        loadServiceOffering(serviceId, forceRefresh = true)
    }

    fun clearState() {
        _offeringsState.update { ServiceOfferingsUiState.Loading }
        currentCategoryId = ""
        currentOffset = 0
        hasMoreData = true
    }

    fun clearServiceDetailsState() {
        _serviceDetailsState.update { ServiceDetailsState.Loading }
    }

    fun clearCache() {
        cachedOfferings.clear()
        // Also clear repository cache if needed
        viewModelScope.launch {
            getOfferingsByCategoryUseCase.clearAllCache()
        }
        println("🗑️ [ViewModel] Cache cleared")
    }

    override fun onCleared() {
        super.onCleared()
        cachedOfferings.clear()
        _offeringsState.update { ServiceOfferingsUiState.Loading }
        _serviceDetailsState.update { ServiceDetailsState.Loading }
    }
}

sealed class ServiceDetailsState {
    object Loading : ServiceDetailsState()
    data class Success(
        val serviceOffering: ServiceOffering,
        val isFromCache: Boolean = false,
        val warningMessage: String? = null
    ) : ServiceDetailsState()
    data class Error(val message: String, val technicalMessage: String? = null) : ServiceDetailsState()
}