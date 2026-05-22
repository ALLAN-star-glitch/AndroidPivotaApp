package com.example.pivota.dashboard.data.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.dashboard.data.mapper.ProfileDtoMapper
import com.example.pivota.dashboard.data.remote.ProfileApiService
import com.example.pivota.dashboard.domain.model.profile_models.CompleteProfile
import com.example.pivota.dashboard.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val mapper: ProfileDtoMapper
) : ProfileRepository {

    override suspend fun fetchProfile(): ApiResult<CompleteProfile> {
        return safeApiCall {
            profileApiService.fetchProfile()  // Returns ProfileResponseDto (wrapper with success, message, code, data)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data  // This is ProfileResponseDto
                    if (response.success && response.data != null) {
                        val domainProfile = mapper.toDomain(response.data)  // Pass UserProfDto to mapper
                        if (domainProfile != null) {
                            ApiResult.Success(domainProfile)
                        } else {
                            // ✅ FIXED: Use data class constructor
                            ApiResult.Error(
                                networkError = NetworkError.Unknown(
                                    originalMessage = "Failed to map profile data"
                                ),
                                technicalMessage = "Failed to map profile data"
                            )
                        }
                    } else {
                        // ✅ FIXED: Use data class constructor
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message ?: "Unknown error"
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