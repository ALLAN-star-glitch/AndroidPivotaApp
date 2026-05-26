package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.repository.CategoriesRepository
import com.example.pivota.core.network.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSubcategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {

    /**
     * Get subcategories for a specific parent category
     * @param parentCategoryId The ID of the parent category
     * @return Flow of List<Category> - the subcategories
     */
    operator fun invoke(parentCategoryId: String): Flow<List<Category>> = flow {
        val result = repository.getCategories(
            parentId = parentCategoryId,      // Filter by parent ID
            hasParent = true,                 // Only get categories that have a parent
            type = "COMPLIMENTARY"            // Only COMPLIMENTARY type subcategories
        )

        when (result) {
            is ApiResult.Success -> {
                val subcategories = result.data ?: emptyList()
                println("✅ Found ${subcategories.size} subcategories for parent: $parentCategoryId")
                emit(subcategories)
            }
            is ApiResult.Error -> {
                println("❌ Failed to load subcategories: ${result.technicalMessage}")
                emit(emptyList())  // Emit empty list on error
            }
            ApiResult.Loading -> {
                // Don't emit anything for loading state
            }
        }
    }
}