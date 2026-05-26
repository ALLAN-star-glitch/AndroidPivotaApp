package com.example.pivota.dashboard.data.repository

import com.example.pivota.core.database.dao.ServiceOfferingDao
import com.example.pivota.core.database.entity.ServiceOfferingsCacheMetadataEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.dashboard.data.mapper.ServiceOfferingCacheMapper
import com.example.pivota.dashboard.data.mapper.ServiceOfferingMapper
import com.example.pivota.dashboard.data.remote.ServiceOfferingsApiService
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse

class ServiceOfferingsRepositoryImpl @Inject constructor(
    private val apiService: ServiceOfferingsApiService,
    private val offeringDao: ServiceOfferingDao,
    private val networkMapper: ServiceOfferingMapper,
    private val cacheMapper: ServiceOfferingCacheMapper
) : ServiceOfferingsRepository {

    companion object {
        private const val CACHE_VALID_DURATION_MS = 5 * 60 * 1000 // 5 minutes
    }

    override suspend fun getOfferingsByCategory(
        categoryId: String,
        limit: Int,
        offset: Int,
        city: String?,
        minPrice: Double?,
        maxPrice: Double?
    ): ApiResult<ServiceOfferingsResponse> {
        // Try network first
        val networkResult = safeApiCall {
            apiService.getOfferingsByCategory(
                categoryId = categoryId,
                limit = limit,
                offset = offset,
                city = city,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }

        return when (networkResult) {
            is ApiResult.Success -> {
                val response = networkResult.data
                if (response.success && response.data != null) {
                    // Cache to Room
                    val entities = response.data.map { dto ->
                        cacheMapper.toEntity(dto, categoryId)
                    }
                    offeringDao.insertOfferings(entities)

                    // Save cache metadata
                    val metadata = ServiceOfferingsCacheMetadataEntity(
                        categoryId = categoryId,
                        lastUpdated = System.currentTimeMillis(),
                        totalCount = response.data.size
                    )
                    offeringDao.upsertCacheMetadata(metadata)

                    val domainResponse = networkMapper.toServiceOfferingsResponse(response)
                    ApiResult.Success(domainResponse)
                } else {
                    getCachedOfferings(categoryId)
                }
            }
            is ApiResult.Error -> {
                getCachedOfferings(categoryId)
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    override fun getOfferingsByCategoryStream(
        categoryId: String
    ): Flow<ServiceOfferingsResponse> {
        return offeringDao.getOfferingsByCategory(categoryId).map { entities ->
            val offerings = cacheMapper.toDomainList(entities)
            ServiceOfferingsResponse(
                success = true,
                message = "Cached data",
                code = "CACHED",
                data = offerings,
                pagination = null
            )
        }
    }

    override suspend fun refreshOfferingsByCategory(categoryId: String) {
        try {
            val metadata = offeringDao.getCacheMetadata(categoryId)
            val isCacheValid = metadata != null &&
                    (System.currentTimeMillis() - metadata.lastUpdated) < CACHE_VALID_DURATION_MS

            if (!isCacheValid) {
                val networkResult = safeApiCall {
                    apiService.getOfferingsByCategory(categoryId = categoryId)
                }

                if (networkResult is ApiResult.Success) {
                    val response = networkResult.data
                    if (response.success && response.data != null) {
                        val entities = response.data.map { dto ->
                            cacheMapper.toEntity(dto, categoryId)
                        }
                        offeringDao.insertOfferings(entities)
                        offeringDao.upsertCacheMetadata(
                            ServiceOfferingsCacheMetadataEntity(
                                categoryId = categoryId,
                                lastUpdated = System.currentTimeMillis(),
                                totalCount = response.data.size
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Silent fail - cache remains valid
        }
    }

    override suspend fun clearOfferingsCache() {
        val staleTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours
        offeringDao.deleteStaleOfferings(staleTime)
    }

    private suspend fun getCachedOfferings(
        categoryId: String
    ): ApiResult<ServiceOfferingsResponse> {
        return try {
            val cached = offeringDao.getOfferingsByCategoryList(categoryId)
            if (cached.isNotEmpty()) {
                val domainOfferings = cacheMapper.toDomainList(cached)
                ApiResult.Success(
                    ServiceOfferingsResponse(
                        success = true,
                        message = "Cached data",
                        code = "CACHED",
                        data = domainOfferings,
                        pagination = null
                    )
                )
            } else {
                ApiResult.Error(
                    networkError = NetworkError.Unknown(
                        originalMessage = "No cached data available"
                    ),
                    technicalMessage = "No cached data available for category: $categoryId"
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                networkError = NetworkError.Unknown(
                    originalMessage = e.message ?: "Failed to load cached data"
                ),
                technicalMessage = e.message
            )
        }
    }
}