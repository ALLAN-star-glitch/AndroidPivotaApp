package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CacheStatus
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCommonServicesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {

    /**
     * Get common services as a Flow for real-time updates
     * This is for continuous observation of cache changes
     */
    fun getDiscoveryMetadataStream(): Flow<List<DiscoveryCategory>> {
        println("GetCommonServicesUseCase: getDiscoveryMetadataStream() called")
        return repository.getDiscoveryMetadataStream(type = "COMPLIMENTARY").map { allServices ->
            if (allServices.isEmpty()) {
                return@map emptyList()
            }

            // Group by vertical
            val housingServices = allServices.filter { it.vertical == "HOUSING" }
            val jobsServices = allServices.filter { it.vertical == "JOBS" }
            val socialSupportServices = allServices.filter { it.vertical == "SOCIAL_SUPPORT" }

            // Sort alphabetically
            val sortedHousing = housingServices.sortedBy { it.name }
            val sortedJobs = jobsServices.sortedBy { it.name }
            val sortedSocial = socialSupportServices.sortedBy { it.name }

            // Combine all services - UI will decide how many to show
            val allSorted = mutableListOf<DiscoveryCategory>()
            allSorted.addAll(sortedHousing)
            allSorted.addAll(sortedJobs)
            allSorted.addAll(sortedSocial)

            allSorted
        }
    }

    /**
     * Get common services as a suspend function for one-time fetch with caching strategy
     * This is the primary method for loading data
     */
    suspend operator fun invoke(
        forceRefresh: Boolean = false
    ): ApiResult<List<DiscoveryCategory>> {
        println("GetCommonServicesUseCase: invoke() called, forceRefresh=$forceRefresh, type=COMPLIMENTARY")

        return repository.getDiscoveryMetadata(
            type = "COMPLIMENTARY",
            forceRefresh = forceRefresh
        ).let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val allServices = apiResult.data
                    if (allServices.isEmpty()) {
                        println("GetCommonServicesUseCase: Empty result from repository")
                        ApiResult.Success(emptyList())
                    } else {
                        // Group and sort by vertical
                        val housingServices = allServices.filter { it.vertical == "HOUSING" }.sortedBy { it.name }
                        val jobsServices = allServices.filter { it.vertical == "JOBS" }.sortedBy { it.name }
                        val socialSupportServices = allServices.filter { it.vertical == "SOCIAL_SUPPORT" }.sortedBy { it.name }

                        val allSorted = mutableListOf<DiscoveryCategory>()
                        allSorted.addAll(housingServices)
                        allSorted.addAll(jobsServices)
                        allSorted.addAll(socialSupportServices)

                        println("GetCommonServicesUseCase: Successfully loaded ${allSorted.size} categories")
                        ApiResult.Success(allSorted)
                    }
                }
                is ApiResult.Error -> {
                    println("GetCommonServicesUseCase: Error loading categories: ${apiResult.technicalMessage}")
                    apiResult
                }
                ApiResult.Loading -> {
                    println("GetCommonServicesUseCase: Loading state")
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
        println("GetCommonServicesUseCase: refresh() called")
        repository.refreshDiscoveryMetadata(type = "COMPLIMENTARY")
    }

    /**
     * Clear all cached categories
     */
    suspend fun clearCache() {
        println("GetCommonServicesUseCase: clearCache() called")
        repository.clearAllCategoriesCache()
    }
}