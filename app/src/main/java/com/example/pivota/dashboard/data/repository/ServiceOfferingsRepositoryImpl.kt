package com.example.pivota.dashboard.data.repository

import android.util.Log
import com.example.pivota.core.database.dao.ServiceOfferingDao
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.core.utils.NetworkMonitor
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.data.mapper.ServiceOfferingCacheMapper
import com.example.pivota.dashboard.data.mapper.ServiceOfferingMapper
import com.example.pivota.dashboard.data.remote.ServiceOfferingsApiService
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.GetAllOfferingsParams
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse

class ServiceOfferingsRepositoryImpl @Inject constructor(
    private val apiService: ServiceOfferingsApiService,
    private val offeringDao: ServiceOfferingDao,
    private val networkMapper: ServiceOfferingMapper,
    private val cacheMapper: ServiceOfferingCacheMapper,
    private val networkMonitor: NetworkMonitor
) : ServiceOfferingsRepository {

    companion object {
        private const val TAG = "ServiceOfferingsRepo"

        // Cache only for individual offerings (15 minutes)
        private const val INDIVIDUAL_OFFERING_CACHE_MS = 15 * 60 * 1000L
    }

    // ======================================================
    // LISTINGS / SEARCH RESULTS - NO CACHING (Direct network)
    // ======================================================

    override suspend fun getOfferingsByCategory(
        categoryId: String,
        limit: Int,
        offset: Int,
        city: String?,
        minPrice: Double?,
        maxPrice: Double?,
        forceRefresh: Boolean
    ): ApiResult<ServiceOfferingsResponse> {
        Log.d(TAG, "getOfferingsByCategory: categoryId=$categoryId, forceRefresh=$forceRefresh")

        // Always fetch from network for listings - users expect fresh results
        return safeApiCall {
            apiService.getOfferingsByCategory(
                categoryId = categoryId,
                limit = limit,
                offset = offset,
                city = city,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }.let { result ->
            when (result) {
                is ApiResult.Success -> {
                    val response = result.data
                    if (response.success && response.data != null) {
                        Log.d(TAG, "Successfully fetched ${response.data.size} offerings")
                        ApiResult.Success(networkMapper.toServiceOfferingsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = response.message),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    Log.e(TAG, "Failed to fetch offerings: ${result.technicalMessage}")
                    result
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getAllOfferings(
        params: GetAllOfferingsParams,
        forceRefresh: Boolean
    ): ApiResult<ServiceOfferingsResponse> {
        Log.d(TAG, "getAllOfferings: params=$params, forceRefresh=$forceRefresh")

        // Always fetch from network for listings
        return safeApiCall {
            apiService.getAllOfferings(
                limit = params.limit,
                offset = params.offset,
                city = params.city,
                minPrice = params.minPrice,
                maxPrice = params.maxPrice,
                sortBy = params.sortBy.value,
                minRating = params.minRating,
                verifiedOnly = params.verifiedOnly
            )
        }.let { result ->
            when (result) {
                is ApiResult.Success -> {
                    val response = result.data
                    if (response.success && response.data != null) {
                        Log.d(TAG, "Successfully fetched ${response.data.size} all offerings")
                        ApiResult.Success(networkMapper.toServiceOfferingsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = response.message),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    Log.e(TAG, "Failed to fetch all offerings: ${result.technicalMessage}")
                    result
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    // ======================================================
    // INDIVIDUAL OFFERINGS - CACHE IS GOOD HERE
    // ======================================================

    override suspend fun getServiceOfferingById(
        serviceId: String,
        forceRefresh: Boolean
    ): ApiResult<ServiceOffering> {
        Log.d(TAG, "getServiceOfferingById: serviceId=$serviceId, forceRefresh=$forceRefresh")

        val isNetworkAvailable = networkMonitor.isNetworkAvailable()

        // Check cache first (only if not forcing refresh)
        if (!forceRefresh) {
            val cached = offeringDao.getServiceOfferingById(serviceId)
            if (cached != null) {
                val metadata = offeringDao.getCacheMetadata(cached.categoryId)
                val isCacheFresh = metadata != null &&
                        (System.currentTimeMillis() - metadata.lastUpdated) < INDIVIDUAL_OFFERING_CACHE_MS

                // Return cached if fresh OR offline
                if (isCacheFresh || !isNetworkAvailable) {
                    Log.d(TAG, "Returning cached offering: $serviceId (fresh=$isCacheFresh, offline=${!isNetworkAvailable})")
                    return ApiResult.Success(cacheMapper.toDomain(cached))
                }
            }
        }

        // Fetch from network
        val result = safeApiCall {
            apiService.getServiceOfferingById(serviceId)
        }

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data != null) {
                    val offeringData = response.data
                    val domainOffering = mapToDomain(offeringData)

                    // Cache the result
                    val entity = cacheMapper.toEntityFromDetail(offeringData, offeringData.categoryId)
                    offeringDao.insertOfferings(listOf(entity))

                    // Update cache metadata
                    offeringDao.upsertCacheMetadata(
                        com.example.pivota.core.database.entity.ServiceOfferingsCacheMetadataEntity(
                            categoryId = offeringData.categoryId,
                            lastUpdated = System.currentTimeMillis(),
                            totalCount = 1
                        )
                    )

                    Log.d(TAG, "Successfully fetched and cached offering: $serviceId")
                    ApiResult.Success(domainOffering)
                } else {
                    // Try to return cached version even if network failed
                    val cached = offeringDao.getServiceOfferingById(serviceId)
                    if (cached != null) {
                        Log.d(TAG, "Network returned empty, returning cached version")
                        ApiResult.Success(cacheMapper.toDomain(cached))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = response.message),
                            technicalMessage = response.message
                        )
                    }
                }
            }
            is ApiResult.Error -> {
                // Try to return cached version on error
                val cached = offeringDao.getServiceOfferingById(serviceId)
                if (cached != null) {
                    Log.d(TAG, "Network error, returning cached version")
                    ApiResult.Success(cacheMapper.toDomain(cached))
                } else {
                    result
                }
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    // ======================================================
    // CREATE SERVICE OFFERING
    // ======================================================

    override suspend fun createServiceOffering(request: CreateServiceOfferingRequestDto): ApiResult<ServiceOfferingsResponse> {
        Log.d(TAG, "createServiceOffering: title=${request.title}, categoryId=${request.categoryId}")

        val result = safeApiCall {
            apiService.createServiceOffering(request)
        }

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data != null) {
                    val createdData = response.data
                    val domainOffering = mapToDomain(createdData)

                    // Optionally cache the new offering
                    val entity = cacheMapper.toEntityFromDetail(createdData, createdData.categoryId)
                    offeringDao.insertOfferings(listOf(entity))

                    // Invalidate cache for this category (since new offering affects listings)
                    // But we don't cache listings, so this is optional

                    val serviceOfferingsResponse = ServiceOfferingsResponse(
                        success = response.success,
                        message = response.message,
                        code = response.code.toString(),
                        data = listOf(domainOffering),
                        pagination = null
                    )
                    ApiResult.Success(serviceOfferingsResponse)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown(originalMessage = response.message),
                        technicalMessage = response.message
                    )
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "Failed to create offering: ${result.technicalMessage}")
                result
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    // ======================================================
    // STREAMS - Only for offline cached data
    // ======================================================

    override fun getOfferingsByCategoryStream(
        categoryId: String
    ): Flow<ServiceOfferingsResponse> {
        Log.d(TAG, "getOfferingsByCategoryStream: categoryId=$categoryId")
        // Return cached offerings for offline viewing only
        return flowOf(
            ServiceOfferingsResponse(
                success = true,
                message = "Cached data",
                code = "CACHED",
                data = emptyList(), // You can implement this if needed
                pagination = null
            )
        )
    }

    // ======================================================
    // REFRESH METHODS - Simplified
    // ======================================================

    override suspend fun refreshOfferingsByCategory(categoryId: String, force: Boolean) {
        Log.d(TAG, "refreshOfferingsByCategory: categoryId=$categoryId, force=$force")
        // Since we don't cache listings, nothing to refresh
        // Individual offerings are refreshed on-demand when forceRefresh=true
    }

    override suspend fun refreshAllOfferings(force: Boolean) {
        Log.d(TAG, "refreshAllOfferings: force=$force")
        // Since we don't cache listings, nothing to refresh
    }

    override suspend fun clearOfferingsCache() {
        Log.d(TAG, "clearOfferingsCache: Clearing stale individual offerings")
        val staleTime = System.currentTimeMillis() - INDIVIDUAL_OFFERING_CACHE_MS
        offeringDao.deleteStaleOfferings(staleTime)
    }

    override suspend fun clearAllCache() {
        Log.d(TAG, "clearAllCache: Clearing all cached offerings")
        offeringDao.clearAllOfferings()
        offeringDao.clearAllCacheMetadata()
    }

    override suspend fun getCacheStatus(categoryId: String): CacheStatus {
        // Simplified - only for individual offerings
        val metadata = offeringDao.getCacheMetadata(categoryId)
        if (metadata == null) {
            return CacheStatus.Empty
        }
        val age = System.currentTimeMillis() - metadata.lastUpdated
        return if (age < INDIVIDUAL_OFFERING_CACHE_MS) {
            CacheStatus.Fresh(age)
        } else {
            CacheStatus.Expired(age)
        }
    }

    override suspend fun getAllOfferingsCacheStatus(): CacheStatus {
        // Not applicable since we don't cache all offerings
        return CacheStatus.Empty
    }

    // ======================================================
    // PRIVATE HELPERS
    // ======================================================

    private fun mapToDomain(data: com.example.pivota.dashboard.data.dto.CreatedServiceOfferingDataDto): ServiceOffering {
        return ServiceOffering(
            id = data.id,
            externalId = data.externalId,
            professionalName = data.professionalName,
            professionalAvatar = data.professionalAvatar,
            isVerified = data.isVerified,
            title = data.title,
            description = data.description,
            categoryId = data.categoryId,
            categoryName = data.categoryName,
            basePrice = data.basePrice,
            priceUnit = data.priceUnit,
            currency = data.currency,
            coverageAreas = data.coverageAreas,
            availability = data.availability?.map { dayDto ->
                DayAvailability(
                    day = dayDto.day,
                    open = dayDto.open,
                    close = dayDto.close,
                    isClosed = dayDto.isClosed
                )
            } ?: emptyList(),
            yearsExperience = data.yearsExperience,
            hourlyRate = data.hourlyRate,
            status = data.status,
            averageRating = data.averageRating,
            reviewCount = data.reviewCount,
            createdAt = data.createdAt,
            updatedAt = data.updatedAt,
            isNegotiable = data.isNegotiable ?: true,
            minNegotiablePrice = data.minNegotiablePrice,
            maxNegotiablePrice = data.maxNegotiablePrice,
            useCustomBookingFee = data.useCustomBookingFee ?: false,
            customBookingFeeEnabled = data.customBookingFeeEnabled,
            customBookingFeeAmount = data.customBookingFeeAmount,
            customBookingFeeCurrency = data.customBookingFeeCurrency,
            customBookingFeeDescription = data.customBookingFeeDescription,
            customBookingFeeRefundable = data.customBookingFeeRefundable,
            skilledProfessionalId = data.skilledProfessionalId
        )
    }
}