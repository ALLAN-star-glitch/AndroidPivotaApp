// core/network/api/HealthApiService.kt
package com.example.pivota.core.network.api

import com.example.pivota.core.network.dto.HealthCheckResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthApiService @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun healthCheck(): HealthCheckResponseDto {
        return try {
            client.get("health") {
                timeout {
                    requestTimeoutMillis = 3000
                }
            }.body()
        } catch (e: Exception) {
            throw e
        }
    }
}