package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse
import kotlinx.coroutines.flow.Flow

interface ServiceOfferingsRepository {
    suspend fun getOfferingsByCategory(
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): ApiResult<ServiceOfferingsResponse>

    fun getOfferingsByCategoryStream(
        categoryId: String
    ): Flow<ServiceOfferingsResponse>

    suspend fun refreshOfferingsByCategory(categoryId: String)

    suspend fun clearOfferingsCache()
}