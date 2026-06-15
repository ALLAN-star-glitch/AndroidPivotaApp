package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.domain.model.listings_models.professionals.GetAllOfferingsParams
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse
import kotlinx.coroutines.flow.Flow

interface ServiceOfferingsRepository {
    suspend fun getOfferingsByCategory(
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        forceRefresh: Boolean = false
    ): ApiResult<ServiceOfferingsResponse>

    // ======================================================
    // NEW: Get all offerings across all categories
    // ======================================================

    suspend fun getAllOfferings(
        params: GetAllOfferingsParams = GetAllOfferingsParams(),
        forceRefresh: Boolean = false
    ): ApiResult<ServiceOfferingsResponse>

    fun getOfferingsByCategoryStream(
        categoryId: String
    ): Flow<ServiceOfferingsResponse>

    suspend fun createServiceOffering(
        request: CreateServiceOfferingRequestDto
    ): ApiResult<ServiceOfferingsResponse>

    suspend fun refreshOfferingsByCategory(categoryId: String, force: Boolean = false)

    // ======================================================
    // NEW: Refresh all offerings
    // ======================================================

    suspend fun refreshAllOfferings(force: Boolean = false)

    suspend fun clearOfferingsCache()

    suspend fun clearAllCache()

    suspend fun getServiceOfferingById(
        serviceId: String,
        forceRefresh: Boolean = false
    ): ApiResult<ServiceOffering>

    suspend fun getCacheStatus(categoryId: String): CacheStatus

    // ======================================================
    // NEW: Get cache status for all offerings
    // ======================================================

    suspend fun getAllOfferingsCacheStatus(): CacheStatus
}

// Cache status sealed class
sealed class CacheStatus {
    object Empty : CacheStatus()
    data class Fresh(val ageMs: Long) : CacheStatus()
    data class Stale(val ageMs: Long) : CacheStatus()
    data class Expired(val ageMs: Long) : CacheStatus()
}