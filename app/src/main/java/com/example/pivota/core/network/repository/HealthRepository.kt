
package com.example.pivota.core.network.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.api.HealthApiService
import com.example.pivota.core.network.dto.HealthCheckResponseDto
import com.example.pivota.core.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor(
    private val healthApiService: HealthApiService
) {

    suspend fun healthCheck(): ApiResult<HealthCheckResponseDto> {
        return safeApiCall {
            healthApiService.healthCheck()
        }
    }
}