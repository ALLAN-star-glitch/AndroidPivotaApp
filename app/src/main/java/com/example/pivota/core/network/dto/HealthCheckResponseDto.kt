// core/network/dto/HealthCheckResponseDto.kt
package com.example.pivota.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthCheckResponseDto(
    val status: String,      // "ok" or "degraded"
    val service: String,     // "gateway" or "auth-service"
    val timestamp: String    // ISO timestamp e.g., "2026-06-22T10:18:49.769Z"
)