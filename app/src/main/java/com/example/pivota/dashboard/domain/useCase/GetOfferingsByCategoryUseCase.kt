package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult

import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse
import javax.inject.Inject

class GetOfferingsByCategoryUseCase @Inject constructor(
    private val repository: ServiceOfferingsRepository
) {

    suspend operator fun invoke(
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): ApiResult<ServiceOfferingsResponse> {
        return repository.getOfferingsByCategory(
            categoryId = categoryId,
            limit = limit,
            offset = offset,
            city = city,
            minPrice = minPrice,
            maxPrice = maxPrice
        )
    }
}