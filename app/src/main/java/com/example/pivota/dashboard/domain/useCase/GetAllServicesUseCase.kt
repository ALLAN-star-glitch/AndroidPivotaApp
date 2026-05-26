// GetAllServicesUseCase.kt
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
        println("🟢 [GetAllServicesUseCase] invoke() called with type=COMPLIMENTARY")
        return repository.getDiscoveryMetadataStream(type = "COMPLIMENTARY").map { allServices ->
            println("🟢 [GetAllServicesUseCase] Received ${allServices.size} categories from repository")
            allServices.forEach { category ->
                println("🟢 [GetAllServicesUseCase] Repository returned: ${category.name} - hasSubcategories=${category.hasSubcategories}")
            }
            allServices.sortedBy { it.name }
        }
    }

    suspend fun refresh() {
        println("🟢 [GetAllServicesUseCase] refresh() called")
        repository.refreshDiscoveryMetadata(type = "COMPLIMENTARY")
    }
}