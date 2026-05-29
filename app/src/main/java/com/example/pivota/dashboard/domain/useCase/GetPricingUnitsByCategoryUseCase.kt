package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitsByCategory
import com.example.pivota.dashboard.domain.repository.PricingUnitsRepository
import javax.inject.Inject

class GetPricingUnitsByCategoryUseCase @Inject constructor(
    private val repository: PricingUnitsRepository
) {

    /**
     * Get allowed pricing units for a specific category
     * @param categoryId The category ID (must be COMPLIMENTARY type)
     * @return ApiResult with PricingUnitsByCategory data
     */
    suspend operator fun invoke(
        categoryId: String
    ): ApiResult<PricingUnitsByCategory> {
        println("🟢 [GetPricingUnitsByCategoryUseCase] Called for category: $categoryId")
        return repository.getPricingUnitsByCategory(categoryId)
    }

    /**
     * Refresh pricing units (clear cache and fetch fresh)
     * Note: This is a pass-through since the repository handles caching
     */
    suspend fun refresh(categoryId: String): ApiResult<PricingUnitsByCategory> {
        println("🟢 [GetPricingUnitsByCategoryUseCase] Refresh called for category: $categoryId")
        // The repository doesn't have a direct refresh method yet,
        // but you could add cache invalidation if needed
        return repository.getPricingUnitsByCategory(categoryId)
    }
}