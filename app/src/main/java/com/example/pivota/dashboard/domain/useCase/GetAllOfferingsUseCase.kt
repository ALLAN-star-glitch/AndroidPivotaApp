package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.GetAllOfferingsParams
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import javax.inject.Inject

class GetAllOfferingsUseCase @Inject constructor(
    private val repository: ServiceOfferingsRepository
) {
    suspend operator fun invoke(
        params: GetAllOfferingsParams = GetAllOfferingsParams(),
        forceRefresh: Boolean = false
    ): ApiResult<ServiceOfferingsResponse> {
        return repository.getAllOfferings(params, forceRefresh)
    }

    suspend fun getCacheStatus(): CacheStatus {
        return repository.getAllOfferingsCacheStatus()
    }

    suspend fun clearAllCache() {
        repository.clearAllCache()
    }
}