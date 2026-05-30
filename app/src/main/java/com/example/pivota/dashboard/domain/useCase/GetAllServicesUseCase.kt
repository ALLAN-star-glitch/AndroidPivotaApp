// GetAllServicesUseCase.kt
package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllServicesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {

    /**
     * Get all services as a Flow for real-time updates
     * This is for observing changes over time
     */
    fun getDiscoveryMetadataStream(): Flow<List<DiscoveryCategory>> {
        println("GetAllServicesUseCase: getDiscoveryMetadataStream() called with type=COMPLIMENTARY")
        return repository.getDiscoveryMetadataStream(type = "COMPLIMENTARY").map { allServices ->
            println("GetAllServicesUseCase: Received ${allServices.size} categories from repository stream")
            allServices.forEach { category ->
                println("GetAllServicesUseCase: Repository stream returned: ${category.name} - hasSubcategories=${category.hasSubcategories}")
            }
            allServices.sortedBy { it.name }
        }
    }

    /**
     * Get all services as a suspend function for one-time fetch with caching strategy
     * This is the primary method for loading data
     */
    suspend operator fun invoke(
        forceRefresh: Boolean = false
    ): ApiResult<List<DiscoveryCategory>> {
        println("GetAllServicesUseCase: invoke() called, forceRefresh=$forceRefresh, type=COMPLIMENTARY")
        return repository.getDiscoveryMetadata(
            type = "COMPLIMENTARY",
            forceRefresh = forceRefresh
        ).let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val sortedServices = apiResult.data.sortedBy { it.name }
                    println("GetAllServicesUseCase: Successfully loaded ${sortedServices.size} categories")
                    ApiResult.Success(sortedServices)
                }
                is ApiResult.Error -> {
                    println("GetAllServicesUseCase: Error loading categories: ${apiResult.technicalMessage}")
                    apiResult
                }
                ApiResult.Loading -> {
                    println("GetAllServicesUseCase: Loading state")
                    ApiResult.Loading
                }
            }
        }
    }

    /**
     * Get cache status for UI warnings
     */
    suspend fun getCacheStatus(): CacheStatus {
        return repository.getDiscoveryCacheStatus(type = "COMPLIMENTARY")
    }

    /**
     * Check if cached data exists
     */
    suspend fun hasCachedData(): Boolean {
        val status = repository.getDiscoveryCacheStatus(type = "COMPLIMENTARY")
        return status !is CacheStatus.Empty
    }

    /**
     * Force refresh the cache
     */
    suspend fun refresh() {
        println("GetAllServicesUseCase: refresh() called")
        repository.refreshDiscoveryMetadata(type = "COMPLIMENTARY")
    }

    /**
     * Clear all cached categories
     */
    suspend fun clearCache() {
        println("GetAllServicesUseCase: clearCache() called")
        repository.clearAllCategoriesCache()
    }
}