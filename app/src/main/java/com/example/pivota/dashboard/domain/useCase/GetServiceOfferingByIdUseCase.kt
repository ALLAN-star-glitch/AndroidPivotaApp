package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import javax.inject.Inject

class GetServiceOfferingByIdUseCase @Inject constructor(
    private val repository: ServiceOfferingsRepository
) {
    suspend operator fun invoke(
        serviceId: String,
        forceRefresh: Boolean = false
    ): ApiResult<ServiceOffering> {
        return repository.getServiceOfferingById(serviceId, forceRefresh)
    }
}