package com.example.pivota.dashboard.data.remote

import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import com.example.pivota.dashboard.data.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class HousingApiService @Inject constructor(
    @param:AuthHttpClient private val client: HttpClient
) {

    // ======================================================
    // GET ALL HOUSING LISTINGS
    // ======================================================

    suspend fun getAllHousingListings(
        request: GetAllHousingRequestDto
    ): HouseListingsResponseDto {
        println("🔍 ========== GET ALL HOUSING LISTINGS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/listings")
        println("🔍 PARAMS: limit=${request.limit}, offset=${request.offset}, city=${request.city}")
        println("🔍 ======================================================")

        return try {
            val response: HouseListingsResponseDto = client.get("housing-module/listings") {
                contentType(ContentType.Application.Json)
                parameter("limit", request.limit)
                parameter("offset", request.offset)
                request.city?.let { parameter("city", it) }
                request.listingType?.let { parameter("listingType", it) }
                request.minPrice?.let { parameter("minPrice", it) }
                request.maxPrice?.let { parameter("maxPrice", it) }
                request.bedrooms?.let { parameter("bedrooms", it) }
                request.propertyType?.let { parameter("propertyType", it) }
                request.isFurnished?.let { parameter("isFurnished", it) }
                parameter("sortBy", request.sortBy)
                // Cache Control
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.skipCache) parameter("skipCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 300) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== GET ALL HOUSING LISTINGS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 LISTINGS COUNT: ${response.data?.size ?: 0}")
            println("🔍 ======================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get All Housing Listings Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get All Housing Listings Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get All Housing Listings Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // SEARCH HOUSING LISTINGS
    // ======================================================

    suspend fun searchHousingListings(
        request: SearchHousingRequestDto
    ): HouseListingsResponseDto {
        println("🔍 ========== SEARCH HOUSING LISTINGS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/listings/search")
        println("🔍 PARAMS: city=${request.city}, listingType=${request.listingType}")
        println("🔍 =====================================================")

        return try {
            val response: HouseListingsResponseDto = client.get("housing-module/listings/search") {
                contentType(ContentType.Application.Json)
                request.city?.let { parameter("city", it) }
                request.listingType?.let { parameter("listingType", it) }
                request.minPrice?.let { parameter("minPrice", it) }
                request.maxPrice?.let { parameter("maxPrice", it) }
                request.bedrooms?.let { parameter("bedrooms", it) }
                request.propertyType?.let { parameter("propertyType", it) }
                request.minLeaseTerm?.let { parameter("minLeaseTerm", it) }
                request.isPetFriendly?.let { parameter("isPetFriendly", it) }
                request.utilitiesIncluded?.let { parameter("utilitiesIncluded", it) }
                request.isNegotiable?.let { parameter("isNegotiable", it) }
                request.titleDeedAvailable?.let { parameter("titleDeedAvailable", it) }
                parameter("sortBy", request.sortBy)
                parameter("limit", request.limit)
                parameter("offset", request.offset)
                request.categoryId?.let { parameter("categoryId", it) }
                request.subCategoryId?.let { parameter("subCategoryId", it) }
                // Cache Control
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.skipCache) parameter("skipCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 300) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== SEARCH HOUSING LISTINGS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 LISTINGS COUNT: ${response.data?.size ?: 0}")
            println("🔍 =====================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Search Housing Listings Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Search Housing Listings Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Search Housing Listings Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GET HOUSING LISTINGS BY CATEGORY
    // ======================================================

    suspend fun getHousingListingsByCategory(
        request: GetHousingByCategoryRequestDto
    ): HouseListingsResponseDto {
        println("🔍 ========== GET HOUSING LISTINGS BY CATEGORY REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/listings/category")
        println("🔍 CATEGORY ID: ${request.categoryId}")
        println("🔍 =============================================================")

        return try {
            val response: HouseListingsResponseDto = client.get("housing-module/listings/category") {
                contentType(ContentType.Application.Json)
                parameter("categoryId", request.categoryId)
                request.city?.let { parameter("city", it) }
                request.listingType?.let { parameter("listingType", it) }
                request.minPrice?.let { parameter("minPrice", it) }
                request.maxPrice?.let { parameter("maxPrice", it) }
                request.bedrooms?.let { parameter("bedrooms", it) }
                request.propertyType?.let { parameter("propertyType", it) }
                request.isFurnished?.let { parameter("isFurnished", it) }
                parameter("limit", request.limit)
                parameter("offset", request.offset)
                // Cache Control
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.skipCache) parameter("skipCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 300) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== GET HOUSING LISTINGS BY CATEGORY RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 LISTINGS COUNT: ${response.data?.size ?: 0}")
            println("🔍 =============================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Housing Listings By Category Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Housing Listings By Category Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Housing Listings By Category Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GET HOUSING LISTING BY ID
    // ======================================================

    suspend fun getHouseListingById(
        request: GetHouseByIdRequestDto
    ): HouseListingResponseDto {
        println("🔍 ========== GET HOUSING LISTING BY ID REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/details/${request.id}")
        println("🔍 LISTING ID: ${request.id}")
        println("🔍 ======================================================")

        return try {
            val response: HouseListingResponseDto = client.get("housing-module/details/${request.id}") {
                contentType(ContentType.Application.Json)
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 600) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== GET HOUSING LISTING BY ID RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 LISTING ID: ${response.data?.id}")
            println("🔍 ======================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get House Listing By ID Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get House Listing By ID Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get House Listing By ID Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GET MY LISTINGS (Own Housing Listings)
    // ======================================================

    suspend fun getMyHousingListings(
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: String = "recent"
    ): HouseListingsResponseDto {
        println("🔍 ========== GET MY HOUSING LISTINGS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/my-listings")
        println("🔍 =====================================================")

        return try {
            val response: HouseListingsResponseDto = client.get("housing-module/my-listings") {
                contentType(ContentType.Application.Json)
                status?.let { parameter("status", it) }
                parameter("limit", limit)
                parameter("offset", offset)
                parameter("sortBy", sortBy)
            }.body()

            println("🔍 ========== GET MY HOUSING LISTINGS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 LISTINGS COUNT: ${response.data?.size ?: 0}")
            println("🔍 =====================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get My Housing Listings Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get My Housing Listings Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get My Housing Listings Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // CREATE HOUSING LISTING
    // ======================================================

    suspend fun createHouseListing(
        request: CreateHouseRequestDto
    ): HouseCreateResponseDto {
        println("🔍 ========== CREATE HOUSING LISTING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/listings")
        println("🔍 TITLE: ${request.title}")
        println("🔍 CATEGORY: ${request.categoryId}")
        println("🔍 ====================================================")

        return try {
            val response: HouseCreateResponseDto = client.post("housing-module/listings") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== CREATE HOUSING LISTING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ====================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Create Housing Listing Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Create Housing Listing Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Create Housing Listing Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // UPDATE HOUSING LISTING
    // ======================================================

    suspend fun updateHouseListing(
        listingId: String,
        request: UpdateHouseRequestDto
    ): HouseListingResponseDto {
        println("🔍 ========== UPDATE HOUSING LISTING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/listings/$listingId")
        println("🔍 LISTING ID: $listingId")
        println("🔍 ====================================================")

        return try {
            val response: HouseListingResponseDto = client.patch("housing-module/listings/$listingId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== UPDATE HOUSING LISTING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ====================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Update Housing Listing Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Update Housing Listing Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Update Housing Listing Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // SCHEDULE VIEWING
    // ======================================================

    suspend fun scheduleViewing(
        listingId: String,
        request: ScheduleViewingRequestDto
    ): HouseViewingResponseDto {
        println("🔍 ========== SCHEDULE VIEWING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/listings/$listingId/viewing")
        println("🔍 LISTING ID: $listingId")
        println("🔍 VIEWING DATE: ${request.viewingDate}")
        println("🔍 ==============================================")

        return try {
            val response: HouseViewingResponseDto = client.post("housing-module/listings/$listingId/viewing") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== SCHEDULE VIEWING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ==============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Schedule Viewing Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Schedule Viewing Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Schedule Viewing Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // ADMIN: GET ALL HOUSING LISTINGS
    // ======================================================

    suspend fun getAdminHousingListings(
        request: GetAdminHousingRequestDto
    ): HouseListingsResponseDto {
        println("🔍 ========== ADMIN: GET ALL HOUSING LISTINGS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/admin/listings")
        println("🔍 PARAMS: status=${request.status}, accountId=${request.accountId}")
        println("🔍 =============================================================")

        return try {
            val response: HouseListingsResponseDto = client.get("housing-module/admin/listings") {
                contentType(ContentType.Application.Json)
                request.status?.let { parameter("status", it) }
                request.accountId?.let { parameter("accountId", it) }
                request.creatorId?.let { parameter("creatorId", it) }
                request.listingType?.let { parameter("listingType", it) }
                request.propertyType?.let { parameter("propertyType", it) }
                request.minBedrooms?.let { parameter("minBedrooms", it) }
                request.minPrice?.let { parameter("minPrice", it) }
                request.maxPrice?.let { parameter("maxPrice", it) }
                parameter("limit", request.limit)
                parameter("offset", request.offset)
                // Cache Control
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.skipCache) parameter("skipCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 300) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== ADMIN: GET ALL HOUSING LISTINGS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 LISTINGS COUNT: ${response.data?.size ?: 0}")
            println("🔍 =============================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Admin: Get All Housing Listings Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Admin: Get All Housing Listings Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Admin: Get All Housing Listings Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // ADMIN: CREATE HOUSING LISTING FOR ANY ACCOUNT
    // ======================================================

    suspend fun adminCreateHouseListing(
        accountId: String,
        request: AdminCreateHouseRequestDto
    ): HouseCreateResponseDto {
        println("🔍 ========== ADMIN: CREATE HOUSING LISTING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/admin/accounts/$accountId/listings")
        println("🔍 ACCOUNT ID: $accountId")
        println("🔍 TITLE: ${request.title}")
        println("🔍 ===========================================================")

        return try {
            val response: HouseCreateResponseDto = client.post("housing-module/admin/accounts/$accountId/listings") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== ADMIN: CREATE HOUSING LISTING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ===========================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Admin: Create Housing Listing Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Admin: Create Housing Listing Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Admin: Create Housing Listing Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // ADMIN: UPDATE ANY HOUSING LISTING
    // ======================================================

    suspend fun adminUpdateHouseListing(
        listingId: String,
        request: AdminUpdateHouseRequestDto
    ): HouseListingResponseDto {
        println("🔍 ========== ADMIN: UPDATE HOUSING LISTING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/admin/listings/$listingId")
        println("🔍 LISTING ID: $listingId")
        println("🔍 ===========================================================")

        return try {
            val response: HouseListingResponseDto = client.patch("housing-module/admin/listings/$listingId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== ADMIN: UPDATE HOUSING LISTING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ===========================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Admin: Update Housing Listing Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Admin: Update Housing Listing Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Admin: Update Housing Listing Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // ADMIN: SCHEDULE VIEWING FOR ANY USER
    // ======================================================

    suspend fun adminScheduleViewing(
        listingId: String,
        request: AdminScheduleViewingRequestDto
    ): HouseViewingResponseDto {
        println("🔍 ========== ADMIN: SCHEDULE VIEWING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/housing-module/admin/listings/$listingId/viewing")
        println("🔍 LISTING ID: $listingId")
        println("🔍 TARGET VIEWER: ${request.targetViewerId}")
        println("🔍 =====================================================")

        return try {
            val response: HouseViewingResponseDto = client.post("housing-module/admin/listings/$listingId/viewing") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== ADMIN: SCHEDULE VIEWING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 =====================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Admin: Schedule Viewing Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Admin: Schedule Viewing Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Admin: Schedule Viewing Failed: ${e.message}")
            throw e
        }
    }
}