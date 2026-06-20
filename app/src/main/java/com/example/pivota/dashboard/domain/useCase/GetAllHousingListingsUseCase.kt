package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.housing.GetAllHousingParams
import com.example.pivota.dashboard.domain.model.listings_models.housing.HouseListingsResponse
import com.example.pivota.dashboard.domain.repository.HousingRepository
import javax.inject.Inject

class GetAllHousingListingsUseCase @Inject constructor(
    private val repository: HousingRepository
) {
    suspend operator fun invoke(
        params: GetAllHousingParams = GetAllHousingParams()
    ): ApiResult<HouseListingsResponse> {
        return repository.getAllHousingListings(params)
    }
}