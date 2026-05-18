package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory

sealed class CommonServicesUiState {
    data object Loading : CommonServicesUiState()
    data class Success(val services: List<DiscoveryCategory>) : CommonServicesUiState()
    data class Error(val message: String) : CommonServicesUiState()
}