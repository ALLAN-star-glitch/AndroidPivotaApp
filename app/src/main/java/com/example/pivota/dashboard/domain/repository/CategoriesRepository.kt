package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    /**
     * Get discovery metadata (lightweight categories)
     * @param vertical Optional: HOUSING, JOBS, SOCIAL_SUPPORT
     * @param type Optional: MAIN, COMPLIMENTARY
     */
    suspend fun getDiscoveryMetadata(
        vertical: String? = null,
        type: String? = null
    ): ApiResult<List<DiscoveryCategory>>

    /**
     * Get discovery metadata as Flow (for offline-first real-time updates)
     * @param vertical Optional: HOUSING, JOBS, SOCIAL_SUPPORT
     * @param type Optional: MAIN, COMPLIMENTARY
     */
    fun getDiscoveryMetadataStream(
        vertical: String? = null,
        type: String? = null
    ): Flow<List<DiscoveryCategory>>

    /**
     * Get full categories with filtering
     * @param vertical Optional: HOUSING, JOBS, SOCIAL_SUPPORT
     * @param type Optional: MAIN, COMPLIMENTARY
     * @param parentId Optional: Filter by parent category (use "null" for top-level)
     * @param hasSubcategories Optional: Filter categories that have subcategories
     * @param hasParent Optional: Filter categories that have a parent
     * @param search Optional: Search by name
     * @param includeNested Optional: Include nested subcategories
     */
    suspend fun getCategories(
        vertical: String? = null,
        type: String? = null,
        parentId: String? = null,
        hasSubcategories: Boolean? = null,
        hasParent: Boolean? = null,
        search: String? = null,
        includeNested: Boolean? = null
    ): ApiResult<List<Category>>

    suspend fun refreshDiscoveryMetadata(
        vertical: String? = null,
        type: String? = null
    )
}