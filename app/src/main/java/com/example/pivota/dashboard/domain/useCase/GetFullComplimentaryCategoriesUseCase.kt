package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFullComplimentaryCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {

    /**
     * Get full COMPLIMENTARY categories with parent-child relationships
     * Uses the full categories API (not discovery metadata)
     */
    operator fun invoke(): Flow<List<Category>> = flow {
        println("🟢 [GetFullComplimentaryCategoriesUseCase] Fetching full COMPLIMENTARY categories")

        val result = repository.getCategories(
            type = "COMPLIMENTARY",
            includeNested = true
        )

        when (result) {
            is ApiResult.Success -> {
                val categories = result.data ?: emptyList()
                println("🟢 [GetFullComplimentaryCategoriesUseCase] Received ${categories.size} categories with parent-child relationships")
                categories.forEach { category ->
                    println("   - ${category.name} (ID: ${category.id}, parentId: ${category.parentId})")
                }
                emit(categories)
            }
            is ApiResult.Error -> {
                println("❌ [GetFullComplimentaryCategoriesUseCase] Error: ${result.technicalMessage}")
                emit(emptyList())
            }
            ApiResult.Loading -> {
                // Don't emit anything for loading
            }
        }
    }

    suspend fun refresh(): ApiResult<List<Category>> {
        println("🟢 [GetFullComplimentaryCategoriesUseCase] Refreshing categories")
        return repository.getCategories(
            type = "COMPLIMENTARY",
            includeNested = true
        )
    }
}