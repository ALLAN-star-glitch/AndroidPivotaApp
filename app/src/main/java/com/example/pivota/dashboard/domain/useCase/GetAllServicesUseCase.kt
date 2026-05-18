package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllServicesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {

    operator fun invoke(): Flow<List<DiscoveryCategory>> {
        return repository.getDiscoveryMetadataStream(type = "COMPLIMENTARY").map { allServices ->
            allServices.sortedBy { it.name }
        }
    }

    suspend fun refresh() {
        repository.refreshDiscoveryMetadata(type = "COMPLIMENTARY")
    }
}