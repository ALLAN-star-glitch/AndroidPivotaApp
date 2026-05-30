package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering

sealed class ServiceOfferingsUiState {
    object Loading : ServiceOfferingsUiState()
    data class Success(
        val offerings: List<ServiceOffering>,
        val hasMore: Boolean,
        val totalCount: Int,
        val isFromCache: Boolean = false,
        val warningMessage: String? = null
    ) : ServiceOfferingsUiState()
    data class Error(
        val message: String,
        val technicalMessage: String? = null
    ) : ServiceOfferingsUiState()
}