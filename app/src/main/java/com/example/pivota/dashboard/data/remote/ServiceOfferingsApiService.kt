package com.example.pivota.dashboard.data.remote

import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingResponseDto
import com.example.pivota.dashboard.data.dto.ServiceOfferingsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class ServiceOfferingsApiService @Inject constructor(
    @param:AuthHttpClient private val client: HttpClient
) {

    /**
     * Get service offerings by category ID
     * @param categoryId - The category ID (must be COMPLIMENTARY type)
     * @param limit - Results per page (default: 20)
     * @param offset - Pagination offset (default: 0)
     * @param city - Filter by city (optional)
     * @param minPrice - Minimum price filter (optional)
     * @param maxPrice - Maximum price filter (optional)
     */
    suspend fun getOfferingsByCategory(
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): ServiceOfferingsResponseDto {
        println("🔍 ========== GET OFFERINGS BY CATEGORY REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/contractors-module/service-offerings/category/$categoryId")
        println("🔍 PARAMS: limit=$limit, offset=$offset, city=$city, minPrice=$minPrice, maxPrice=$maxPrice")
        println("🔍 =======================================================")

        return try {
            val response: ServiceOfferingsResponseDto = client.get("v1/contractors-module/service-offerings/category/$categoryId") {
                contentType(ContentType.Application.Json)
                parameter("limit", limit)
                parameter("offset", offset)
                city?.let { parameter("city", it) }
                minPrice?.let { parameter("minPrice", it) }
                maxPrice?.let { parameter("maxPrice", it) }
            }.body()

            println("🔍 ========== GET OFFERINGS BY CATEGORY RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 OFFERINGS COUNT: ${response.data?.size ?: 0}")
            response.pagination?.let { pagination ->
                println("🔍 PAGINATION: total=${pagination.total}, hasMore=${pagination.hasMore}")
            }
            println("🔍 =======================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Offerings By Category Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Offerings By Category Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Offerings By Category Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Create a new service offering
     * @param request - The service offering creation request
     */
    suspend fun createServiceOffering(
        request: CreateServiceOfferingRequestDto
    ): CreateServiceOfferingResponseDto {
        println("🔍 ========== CREATE SERVICE OFFERING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/contractors-module/service-offerings")
        println("🔍 REQUEST: title=${request.title}, categoryId=${request.categoryId}, basePrice=${request.basePrice}")
        println("🔍 =====================================================")

        return try {
            val response: CreateServiceOfferingResponseDto = client.post("v1/contractors-module/service-offerings") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== CREATE SERVICE OFFERING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 STATUS: ${response.status}")
            response.data?.let {
                println("🔍 CREATED OFFERING ID: ${it.id}")
                println("🔍 PROFESSIONAL NAME: ${it.professionalName}")
            }
            println("🔍 =====================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Create Service Offering Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Create Service Offering Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Create Service Offering Failed: ${e.message}")
            throw e
        }
    }
}