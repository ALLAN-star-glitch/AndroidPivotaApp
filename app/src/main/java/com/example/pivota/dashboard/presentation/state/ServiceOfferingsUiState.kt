package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering


sealed class ServiceOfferingsUiState {
    object Loading : ServiceOfferingsUiState()
    data class Success(
        val offerings: List<ServiceOffering>,
        val hasMore: Boolean,
        val totalCount: Int
    ) : ServiceOfferingsUiState()
    data class Error(
        val message: String,
        val technicalMessage: String?
    ) : ServiceOfferingsUiState()
}