package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetComplimentaryCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {

    operator fun invoke(): Flow<List<DiscoveryCategory>> {
        println("🟢 [GetComplimentaryCategoriesUseCase] Fetching COMPLIMENTARY categories")
        return repository.getDiscoveryMetadataStream(type = "COMPLIMENTARY").map { categories ->
            println("🟢 [GetComplimentaryCategoriesUseCase] Received ${categories.size} COMPLIMENTARY categories")
            categories.sortedBy { it.name }
        }
    }

    suspend fun refresh() {
        println("🟢 [GetComplimentaryCategoriesUseCase] Refreshing COMPLIMENTARY categories")
        repository.refreshDiscoveryMetadata(type = "COMPLIMENTARY")
    }
}