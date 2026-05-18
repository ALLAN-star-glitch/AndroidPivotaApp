package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCommonServicesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {

    operator fun invoke(): Flow<List<DiscoveryCategory>> {
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
     * Force refresh by clearing cache and fetching fresh data
     */
    suspend fun refresh() {
        repository.refreshDiscoveryMetadata(type = "COMPLIMENTARY")
    }
}