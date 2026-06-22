// core/network/useCase/HealthUseCase.kt
package com.example.pivota.core.network.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.dto.HealthCheckResponseDto
import com.example.pivota.core.network.repository.HealthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthUseCase @Inject constructor(
    private val healthRepository: HealthRepository
) {

    suspend operator fun invoke(): ApiResult<HealthCheckResponseDto> {
        return healthRepository.healthCheck()
    }
}