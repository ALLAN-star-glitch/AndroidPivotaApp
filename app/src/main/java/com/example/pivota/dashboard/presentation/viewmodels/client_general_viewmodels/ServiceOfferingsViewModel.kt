package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.useCase.GetOfferingsByCategoryUseCase
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
    private val getOfferingsByCategoryUseCase: GetOfferingsByCategoryUseCase
) : ViewModel() {

    private val _offeringsState = MutableStateFlow<ServiceOfferingsUiState>(ServiceOfferingsUiState.Loading)
    val offeringsState: StateFlow<ServiceOfferingsUiState> = _offeringsState.asStateFlow()

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
        isLoadMore: Boolean = false
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
                maxPrice = maxPrice
            )) {
                is ApiResult.Success -> {
                    val offerings = result.data.data
                    val pagination = result.data.pagination

                    hasMoreData = pagination?.hasMore ?: false

                    val currentState = _offeringsState.value
                    val existingOfferings = if (isLoadMore && currentState is ServiceOfferingsUiState.Success) {
                        currentState.offerings
                    } else {
                        emptyList()
                    }

                    val allOfferings = existingOfferings + offerings

                    _offeringsState.update {
                        ServiceOfferingsUiState.Success(
                            offerings = allOfferings,
                            hasMore = hasMoreData,
                            totalCount = pagination?.total ?: allOfferings.size
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
            isLoadMore = false
        )
    }

    fun clearState() {
        _offeringsState.update { ServiceOfferingsUiState.Loading }
        currentCategoryId = ""
        currentOffset = 0
        hasMoreData = true
    }
}