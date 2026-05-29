package com.example.pivota.dashboard.data.remote

import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import com.example.pivota.dashboard.data.dto.PricingUnitsByCategoryResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class PricingUnitsApiService @Inject constructor(
    @param:AuthHttpClient private val client: HttpClient
) {

    /**
     * Get allowed pricing units for a specific category
     * @param categoryId - The category ID (must be COMPLIMENTARY type)
     * @return Pricing units with min/max prices and requirements
     */
    suspend fun getPricingUnitsByCategory(
        categoryId: String
    ): PricingUnitsByCategoryResponseDto {
        println("🔍 ========== GET PRICING UNITS BY CATEGORY REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/contractors-pricing/units/category/$categoryId")
        println("🔍 ==========================================================")

        return try {
            val response: PricingUnitsByCategoryResponseDto = client.get("v1/contractors-pricing/units/category/$categoryId") {
                contentType(ContentType.Application.Json)
            }.body()

            println("🔍 ========== GET PRICING UNITS BY CATEGORY RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CATEGORY: ${response.data?.categoryName}")
            println("🔍 ALLOWED UNITS COUNT: ${response.data?.allowedUnits?.size ?: 0}")
            response.data?.allowedUnits?.forEach { unit ->
                println("🔍   - ${unit.unit}: ${unit.minPrice} - ${unit.maxPrice} (${unit.currency})")
            }
            println("🔍 ==========================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Pricing Units Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Pricing Units Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Pricing Units Failed: ${e.message}")
            throw e
        }
    }
}