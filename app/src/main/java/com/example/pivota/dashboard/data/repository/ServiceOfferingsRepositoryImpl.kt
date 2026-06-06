package com.example.pivota.dashboard.data.repository

import android.util.Log
import com.example.pivota.core.database.dao.ServiceOfferingDao
import com.example.pivota.core.database.entity.ServiceOfferingsCacheMetadataEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.core.utils.NetworkMonitor
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.data.mapper.ServiceOfferingCacheMapper
import com.example.pivota.dashboard.data.mapper.ServiceOfferingMapper
import com.example.pivota.dashboard.data.remote.ServiceOfferingsApiService
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.repository.ServiceOfferingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

        // Cache expiry strategy
        private const val CACHE_EXPIRY_FRESH_MS = 5 * 60 * 1000L      // 5 minutes - fresh data
        private const val CACHE_EXPIRY_STALE_MS = 30 * 60 * 1000L     // 30 minutes - stale but acceptable
        private const val CACHE_EXPIRY_OFFLINE_MS = 24 * 60 * 60 * 1000L // 24 hours - offline fallback
    }

    override suspend fun getOfferingsByCategory(
        categoryId: String,
        limit: Int,
        offset: Int,
        city: String?,
        minPrice: Double?,
        maxPrice: Double?,
        forceRefresh: Boolean
    ): ApiResult<ServiceOfferingsResponse> {
        val metadata = offeringDao.getCacheMetadata(categoryId)
        val now = System.currentTimeMillis()
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()

        Log.d(TAG, "getOfferingsByCategory: categoryId=$categoryId, forceRefresh=$forceRefresh, isNetworkAvailable=$isNetworkAvailable")

        // STRATEGY 1: Force refresh requested and network available
        if (forceRefresh && isNetworkAvailable) {
            Log.d(TAG, "Strategy 1: Force refresh - fetching from network")
            return fetchFromNetworkAndCache(categoryId, limit, offset, city, minPrice, maxPrice)
        }

        // STRATEGY 2: Check if we have valid fresh cache
        val isCacheFresh = metadata != null &&
                (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS

        if (isCacheFresh) {
            Log.d(TAG, "Strategy 2: Using fresh cache")
            val cachedResponse = getCachedOfferings(categoryId)
            if (cachedResponse is ApiResult.Success && cachedResponse.data.data.isNotEmpty()) {
                return cachedResponse
            }
        }

        // STRATEGY 3: Cache is stale but network available - return stale + background refresh
        val isCacheStale = metadata != null &&
                (now - metadata.lastUpdated) < CACHE_EXPIRY_STALE_MS

        if (isCacheStale && isNetworkAvailable) {
            Log.d(TAG, "Strategy 3: Cache stale, returning stale data with background refresh")
            val staleResponse = getCachedOfferings(categoryId)
            if (staleResponse is ApiResult.Success && staleResponse.data.data.isNotEmpty()) {
                // Trigger background refresh
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Background refresh triggered for category: $categoryId")
                    fetchFromNetworkAndCache(categoryId, limit, offset, city, minPrice, maxPrice)
                }
                return staleResponse
            }
        }

        // STRATEGY 4: No cache or cache expired, but we have network
        if (isNetworkAvailable) {
            Log.d(TAG, "Strategy 4: No valid cache, fetching from network")
            return fetchFromNetworkAndCache(categoryId, limit, offset, city, minPrice, maxPrice)
        }

        // STRATEGY 5: No network - return any cached data (even if very stale)
        val anyCachedData = getCachedOfferings(categoryId)
        if (anyCachedData is ApiResult.Success && anyCachedData.data.data.isNotEmpty()) {
            Log.d(TAG, "Strategy 5: No network, returning stale cache with offline warning")
            return anyCachedData
        }

        // STRATEGY 6: No network and no cache - return error
        Log.e(TAG, "Strategy 6: No network and no cache available")
        return ApiResult.Error(
            networkError = NetworkError.Unknown(
                originalMessage = "No internet connection and no cached data available"
            ),
            technicalMessage = "Offline - no cached data for category: $categoryId"
        )
    }

    override fun getOfferingsByCategoryStream(
        categoryId: String
    ): Flow<ServiceOfferingsResponse> {
        Log.d(TAG, "getOfferingsByCategoryStream: categoryId=$categoryId")
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

    override suspend fun refreshOfferingsByCategory(categoryId: String, force: Boolean) {
        Log.d(TAG, "refreshOfferingsByCategory: categoryId=$categoryId, force=$force")

        val metadata = offeringDao.getCacheMetadata(categoryId)
        val now = System.currentTimeMillis()
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()

        // Only refresh if network available and (force refresh OR cache is stale)
        val shouldRefresh = force ||
                metadata == null ||
                (now - metadata.lastUpdated) > CACHE_EXPIRY_FRESH_MS

        if (shouldRefresh && isNetworkAvailable) {
            try {
                Log.d(TAG, "Refreshing offerings for category: $categoryId")
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
                        Log.d(TAG, "Successfully refreshed offerings for category: $categoryId, count: ${response.data.size}")
                    }
                } else {
                    Log.w(TAG, "Failed to refresh offerings for category: $categoryId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing offerings for category: $categoryId", e)
                // Silent fail - cache remains valid
            }
        } else {
            Log.d(TAG, "Skipping refresh for category: $categoryId (shouldRefresh=$shouldRefresh, isNetworkAvailable=$isNetworkAvailable)")
        }
    }

    override suspend fun clearOfferingsCache() {
        Log.d(TAG, "clearOfferingsCache: Clearing stale offerings")
        val staleTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours
        offeringDao.deleteStaleOfferings(staleTime)
    }

    override suspend fun clearAllCache() {
        Log.d(TAG, "clearAllCache: Clearing all offerings cache")
        offeringDao.clearAllOfferings()
        offeringDao.clearAllCacheMetadata()
    }

    override suspend fun getCacheStatus(categoryId: String): CacheStatus {
        val metadata = offeringDao.getCacheMetadata(categoryId)
        if (metadata == null) {
            Log.d(TAG, "getCacheStatus: No cache for category $categoryId")
            return CacheStatus.Empty
        }

        val now = System.currentTimeMillis()
        val age = now - metadata.lastUpdated

        val status = when {
            age < CACHE_EXPIRY_FRESH_MS -> CacheStatus.Fresh(age)
            age < CACHE_EXPIRY_STALE_MS -> CacheStatus.Stale(age)
            else -> CacheStatus.Expired(age)
        }

        Log.d(TAG, "getCacheStatus: category=$categoryId, status=$status, age=${age}ms")
        return status
    }

    private suspend fun fetchFromNetworkAndCache(
        categoryId: String,
        limit: Int,
        offset: Int,
        city: String?,
        minPrice: Double?,
        maxPrice: Double?
    ): ApiResult<ServiceOfferingsResponse> {
        Log.d(TAG, "fetchFromNetworkAndCache: categoryId=$categoryId, limit=$limit, offset=$offset")

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
                    Log.d(TAG, "Network fetch successful, caching ${response.data.size} offerings")

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
                    Log.w(TAG, "Network response successful but no data, falling back to cache")
                    getCachedOfferings(categoryId)
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "Network error: ${networkResult.technicalMessage}, falling back to cache")
                getCachedOfferings(categoryId)
            }
            ApiResult.Loading -> {
                Log.d(TAG, "Network loading state")
                ApiResult.Loading
            }
        }
    }

    private suspend fun getCachedOfferings(
        categoryId: String
    ): ApiResult<ServiceOfferingsResponse> {
        return try {
            val cached = offeringDao.getOfferingsByCategoryList(categoryId)
            if (cached.isNotEmpty()) {
                Log.d(TAG, "Found ${cached.size} cached offerings for category: $categoryId")
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
                Log.w(TAG, "No cached offerings found for category: $categoryId")
                ApiResult.Error(
                    networkError = NetworkError.Unknown(
                        originalMessage = "No cached data available"
                    ),
                    technicalMessage = "No cached data available for category: $categoryId"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cached offerings for category: $categoryId", e)
            ApiResult.Error(
                networkError = NetworkError.Unknown(
                    originalMessage = e.message ?: "Failed to load cached data"
                ),
                technicalMessage = e.message
            )
        }
    }

    override suspend fun createServiceOffering(request: CreateServiceOfferingRequestDto): ApiResult<ServiceOfferingsResponse> {
        println("🔍 ========== CREATE SERVICE OFFERING ==========")
        println("🔍 Title: ${request.title}")
        println("🔍 CategoryId: ${request.categoryId}")
        println("🔍 BasePrice: ${request.basePrice}")
        println("🔍 CoverageAreas: ${request.coverageAreas}")  // ✅ Updated
        println("🔍 =============================================")

        val result = safeApiCall {
            apiService.createServiceOffering(request)
        }

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                println("🔍 CREATE SERVICE OFFERING RESPONSE: success=${response.success}, message=${response.message}")

                if (response.success && response.data != null) {
                    val createdData = response.data
                    val domainOffering = ServiceOffering(
                        id = createdData.id,
                        externalId = createdData.externalId,
                        professionalName = createdData.professionalName,
                        professionalAvatar = createdData.professionalAvatar,
                        isVerified = createdData.isVerified,
                        title = createdData.title,
                        description = createdData.description,
                        categoryId = createdData.categoryId,
                        categoryName = createdData.categoryName,
                        basePrice = createdData.basePrice,
                        priceUnit = createdData.priceUnit,
                        currency = createdData.currency,
                        // ❌ REMOVED locationCity and locationNeighborhood
                        // ✅ ADDED coverageAreas (replaces serviceAreas)
                        coverageAreas = createdData.coverageAreas,
                        availability = createdData.availability?.map { dayDto ->
                            DayAvailability(
                                day = dayDto.day,
                                open = dayDto.open,
                                close = dayDto.close,
                                isClosed = dayDto.isClosed
                            )
                        } ?: emptyList(),
                        yearsExperience = createdData.yearsExperience,
                        hourlyRate = createdData.hourlyRate,
                        status = createdData.status,
                        averageRating = createdData.averageRating,
                        reviewCount = createdData.reviewCount,
                        createdAt = createdData.createdAt,
                        updatedAt = createdData.updatedAt
                    )

                    // Invalidate cache for this category after creation
                    CoroutineScope(Dispatchers.IO).launch {
                        offeringDao.deleteCacheMetadata(createdData.categoryId)
                        Log.d(TAG, "Cache invalidated for category: ${createdData.categoryId} after creation")
                    }

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
                        networkError = NetworkError.Unknown(
                            originalMessage = response.message
                        ),
                        technicalMessage = response.message
                    )
                }
            }
            is ApiResult.Error -> {
                println("❌ CREATE SERVICE OFFERING ERROR: ${result.technicalMessage}")
                ApiResult.Error(
                    networkError = result.networkError,
                    technicalMessage = result.technicalMessage
                )
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    override suspend fun getServiceOfferingById(
        serviceId: String,
        forceRefresh: Boolean
    ): ApiResult<ServiceOffering> {
        println("🔍 ========== GET SERVICE OFFERING BY ID ==========")
        println("🔍 Service ID: $serviceId")
        println("🔍 Force Refresh: $forceRefresh")
        println("🔍 ================================================")

        val isNetworkAvailable = networkMonitor.isNetworkAvailable()

        // Check cache first (unless force refresh requested)
        val cachedOffering = if (!forceRefresh) {
            offeringDao.getServiceOfferingById(serviceId)
        } else {
            null
        }

        if (cachedOffering != null && !forceRefresh) {
            println("📦 Found cached service offering: $serviceId")
            val domainOffering = cacheMapper.toDomain(cachedOffering)

            // If cache is fresh enough, return it
            val metadata = offeringDao.getCacheMetadata(cachedOffering.categoryId)
            val now = System.currentTimeMillis()
            val isCacheFresh = metadata != null &&
                    (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS

            if (isCacheFresh || !isNetworkAvailable) {
                println("✅ Returning cached offering (fresh=$isCacheFresh, forceRefresh=$forceRefresh)")
                return ApiResult.Success(domainOffering)
            }
        }

        // If force refresh requested but no network, return cache if available
        if (forceRefresh && !isNetworkAvailable && cachedOffering != null) {
            println("📦 Force refresh requested but offline, returning cached version")
            return ApiResult.Success(cacheMapper.toDomain(cachedOffering))
        }

        // Fetch from network
        val result = safeApiCall {
            apiService.getServiceOfferingById(serviceId)
        }

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                println("🔍 GET SERVICE OFFERING BY ID RESPONSE: success=${response.success}, message=${response.message}")

                if (response.success && response.data != null) {
                    val offeringData = response.data
                    val domainOffering = ServiceOffering(
                        id = offeringData.id,
                        externalId = offeringData.externalId,
                        professionalName = offeringData.professionalName,
                        professionalAvatar = offeringData.professionalAvatar,
                        isVerified = offeringData.isVerified,
                        title = offeringData.title,
                        description = offeringData.description,
                        categoryId = offeringData.categoryId,
                        categoryName = offeringData.categoryName,
                        basePrice = offeringData.basePrice,
                        priceUnit = offeringData.priceUnit,
                        currency = offeringData.currency,
                        // ❌ REMOVED locationCity and locationNeighborhood
                        // ✅ ADDED coverageAreas (replaces serviceAreas)
                        coverageAreas = offeringData.coverageAreas,
                        availability = offeringData.availability?.map { dayDto ->
                            DayAvailability(
                                day = dayDto.day,
                                open = dayDto.open,
                                close = dayDto.close,
                                isClosed = dayDto.isClosed
                            )
                        } ?: emptyList(),
                        yearsExperience = offeringData.yearsExperience,
                        hourlyRate = offeringData.hourlyRate,
                        status = offeringData.status,
                        averageRating = offeringData.averageRating,
                        reviewCount = offeringData.reviewCount,
                        createdAt = offeringData.createdAt,
                        updatedAt = offeringData.updatedAt
                    )

                    // Cache the result
                    val entity = cacheMapper.toEntityFromDetail(offeringData, offeringData.categoryId)
                    offeringDao.insertOfferings(listOf(entity))

                    println("✅ Cached offering from network: $serviceId")
                    ApiResult.Success(domainOffering)
                } else {
                    // Return cached version if available
                    cachedOffering?.let {
                        println("📦 Returning cached version due to empty network response")
                        ApiResult.Success(cacheMapper.toDomain(it))
                    } ?: ApiResult.Error(
                        networkError = NetworkError.Unknown(
                            originalMessage = response.message
                        ),
                        technicalMessage = response.message
                    )
                }
            }
            is ApiResult.Error -> {
                println("❌ GET SERVICE OFFERING BY ID ERROR: ${result.technicalMessage}")
                // Return cached version if available
                cachedOffering?.let {
                    println("📦 Returning cached version due to network error")
                    ApiResult.Success(cacheMapper.toDomain(it))
                } ?: ApiResult.Error(
                    networkError = result.networkError,
                    technicalMessage = result.technicalMessage
                )
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}