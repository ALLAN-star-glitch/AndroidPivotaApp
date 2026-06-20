package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.housing.GetAllHousingParams
import com.example.pivota.dashboard.domain.model.listings_models.housing.HousingUiState
import com.example.pivota.dashboard.domain.useCase.GetAllHousingListingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HousingViewModel @Inject constructor(
    private val getAllHousingListingsUseCase: GetAllHousingListingsUseCase
) : ViewModel() {

    private val _housingState = MutableStateFlow<HousingUiState>(HousingUiState.Loading)
    val housingState: StateFlow<HousingUiState> = _housingState.asStateFlow()

    // Pagination state
    private var currentParams: GetAllHousingParams = GetAllHousingParams()
    private var hasMoreData: Boolean = true
    private var currentOffset: Int = 0

    /**
     * Load all housing listings with pagination and filters
     */
    fun loadHousingListings(
        params: GetAllHousingParams = GetAllHousingParams(),
        isLoadMore: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isLoadMore) {
                _housingState.update { HousingUiState.Loading }
                currentOffset = 0
                currentParams = params
                hasMoreData = true
            }

            val requestParams = if (isLoadMore) {
                params.copy(offset = currentOffset)
            } else {
                params
            }

            val result = getAllHousingListingsUseCase(requestParams)

            when (result) {
                is ApiResult.Success -> {
                    val houses = result.data.data
                    val pagination = result.data.pagination

                    hasMoreData = pagination?.hasMore ?: false
                    currentOffset = requestParams.offset + requestParams.limit

                    val currentState = _housingState.value
                    val existingHouses = if (isLoadMore && currentState is HousingUiState.Success) {
                        currentState.houses
                    } else {
                        emptyList()
                    }

                    _housingState.update {
                        HousingUiState.Success(
                            houses = existingHouses + houses,
                            hasMore = hasMoreData,
                            totalCount = pagination?.total ?: (existingHouses + houses).size
                        )
                    }
                }
                is ApiResult.Error -> {
                    _housingState.update {
                        HousingUiState.Error(
                            message = result.technicalMessage ?: "Failed to load housing listings"
                        )
                    }
                }
                ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    /**
     * Load more housing listings for pagination
     */
    fun loadMore() {
        if (hasMoreData && _housingState.value is HousingUiState.Success) {
            loadHousingListings(
                params = currentParams.copy(offset = currentOffset),
                isLoadMore = true
            )
        }
    }

    /**
     * Refresh housing listings (pull to refresh)
     */
    fun refreshHousingListings() {
        currentOffset = 0
        hasMoreData = true
        loadHousingListings(
            params = currentParams.copy(offset = 0),
            isLoadMore = false
        )
    }

    /**
     * Clear state
     */
    fun clearState() {
        _housingState.update { HousingUiState.Loading }
        currentOffset = 0
        hasMoreData = true
    }

    override fun onCleared() {
        super.onCleared()
        _housingState.update { HousingUiState.Loading }
    }
}

