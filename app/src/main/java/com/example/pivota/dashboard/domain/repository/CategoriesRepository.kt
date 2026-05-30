package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    suspend fun getDiscoveryMetadata(
        vertical: String? = null,
        type: String? = null,
        forceRefresh: Boolean = false
    ): ApiResult<List<DiscoveryCategory>>

    fun getDiscoveryMetadataStream(
        vertical: String? = null,
        type: String? = null
    ): Flow<List<DiscoveryCategory>>

    suspend fun getCategories(
        vertical: String? = null,
        type: String? = null,
        parentId: String? = null,
        hasSubcategories: Boolean? = null,
        hasParent: Boolean? = null,
        search: String? = null,
        includeNested: Boolean? = null,
        forceRefresh: Boolean = false
    ): ApiResult<List<Category>>

    suspend fun refreshDiscoveryMetadata(
        vertical: String? = null,
        type: String? = null
    )

    suspend fun refreshCategories(
        vertical: String? = null,
        type: String? = null,
        parentId: String? = null
    )

    suspend fun clearAllCategoriesCache()

    suspend fun getDiscoveryCacheStatus(
        vertical: String? = null,
        type: String? = null
    ): CacheStatus

    suspend fun getCategoriesCacheStatus(
        vertical: String? = null,
        type: String? = null,
        parentId: String? = null
    ): CacheStatus
}
