package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitsByCategory

interface PricingUnitsRepository {
    suspend fun getPricingUnitsByCategory(
        categoryId: String
    ): ApiResult<PricingUnitsByCategory>
}