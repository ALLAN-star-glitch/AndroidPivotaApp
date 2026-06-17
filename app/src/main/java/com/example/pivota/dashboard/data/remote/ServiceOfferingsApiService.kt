package com.example.pivota.dashboard.data.remote

import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingResponseDto
import com.example.pivota.dashboard.data.dto.GetAllOfferingsRequestDto
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
        println("🔍 URL: ${NetworkConstants.BASE_URL}/contractors-module/service-offerings/category/$categoryId")
        println("🔍 PARAMS: limit=$limit, offset=$offset, city=$city, minPrice=$minPrice, maxPrice=$maxPrice")
        println("🔍 =======================================================")

        return try {
            val response: ServiceOfferingsResponseDto = client.get("contractors-module/service-offerings/category/$categoryId") {
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

    // ======================================================
    // NEW: GET ALL OFFERINGS (Across all categories)
    // ======================================================

    /**
     * Get all service offerings across all categories with pagination and filtering
     * @param limit - Results per page (default: 20)
     * @param offset - Pagination offset (default: 0)
     * @param city - Filter by city (optional)
     * @param minPrice - Minimum price filter (optional)
     * @param maxPrice - Maximum price filter (optional)
     * @param sortBy - Sort by option (recent, price_asc, price_desc, rating)
     * @param minRating - Minimum rating filter (1-5)
     * @param verifiedOnly - Show only verified professionals
     */
    suspend fun getAllOfferings(
        limit: Int = 20,
        offset: Int = 0,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        sortBy: String = "recent",
        minRating: Int? = null,
        verifiedOnly: Boolean = false
    ): ServiceOfferingsResponseDto {
        println("🔍 ========== GET ALL OFFERINGS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/contractors-module/service-offerings/all")
        println("🔍 PARAMS: limit=$limit, offset=$offset, city=$city, sortBy=$sortBy, minRating=$minRating, verifiedOnly=$verifiedOnly")
        println("🔍 PRICE RANGE: minPrice=$minPrice, maxPrice=$maxPrice")
        println("🔍 ===============================================")

        return try {
            val response: ServiceOfferingsResponseDto = client.get("contractors-module/service-offerings/all") {
                contentType(ContentType.Application.Json)
                parameter("limit", limit)
                parameter("offset", offset)
                city?.let { parameter("city", it) }
                minPrice?.let { parameter("minPrice", it) }
                maxPrice?.let { parameter("maxPrice", it) }
                parameter("sortBy", sortBy)
                minRating?.let { parameter("minRating", it) }
                parameter("verifiedOnly", verifiedOnly)
            }.body()

            println("🔍 ========== GET ALL OFFERINGS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 OFFERINGS COUNT: ${response.data?.size ?: 0}")
            response.pagination?.let { pagination ->
                println("🔍 PAGINATION: total=${pagination.total}, hasMore=${pagination.hasMore}")
            }
            println("🔍 ===============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get All Offerings Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get All Offerings Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get All Offerings Failed: ${e.message}")
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
        println("🔍 URL: ${NetworkConstants.BASE_URL}/contractors-module/service-offerings")
        println("🔍 REQUEST: title=${request.title}, categoryId=${request.categoryId}, basePrice=${request.basePrice}")
        println("🔍 =====================================================")

        return try {
            val response: CreateServiceOfferingResponseDto = client.post("contractors-module/service-offerings") {
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

    /**
     * Get a single service offering by ID
     * @param serviceId - The ID of the service offering to retrieve
     */
    suspend fun getServiceOfferingById(
        serviceId: String
    ): CreateServiceOfferingResponseDto {
        println("🔍 ========== GET SERVICE OFFERING BY ID REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/contractors-module/service-offerings/$serviceId")
        println("🔍 SERVICE ID: $serviceId")
        println("🔍 =======================================================")

        return try {
            val response: CreateServiceOfferingResponseDto = client.get("contractors-module/service-offerings/$serviceId") {
                contentType(ContentType.Application.Json)
            }.body()

            println("🔍 ========== GET SERVICE OFFERING BY ID RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            response.data?.let {
                println("🔍 OFFERING ID: ${it.id}")
                println("🔍 TITLE: ${it.title}")
                println("🔍 CATEGORY: ${it.categoryName}")
                println("🔍 PROFESSIONAL: ${it.professionalName}")
                println("🔍 VERIFIED: ${it.isVerified}")
                println("🔍 PRICE: ${it.basePrice} ${it.currency}")
                println("🔍 STATUS: ${it.status}")
            }
            println("🔍 ========================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Service Offering By ID Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Service Offering By ID Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Service Offering By ID Failed: ${e.message}")
            throw e
        }
    }
}