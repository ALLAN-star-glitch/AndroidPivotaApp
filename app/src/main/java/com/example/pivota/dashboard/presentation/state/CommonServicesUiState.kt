package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory

sealed class CommonServicesUiState {
    object Loading : CommonServicesUiState()
    data class Success(
        val services: List<DiscoveryCategory>,
        val isFromCache: Boolean = false,
        val warningMessage: String? = null
    ) : CommonServicesUiState()
    data class Error(
        val message: String,
        val technicalMessage: String? = null
    ) : CommonServicesUiState()
}