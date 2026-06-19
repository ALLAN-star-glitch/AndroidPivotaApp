package com.example.pivota.dashboard.data.remote

import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import com.example.pivota.dashboard.data.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class JobPostsApiService @Inject constructor(
    @param:AuthHttpClient private val client: HttpClient
) {

    // ======================================================
    // GET ALL JOBS
    // ======================================================

    suspend fun getAllJobs(
        request: GetAllJobsRequestDto
    ): JobPostsResponseDto {
        println("🔍 ========== GET ALL JOBS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/jobs")
        println("🔍 PARAMS: limit=${request.limit}, offset=${request.offset}, city=${request.city}")
        println("🔍 ===========================================")

        return try {
            val response: JobPostsResponseDto = client.get("jobs-module/jobs") {
                contentType(ContentType.Application.Json)
                parameter("limit", request.limit)
                parameter("offset", request.offset)
                request.city?.let { parameter("city", it) }
                request.minPay?.let { parameter("minPay", it) }
                request.maxPay?.let { parameter("maxPay", it) }
                parameter("sortBy", request.sortBy)
                // Job Characteristics
                request.employmentType?.let { parameter("employmentType", it) }
                request.paymentType?.let { parameter("paymentType", it) }
                request.workArrangement?.let { parameter("workArrangement", it) }
                request.commitment?.let { parameter("commitment", it) }
                request.workSchedule?.let { parameter("workSchedule", it) }
                request.documentationLevel?.let { parameter("documentationLevel", it) }
                request.skillLevel?.let { parameter("skillLevel", it) }
                request.experienceLevel?.let { parameter("experienceLevel", it) }
                request.educationLevel?.let { parameter("educationLevel", it) }
                // New Filters
                request.isAnonymous?.let { parameter("isAnonymous", it) }
                request.isRemote?.let { parameter("isRemote", it) }
                request.applicationDeadlineAfter?.let { parameter("applicationDeadlineAfter", it) }
                request.applicationDeadlineBefore?.let { parameter("applicationDeadlineBefore", it) }
                request.startDateAfter?.let { parameter("startDateAfter", it) }
                request.startDateBefore?.let { parameter("startDateBefore", it) }
                request.hoursPerWeekMin?.let { parameter("hoursPerWeekMin", it) }
                request.hoursPerWeekMax?.let { parameter("hoursPerWeekMax", it) }
                // Cache Control
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.skipCache) parameter("skipCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 300) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== GET ALL JOBS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 JOBS COUNT: ${response.data?.size ?: 0}")
            println("🔍 ===========================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get All Jobs Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get All Jobs Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get All Jobs Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GET JOBS BY CATEGORY
    // ======================================================

    suspend fun getJobsByCategory(
        request: GetJobsByCategoryRequestDto
    ): JobPostsResponseDto {
        println("🔍 ========== GET JOBS BY CATEGORY REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/jobs/category")
        println("🔍 CATEGORY ID: ${request.categoryId}")
        println("🔍 ==================================================")

        return try {
            val response: JobPostsResponseDto = client.get("jobs-module/jobs/category") {
                contentType(ContentType.Application.Json)
                parameter("categoryId", request.categoryId)
                parameter("limit", request.limit)
                parameter("offset", request.offset)
                request.city?.let { parameter("city", it) }
                request.minPay?.let { parameter("minPay", it) }
                request.maxPay?.let { parameter("maxPay", it) }
                parameter("sortBy", request.sortBy)
                // Job Characteristics
                request.employmentType?.let { parameter("employmentType", it) }
                request.paymentType?.let { parameter("paymentType", it) }
                request.workArrangement?.let { parameter("workArrangement", it) }
                request.commitment?.let { parameter("commitment", it) }
                request.workSchedule?.let { parameter("workSchedule", it) }
                request.documentationLevel?.let { parameter("documentationLevel", it) }
                request.skillLevel?.let { parameter("skillLevel", it) }
                request.experienceLevel?.let { parameter("experienceLevel", it) }
                request.educationLevel?.let { parameter("educationLevel", it) }
                // New Filters
                request.isAnonymous?.let { parameter("isAnonymous", it) }
                request.isRemote?.let { parameter("isRemote", it) }
                request.applicationDeadlineAfter?.let { parameter("applicationDeadlineAfter", it) }
                request.applicationDeadlineBefore?.let { parameter("applicationDeadlineBefore", it) }
                request.startDateAfter?.let { parameter("startDateAfter", it) }
                request.startDateBefore?.let { parameter("startDateBefore", it) }
                request.hoursPerWeekMin?.let { parameter("hoursPerWeekMin", it) }
                request.hoursPerWeekMax?.let { parameter("hoursPerWeekMax", it) }
                // Cache Control
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.skipCache) parameter("skipCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 300) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== GET JOBS BY CATEGORY RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 JOBS COUNT: ${response.data?.size ?: 0}")
            println("🔍 ==================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Jobs By Category Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Jobs By Category Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Jobs By Category Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GET JOB BY ID
    // ======================================================

    suspend fun getJobById(
        request: GetJobByIdRequestDto
    ): JobPostResponseDto {
        println("🔍 ========== GET JOB BY ID REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/details/${request.id}")
        println("🔍 JOB ID: ${request.id}")
        println("🔍 ===========================================")

        return try {
            val response: JobPostResponseDto = client.get("jobs-module/details/${request.id}") {
                contentType(ContentType.Application.Json)
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 600) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== GET JOB BY ID RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Job By ID Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Job By ID Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Job By ID Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GET JOB LISTINGS BY OWNER
    // ======================================================

    suspend fun getJobListingsByOwner(
        request: GetJobListingsByOwnerRequestDto
    ): JobPostsResponseDto {
        println("🔍 ========== GET JOB LISTINGS BY OWNER REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/jobs/owner/${request.accountId}")
        println("🔍 ACCOUNT ID: ${request.accountId}")
        println("🔍 =======================================================")

        return try {
            val response: JobPostsResponseDto = client.get("jobs-module/jobs/owner/${request.accountId}") {
                contentType(ContentType.Application.Json)
                request.status?.let { parameter("status", it) }
                parameter("limit", request.limit)
                parameter("offset", request.offset)
                parameter("sortBy", request.sortBy)
                if (request.bypassCache) parameter("bypassCache", true)
                if (request.skipCache) parameter("skipCache", true)
                if (request.refreshCache) parameter("refreshCache", true)
                if (request.cacheTTL != 300) parameter("cacheTTL", request.cacheTTL)
                if (request.readOnly) parameter("readOnly", true)
            }.body()

            println("🔍 ========== GET JOB LISTINGS BY OWNER RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 JOBS COUNT: ${response.data?.size ?: 0}")
            println("🔍 ========================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Job Listings By Owner Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Job Listings By Owner Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Job Listings By Owner Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // CREATE JOB POST
    // ======================================================

    suspend fun createJobPost(
        request: CreateJobPostRequestDto
    ): CreateJobPostResponseDto {
        println("🔍 ========== CREATE JOB POST REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/jobs")
        println("🔍 TITLE: ${request.title}")
        println("🔍 CATEGORY: ${request.categoryId}")
        println("🔍 ==============================================")

        return try {
            val response: CreateJobPostResponseDto = client.post("jobs-module/jobs") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== CREATE JOB POST RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ==============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Create Job Post Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Create Job Post Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Create Job Post Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // UPDATE JOB POST
    // ======================================================

    suspend fun updateJobPost(
        jobId: String,
        request: UpdateJobPostRequestDto
    ): JobPostResponseDto {
        println("🔍 ========== UPDATE JOB POST REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/jobs/$jobId")
        println("🔍 JOB ID: $jobId")
        println("🔍 ==============================================")

        return try {
            val response: JobPostResponseDto = client.patch("jobs-module/jobs/$jobId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== UPDATE JOB POST RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ==============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Update Job Post Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Update Job Post Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Update Job Post Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // CLOSE JOB POST
    // ======================================================

    suspend fun closeJobPost(
        jobId: String
    ): CloseJobPostResponseDto {
        println("🔍 ========== CLOSE JOB POST REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/jobs/$jobId/close")
        println("🔍 JOB ID: $jobId")
        println("🔍 =============================================")

        return try {
            val response: CloseJobPostResponseDto = client.patch("jobs-module/jobs/$jobId/close") {
                contentType(ContentType.Application.Json)
            }.body()

            println("🔍 ========== CLOSE JOB POST RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 =============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Close Job Post Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Close Job Post Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Close Job Post Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GET MY LISTINGS (Own Jobs)
    // ======================================================

    suspend fun getMyListings(
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: String = "recent",
        // Job Characteristics Filters
        employmentType: String? = null,
        paymentType: String? = null,
        workArrangement: String? = null,
        commitment: String? = null,
        experienceLevel: String? = null,
        educationLevel: String? = null,
        // New Filters
        isAnonymous: Boolean? = null,
        hoursPerWeekMin: Int? = null,
        hoursPerWeekMax: Int? = null
    ): JobPostsResponseDto {
        println("🔍 ========== GET MY LISTINGS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/jobs-module/my-listings")
        println("🔍 =============================================")

        return try {
            val response: JobPostsResponseDto = client.get("jobs-module/my-listings") {
                contentType(ContentType.Application.Json)
                status?.let { parameter("status", it) }
                parameter("limit", limit)
                parameter("offset", offset)
                parameter("sortBy", sortBy)
                // Job Characteristics
                employmentType?.let { parameter("employmentType", it) }
                paymentType?.let { parameter("paymentType", it) }
                workArrangement?.let { parameter("workArrangement", it) }
                commitment?.let { parameter("commitment", it) }
                experienceLevel?.let { parameter("experienceLevel", it) }
                educationLevel?.let { parameter("educationLevel", it) }
                // New Filters
                isAnonymous?.let { parameter("isAnonymous", it) }
                hoursPerWeekMin?.let { parameter("hoursPerWeekMin", it) }
                hoursPerWeekMax?.let { parameter("hoursPerWeekMax", it) }
            }.body()

            println("🔍 ========== GET MY LISTINGS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 JOBS COUNT: ${response.data?.size ?: 0}")
            println("🔍 =============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get My Listings Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get My Listings Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get My Listings Failed: ${e.message}")
            throw e
        }
    }
}