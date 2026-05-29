package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import javax.inject.Inject

class CreateServiceOfferingUseCase @Inject constructor(
    private val repository: ServiceOfferingsRepository
) {

    suspend operator fun invoke(
        request: CreateServiceOfferingRequestDto
    ): ApiResult<ServiceOfferingsResponse> {
        return repository.createServiceOffering(request)
    }
}