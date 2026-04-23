package com.example.pivota.dashboard.data.repository

import com.example.pivota.auth.data.mapper.AuthDataMapper
import com.example.pivota.auth.domain.model.CompleteProfileResult
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.dashboard.data.remote.ProfileApiService
import com.example.pivota.dashboard.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val mapper: AuthDataMapper
) : ProfileRepository {

    override suspend fun fetchProfile(): ApiResult<CompleteProfileResult> {
        return safeApiCall {
            profileApiService.fetchProfile()
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success && response.data != null) {
                        ApiResult.Success(mapper.toCompleteProfile(response.data))
                    } else {
                        ApiResult.Error(NetworkError.Unknown, response.message)
                    }
                }
                is ApiResult.Error -> apiResult
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }
}