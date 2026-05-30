package com.example.pivota.dashboard.data.repository

import android.util.Log
import com.example.pivota.core.database.dao.CategoryDao
import com.example.pivota.core.database.entity.CategoriesCacheMetadataEntity
import com.example.pivota.core.database.entity.CategoryEntity
import com.example.pivota.core.database.entity.DiscoveryCategoryEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.core.utils.NetworkMonitor
import com.example.pivota.dashboard.data.mapper.CategoriesDtoMapper
import com.example.pivota.dashboard.data.remote.CategoriesApiService
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoriesApiService: CategoriesApiService,
    private val categoryDao: CategoryDao,
    private val mapper: CategoriesDtoMapper,
    private val networkMonitor: NetworkMonitor
) : CategoriesRepository {

    companion object {
        private const val TAG = "CategoriesRepository"

        // Cache expiry strategy for categories (changes rarely)
        private const val CACHE_EXPIRY_FRESH_MS = 24 * 60 * 60 * 1000L      // 24 hours - fresh data
        private const val CACHE_EXPIRY_STALE_MS = 7 * 24 * 60 * 60 * 1000L  // 7 days - stale but acceptable
        // Note: Offline mode uses any cached data regardless of age
    }

    // ==================== DISCOVERY METADATA METHODS ====================

    override suspend fun getDiscoveryMetadata(
        vertical: String?,
        type: String?,
        forceRefresh: Boolean
    ): ApiResult<List<DiscoveryCategory>> {
        val cacheKey = generateDiscoveryCacheKey(vertical, type)
        val metadata = categoryDao.getDiscoveryCacheMetadata(cacheKey)
        val now = System.currentTimeMillis()
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()

        Log.d(TAG, "getDiscoveryMetadata: cacheKey=$cacheKey, forceRefresh=$forceRefresh")

        // Strategy 1: Force refresh requested and network available
        if (forceRefresh && isNetworkAvailable) {
            Log.d(TAG, "Strategy 1: Force refresh - fetching from network")
            return fetchDiscoveryFromNetworkAndCache(vertical, type, cacheKey)
        }

        // Strategy 2: Check if we have valid fresh cache (less than 24 hours old)
        val isCacheFresh = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS
        if (isCacheFresh) {
            Log.d(TAG, "Strategy 2: Using fresh cache")
            val cachedResponse = getCachedDiscoveryCategories(cacheKey)
            if (cachedResponse is ApiResult.Success && cachedResponse.data.isNotEmpty()) {
                return cachedResponse
            }
        }

        // Strategy 3: Cache is stale but network available - return stale + background refresh
        val isCacheStale = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_STALE_MS
        if (isCacheStale && isNetworkAvailable) {
            Log.d(TAG, "Strategy 3: Cache stale, returning stale data with background refresh")
            val staleResponse = getCachedDiscoveryCategories(cacheKey)
            if (staleResponse is ApiResult.Success && staleResponse.data.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Background refresh triggered for discovery metadata: $cacheKey")
                    fetchDiscoveryFromNetworkAndCache(vertical, type, cacheKey)
                }
                return staleResponse
            }
        }

        // Strategy 4: No cache or cache expired, but we have network
        if (isNetworkAvailable) {
            Log.d(TAG, "Strategy 4: No valid cache, fetching from network")
            return fetchDiscoveryFromNetworkAndCache(vertical, type, cacheKey)
        }

        // Strategy 5: No network - return any cached data (no expiry limit for offline)
        val anyCachedData = getCachedDiscoveryCategories(cacheKey)
        if (anyCachedData is ApiResult.Success && anyCachedData.data.isNotEmpty()) {
            Log.d(TAG, "Strategy 5: No network, returning cached data (any age)")
            return anyCachedData
        }

        // Strategy 6: No network and no cache - return error
        Log.e(TAG, "Strategy 6: No network and no cache available")
        return ApiResult.Error(
            networkError = NetworkError.Unknown(
                originalMessage = "No internet connection and no cached categories available"
            ),
            technicalMessage = "Offline - no cached categories"
        )
    }

    override fun getDiscoveryMetadataStream(
        vertical: String?,
        type: String?
    ): Flow<List<DiscoveryCategory>> {
        val cacheKey = generateDiscoveryCacheKey(vertical, type)
        return categoryDao.getDiscoveryCategories(cacheKey).map { entities ->
            mapper.toDiscoveryDomainListFromEntities(entities)
        }.catch { e ->
            Log.e(TAG, "Error in discovery metadata stream", e)
            emit(emptyList())
        }.onStart {
            CoroutineScope(Dispatchers.IO).launch {
                refreshCacheInBackground(vertical, type)
            }
        }
    }

    // ==================== CATEGORIES METHODS ====================

    override suspend fun getCategories(
        vertical: String?,
        type: String?,
        parentId: String?,
        hasSubcategories: Boolean?,
        hasParent: Boolean?,
        search: String?,
        includeNested: Boolean?,
        forceRefresh: Boolean
    ): ApiResult<List<Category>> {
        val cacheKey = generateCategoriesCacheKey(vertical, type, parentId, hasSubcategories, hasParent, search, includeNested)
        val metadata = categoryDao.getCategoriesCacheMetadata(cacheKey)
        val now = System.currentTimeMillis()
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()

        Log.d(TAG, "getCategories: cacheKey=$cacheKey, forceRefresh=$forceRefresh")

        // Strategy 1: Force refresh requested and network available
        if (forceRefresh && isNetworkAvailable) {
            Log.d(TAG, "Strategy 1: Force refresh - fetching categories from network")
            return fetchCategoriesFromNetworkAndCache(
                vertical, type, parentId, hasSubcategories, hasParent, search, includeNested, cacheKey
            )
        }

        // Strategy 2: Check if we have valid fresh cache (less than 24 hours old)
        val isCacheFresh = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS
        if (isCacheFresh) {
            Log.d(TAG, "Strategy 2: Using fresh cache for categories")
            val cachedResponse = getCachedCategories(cacheKey)
            if (cachedResponse is ApiResult.Success && cachedResponse.data.isNotEmpty()) {
                return cachedResponse
            }
        }

        // Strategy 3: Cache is stale but network available - return stale + background refresh
        val isCacheStale = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_STALE_MS
        if (isCacheStale && isNetworkAvailable) {
            Log.d(TAG, "Strategy 3: Cache stale, returning stale categories with background refresh")
            val staleResponse = getCachedCategories(cacheKey)
            if (staleResponse is ApiResult.Success && staleResponse.data.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Background refresh triggered for categories: $cacheKey")
                    fetchCategoriesFromNetworkAndCache(
                        vertical, type, parentId, hasSubcategories, hasParent, search, includeNested, cacheKey
                    )
                }
                return staleResponse
            }
        }

        // Strategy 4: No cache or cache expired, but we have network
        if (isNetworkAvailable) {
            Log.d(TAG, "Strategy 4: No valid cache, fetching categories from network")
            return fetchCategoriesFromNetworkAndCache(
                vertical, type, parentId, hasSubcategories, hasParent, search, includeNested, cacheKey
            )
        }

        // Strategy 5: No network - return any cached data (no expiry limit for offline)
        val anyCachedData = getCachedCategories(cacheKey)
        if (anyCachedData is ApiResult.Success && anyCachedData.data.isNotEmpty()) {
            Log.d(TAG, "Strategy 5: No network, returning cached categories (any age)")
            return anyCachedData
        }

        // Strategy 6: No network and no cache - return error
        Log.e(TAG, "Strategy 6: No network and no cache available for categories")
        return ApiResult.Error(
            networkError = NetworkError.Unknown(
                originalMessage = "No internet connection and no cached categories available"
            ),
            technicalMessage = "Offline - no cached categories"
        )
    }

    // ==================== REFRESH METHODS ====================

    override suspend fun refreshDiscoveryMetadata(vertical: String?, type: String?) {
        val cacheKey = generateDiscoveryCacheKey(vertical, type)
        if (networkMonitor.isNetworkAvailable()) {
            try {
                Log.d(TAG, "Refreshing discovery metadata: $cacheKey")
                fetchDiscoveryFromNetworkAndCache(vertical, type, cacheKey)
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing discovery metadata", e)
            }
        }
    }

    override suspend fun refreshCategories(vertical: String?, type: String?, parentId: String?) {
        val cacheKey = generateCategoriesCacheKey(vertical, type, parentId, null, null, null, null)
        if (networkMonitor.isNetworkAvailable()) {
            try {
                Log.d(TAG, "Refreshing categories: $cacheKey")
                fetchCategoriesFromNetworkAndCache(
                    vertical, type, parentId, null, null, null, null, cacheKey
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing categories", e)
            }
        }
    }

    // ==================== CACHE MANAGEMENT ====================

    override suspend fun clearAllCategoriesCache() {
        Log.d(TAG, "Clearing all categories cache")
        categoryDao.clearAllDiscoveryCategories()
        categoryDao.clearAllCategories()
        categoryDao.clearAllCacheMetadata()
    }

    override suspend fun getDiscoveryCacheStatus(vertical: String?, type: String?): CacheStatus {
        val cacheKey = generateDiscoveryCacheKey(vertical, type)
        val metadata = categoryDao.getDiscoveryCacheMetadata(cacheKey)
        return calculateCacheStatus(metadata)
    }

    override suspend fun getCategoriesCacheStatus(vertical: String?, type: String?, parentId: String?): CacheStatus {
        val cacheKey = generateCategoriesCacheKey(vertical, type, parentId, null, null, null, null)
        val metadata = categoryDao.getCategoriesCacheMetadata(cacheKey)
        return calculateCacheStatus(metadata)
    }

    // ==================== PRIVATE METHODS ====================

    private fun calculateCacheStatus(metadata: CategoriesCacheMetadataEntity?): CacheStatus {
        if (metadata == null) return CacheStatus.Empty
        val age = System.currentTimeMillis() - metadata.lastUpdated
        return when {
            age < CACHE_EXPIRY_FRESH_MS -> CacheStatus.Fresh(age)
            age < CACHE_EXPIRY_STALE_MS -> CacheStatus.Stale(age)
            else -> CacheStatus.Expired(age)
        }
    }

    private suspend fun refreshCacheInBackground(vertical: String?, type: String?) {
        try {
            val networkResult = safeApiCall {
                categoriesApiService.getDiscoveryMetadata(vertical, type)
            }
            if (networkResult is ApiResult.Success) {
                val response = networkResult.data
                if (response.success && response.data != null) {
                    val cacheKey = generateDiscoveryCacheKey(vertical, type)
                    val entities = mapper.toDiscoveryEntityList(response.data, cacheKey)
                    categoryDao.insertDiscoveryCategories(entities)
                    categoryDao.upsertDiscoveryCacheMetadata(
                        CategoriesCacheMetadataEntity(
                            cacheKey = cacheKey,
                            lastUpdated = System.currentTimeMillis(),
                            totalCount = entities.size
                        )
                    )
                    Log.d(TAG, "Background refresh completed for discovery metadata")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Background refresh failed for discovery metadata", e)
        }
    }

    private suspend fun fetchDiscoveryFromNetworkAndCache(
        vertical: String?,
        type: String?,
        cacheKey: String
    ): ApiResult<List<DiscoveryCategory>> {
        val networkResult = safeApiCall {
            categoriesApiService.getDiscoveryMetadata(vertical, type)
        }

        return when (networkResult) {
            is ApiResult.Success -> {
                val response = networkResult.data
                if (response.success && response.data != null) {
                    val entities = mapper.toDiscoveryEntityList(response.data, cacheKey)
                    categoryDao.insertDiscoveryCategories(entities)
                    categoryDao.upsertDiscoveryCacheMetadata(
                        CategoriesCacheMetadataEntity(
                            cacheKey = cacheKey,
                            lastUpdated = System.currentTimeMillis(),
                            totalCount = entities.size
                        )
                    )
                    ApiResult.Success(mapper.toDiscoveryDomainList(response.data))
                } else {
                    getCachedDiscoveryCategories(cacheKey)
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "Network error, falling back to cache: ${networkResult.technicalMessage}")
                getCachedDiscoveryCategories(cacheKey)
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    private suspend fun fetchCategoriesFromNetworkAndCache(
        vertical: String?,
        type: String?,
        parentId: String?,
        hasSubcategories: Boolean?,
        hasParent: Boolean?,
        search: String?,
        includeNested: Boolean?,
        cacheKey: String
    ): ApiResult<List<Category>> {
        val networkResult = safeApiCall {
            categoriesApiService.getCategories(
                vertical, type, parentId, hasSubcategories, hasParent, search, includeNested
            )
        }

        return when (networkResult) {
            is ApiResult.Success -> {
                val response = networkResult.data
                if (response.success && response.data != null) {
                    val entities = mapper.toCategoryEntityList(response.data, cacheKey)
                    categoryDao.insertCategories(entities)
                    categoryDao.upsertCategoriesCacheMetadata(
                        CategoriesCacheMetadataEntity(
                            cacheKey = cacheKey,
                            lastUpdated = System.currentTimeMillis(),
                            totalCount = entities.size
                        )
                    )
                    ApiResult.Success(mapper.toCategoryDomainList(response.data))
                } else {
                    getCachedCategories(cacheKey)
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "Network error, falling back to cache: ${networkResult.technicalMessage}")
                getCachedCategories(cacheKey)
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    private suspend fun getCachedDiscoveryCategories(cacheKey: String): ApiResult<List<DiscoveryCategory>> {
        return try {
            val cached = categoryDao.getDiscoveryCategoriesList(cacheKey)
            if (cached.isNotEmpty()) {
                Log.d(TAG, "Found ${cached.size} cached discovery categories for key: $cacheKey")
                ApiResult.Success(mapper.toDiscoveryDomainListFromEntities(cached))
            } else {
                ApiResult.Error(
                    networkError = NetworkError.Unknown(originalMessage = "No cached data available"),
                    technicalMessage = "No cached data for key: $cacheKey"
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                networkError = NetworkError.Unknown(originalMessage = e.message ?: "Failed to load cached data"),
                technicalMessage = e.message
            )
        }
    }

    private suspend fun getCachedCategories(cacheKey: String): ApiResult<List<Category>> {
        return try {
            val cached = categoryDao.getCategoriesListByCacheKey(cacheKey)
            if (cached.isNotEmpty()) {
                Log.d(TAG, "Found ${cached.size} cached categories for key: $cacheKey")
                ApiResult.Success(mapper.toCategoryDomainListFromEntities(cached))
            } else {
                ApiResult.Error(
                    networkError = NetworkError.Unknown(originalMessage = "No cached data available"),
                    technicalMessage = "No cached data for key: $cacheKey"
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                networkError = NetworkError.Unknown(originalMessage = e.message ?: "Failed to load cached data"),
                technicalMessage = e.message
            )
        }
    }

    private fun generateDiscoveryCacheKey(vertical: String?, type: String?): String {
        return "discovery_${vertical ?: "all"}_${type ?: "all"}"
    }

    private fun generateCategoriesCacheKey(
        vertical: String?,
        type: String?,
        parentId: String?,
        hasSubcategories: Boolean?,
        hasParent: Boolean?,
        search: String?,
        includeNested: Boolean?
    ): String {
        return buildString {
            append("categories")
            append("_v_${vertical ?: "all"}")
            append("_t_${type ?: "all"}")
            append("_p_${parentId ?: "null"}")
            append("_hs_${hasSubcategories ?: "any"}")
            append("_hp_${hasParent ?: "any"}")
            append("_s_${search ?: "none"}")
            append("_in_${includeNested ?: "false"}")
        }
    }
}