package com.example.pivota.dashboard.data.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.dashboard.data.dto.PricingUnitsByCategoryResponseDto
import com.example.pivota.dashboard.data.mapper.PricingUnitsMapper
import com.example.pivota.dashboard.data.remote.PricingUnitsApiService
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitsByCategory
import com.example.pivota.dashboard.domain.repository.PricingUnitsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PricingUnitsRepositoryImpl @Inject constructor(
    private val apiService: PricingUnitsApiService,
    private val mapper: PricingUnitsMapper
) : PricingUnitsRepository {

    override suspend fun getPricingUnitsByCategory(
        categoryId: String
    ): ApiResult<PricingUnitsByCategory> {
        println("🔍 ========== GET PRICING UNITS BY CATEGORY ==========")
        println("🔍 CategoryId: $categoryId")
        println("🔍 ==================================================")

        val result = safeApiCall {
            apiService.getPricingUnitsByCategory(categoryId)
        }

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                println("🔍 PRICING UNITS RESPONSE: success=${response.success}, message=${response.message}")

                if (response.success && response.data != null) {
                    val domainData = mapper.toDomain(response)
                    if (domainData != null) {
                        ApiResult.Success(domainData)
                    } else {
                        ApiResult.Error(
                            networkError = com.example.pivota.core.network.NetworkError.Unknown(
                                originalMessage = "Failed to map response"
                            ),
                            technicalMessage = "Mapping failed"
                        )
                    }
                } else {
                    ApiResult.Error(
                        networkError = com.example.pivota.core.network.NetworkError.Unknown(
                            originalMessage = response.message
                        ),
                        technicalMessage = response.message
                    )
                }
            }
            is ApiResult.Error -> {
                println("❌ PRICING UNITS ERROR: ${result.technicalMessage}")
                ApiResult.Error(
                    networkError = result.networkError,
                    technicalMessage = result.technicalMessage
                )
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}