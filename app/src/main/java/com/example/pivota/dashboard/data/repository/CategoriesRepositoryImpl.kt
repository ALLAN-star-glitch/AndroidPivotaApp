package com.example.pivota.dashboard.data.repository

import com.example.pivota.core.database.dao.CategoryDao
import com.example.pivota.core.database.entity.DiscoveryCategoryEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.dashboard.data.mapper.CategoriesDtoMapper
import com.example.pivota.dashboard.data.remote.CategoriesApiService
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
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
    private val mapper: CategoriesDtoMapper
) : CategoriesRepository {

    companion object {
        private const val CACHE_VALID_DURATION_MS = 5 * 60 * 1000 // 5 minutes
    }

    override suspend fun getDiscoveryMetadata(
        vertical: String?,
        type: String?
    ): ApiResult<List<DiscoveryCategory>> {
        // Try network first
        val networkResult = safeApiCall {
            categoriesApiService.getDiscoveryMetadata(vertical, type)
        }

        return when (networkResult) {
            is ApiResult.Success -> {
                val response = networkResult.data
                if (response.success && response.data != null) {
                    // Cache to Room
                    val entities = response.data.map { dto ->
                        DiscoveryCategoryEntity(
                            id = dto.id,
                            name = dto.name,
                            slug = dto.slug,
                            vertical = dto.vertical,
                            type = dto.type,
                            hasSubcategories = dto.hasSubcategories,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                    categoryDao.insertDiscoveryCategories(entities)

                    val domainCategories = mapper.toDiscoveryDomainList(response.data)
                    ApiResult.Success(domainCategories)
                } else {
                    // Return cached data if network succeeded but data is null
                    getCachedComplimentaryCategories()
                }
            }
            is ApiResult.Error -> {
                // Return cached data on network error
                getCachedComplimentaryCategories()
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    override fun getDiscoveryMetadataStream(
        vertical: String?,
        type: String?
    ): Flow<List<DiscoveryCategory>> {
        // Return cached data as Flow
        return categoryDao.getComplimentaryCategories().map { entities ->
            entities.map { entity ->
                DiscoveryCategory(
                    id = entity.id,
                    name = entity.name,
                    slug = entity.slug,
                    vertical = entity.vertical,
                    type = entity.type,
                    hasSubcategories = entity.hasSubcategories
                )
            }
        }.catch { e ->
            // On error, emit empty list
            emit(emptyList())
        }.onStart {
            // Refresh in background when collection starts
            CoroutineScope(Dispatchers.IO).launch {
                refreshCacheInBackground(vertical, type)
            }
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
                    val entities = response.data.map { dto ->
                        DiscoveryCategoryEntity(
                            id = dto.id,
                            name = dto.name,
                            slug = dto.slug,
                            vertical = dto.vertical,
                            type = dto.type,
                            hasSubcategories = dto.hasSubcategories,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                    categoryDao.insertDiscoveryCategories(entities)
                }
            }
        } catch (e: Exception) {
            // Silent fail - cache remains valid
        }
    }

    private suspend fun getCachedComplimentaryCategories(): ApiResult<List<DiscoveryCategory>> {
        return try {
            val cached = categoryDao.getComplimentaryCategoriesList()
            if (cached.isNotEmpty()) {
                val domainCategories = cached.map { entity ->
                    DiscoveryCategory(
                        id = entity.id,
                        name = entity.name,
                        slug = entity.slug,
                        vertical = entity.vertical,
                        type = entity.type,
                        hasSubcategories = entity.hasSubcategories
                    )
                }
                ApiResult.Success(domainCategories)
            } else {
                ApiResult.Error(
                    networkError = NetworkError.Unknown,
                    technicalMessage = "No cached data available"
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                networkError = NetworkError.Unknown,
                technicalMessage = e.message ?: "Failed to load cached data"
            )
        }
    }

    override suspend fun refreshDiscoveryMetadata(
        vertical: String?,
        type: String?
    ) {
        refreshCacheInBackground(vertical, type)
    }

    override suspend fun getCategories(
        vertical: String?,
        type: String?,
        parentId: String?,
        hasSubcategories: Boolean?,
        hasParent: Boolean?,
        search: String?,
        includeNested: Boolean?
    ): ApiResult<List<Category>> {
        return safeApiCall {
            categoriesApiService.getCategories(
                vertical = vertical,
                type = type,
                parentId = parentId,
                hasSubcategories = hasSubcategories,
                hasParent = hasParent,
                search = search,
                includeNested = includeNested
            )
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success && response.data != null) {
                        val domainCategories = mapper.toCategoryDomainList(response.data)
                        ApiResult.Success(domainCategories)
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown,
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }
}