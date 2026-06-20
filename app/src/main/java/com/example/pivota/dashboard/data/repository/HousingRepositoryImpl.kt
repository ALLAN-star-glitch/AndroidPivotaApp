package com.example.pivota.dashboard.data.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.dashboard.data.dto.*
import com.example.pivota.dashboard.data.mapper.HousingMapper
import com.example.pivota.dashboard.data.remote.HousingApiService
import com.example.pivota.dashboard.domain.model.listings_models.housing.*
import com.example.pivota.dashboard.domain.repository.HousingRepository
import javax.inject.Inject

class HousingRepositoryImpl @Inject constructor(
    private val housingApiService: HousingApiService,
    private val mapper: HousingMapper
) : HousingRepository {

    override suspend fun getAllHousingListings(
        params: GetAllHousingParams
    ): ApiResult<HouseListingsResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetAllHousingRequestDto(params)
            housingApiService.getAllHousingListings(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun searchHousingListings(
        params: SearchHousingParams
    ): ApiResult<HouseListingsResponse> {
        return safeApiCall {
            val requestDto = mapper.toSearchHousingRequestDto(params)
            housingApiService.searchHousingListings(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getHousingListingsByCategory(
        params: GetHousingByCategoryParams
    ): ApiResult<HouseListingsResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetHousingByCategoryRequestDto(params)
            housingApiService.getHousingListingsByCategory(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getHouseListingById(
        params: GetHouseByIdParams
    ): ApiResult<HouseListingResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetHouseByIdRequestDto(params)
            housingApiService.getHouseListingById(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getOwnHousingListings(
        params: GetOwnHousingParams
    ): ApiResult<HouseListingsResponse> {
        return safeApiCall {
            housingApiService.getMyHousingListings(
                status = params.status,
                limit = params.limit,
                offset = params.offset,
                sortBy = params.sortBy
            )
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun createHouseListing(
        params: CreateHouseParams
    ): ApiResult<HouseCreateResponse> {
        return safeApiCall {
            val requestDto = mapper.toCreateHouseRequestDto(params)
            housingApiService.createHouseListing(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseCreateResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun updateHouseListing(
        listingId: String,
        params: UpdateHouseParams
    ): ApiResult<HouseListingResponse> {
        return safeApiCall {
            val requestDto = mapper.toUpdateHouseRequestDto(params)
            housingApiService.updateHouseListing(listingId, requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun scheduleViewing(
        listingId: String,
        params: ScheduleViewingParams
    ): ApiResult<HouseViewingResponse> {
        return safeApiCall {
            val requestDto = mapper.toScheduleViewingRequestDto(params)
            housingApiService.scheduleViewing(listingId, requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseViewingResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    // ======================================================
    // ADMIN ENDPOINTS
    // ======================================================

    override suspend fun getAdminHousingListings(
        params: GetAdminHousingParams
    ): ApiResult<HouseListingsResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetAdminHousingRequestDto(params)
            housingApiService.getAdminHousingListings(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun adminCreateHouseListing(
        accountId: String,
        params: AdminCreateHouseParams
    ): ApiResult<HouseCreateResponse> {
        return safeApiCall {
            val requestDto = mapper.toAdminCreateHouseRequestDto(params)
            housingApiService.adminCreateHouseListing(accountId, requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseCreateResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun adminUpdateHouseListing(
        listingId: String,
        params: AdminUpdateHouseParams
    ): ApiResult<HouseListingResponse> {
        return safeApiCall {
            val requestDto = mapper.toAdminUpdateHouseRequestDto(params)
            housingApiService.adminUpdateHouseListing(listingId, requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseListingResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun adminScheduleViewing(
        listingId: String,
        params: AdminScheduleViewingParams
    ): ApiResult<HouseViewingResponse> {
        return safeApiCall {
            val requestDto = mapper.toAdminScheduleViewingRequestDto(params)
            housingApiService.adminScheduleViewing(listingId, requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toHouseViewingResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }
}