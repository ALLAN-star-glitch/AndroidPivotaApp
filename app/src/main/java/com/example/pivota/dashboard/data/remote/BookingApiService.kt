package com.example.pivota.dashboard.data.remote

import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import com.example.pivota.dashboard.data.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class BookingApiService @Inject constructor(
    @param:AuthHttpClient private val client: HttpClient
) {

    private companion object {
        const val BASE_PATH = "bookings"
    }

    // ===========================================================
    // BOOKING STATUS
    // ===========================================================

    /**
     * Get all booking statuses for dropdown/UI
     * GET /v1/bookings/statuses
     */
    suspend fun getBookingStatuses(): BaseBookingStatusListResponseDto {
        println("🔍 ========== GET BOOKING STATUSES REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/statuses")
        println("🔍 ==================================================")

        return try {
            val response: BaseBookingStatusListResponseDto = client.get("$BASE_PATH/statuses") {
                contentType(ContentType.Application.Json)
            }.body()

            println("🔍 ========== GET BOOKING STATUSES RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 STATUSES COUNT: ${response.data?.statuses?.size ?: 0}")
            println("🔍 ===================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Booking Statuses Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Booking Statuses Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Booking Statuses Failed: ${e.message}")
            throw e
        }
    }

    // ===========================================================
    // BOOKING CRUD OPERATIONS
    // ===========================================================

    /**
     * Create a new booking
     * POST /v1/bookings
     */
    suspend fun createBooking(
        request: CreateBookingRequestDto
    ): BaseBookingResponseDto {
        println("🔍 ========== CREATE BOOKING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH")
        println("🔍 REQUEST: serviceId=${request.serviceId}, contractorId=${request.contractorId}")
        println("🔍 ============================================")

        return try {
            val response: BaseBookingResponseDto = client.post(BASE_PATH) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== CREATE BOOKING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            response.data?.let { booking ->
                println("🔍 BOOKING ID: ${booking.id}")
                println("🔍 STATUS: ${booking.status}")
            }
            println("🔍 ============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Create Booking Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Create Booking Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Create Booking Failed: ${e.message}")
            throw e
        }
    }

    // ===========================================================
    // GET MY BOOKINGS
    // ===========================================================

    /**
     * Get my bookings as a customer
     * GET /v1/bookings/me/customer
     */
    suspend fun getMyBookingsAsCustomer(
        clientId: String,
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): PaginatedBookingsResponseDto {
        println("🔍 ========== GET MY BOOKINGS AS CUSTOMER REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/me/customer")
        println("🔍 CLIENT ID: $clientId")
        println("🔍 ========================================================")

        return try {
            val response: PaginatedBookingsResponseDto = client.get("$BASE_PATH/me/customer") {
                contentType(ContentType.Application.Json)
                parameter("clientId", clientId)
                status?.let { parameter("status", it) }
                parameter("limit", limit)
                parameter("offset", offset)
            }.body()

            println("🔍 ========== GET MY BOOKINGS AS CUSTOMER RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 BOOKINGS COUNT: ${response.data.size}")
            response.pagination?.let { pagination ->
                println("🔍 PAGINATION: total=${pagination.total}, hasMore=${pagination.hasMore}")
            }
            println("🔍 =========================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get My Bookings As Customer Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get My Bookings As Customer Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get My Bookings As Customer Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Get my bookings as a contractor/professional
     * GET /v1/bookings/me/contractor
     */
    suspend fun getMyBookingsAsProfessional(
        contractorId: String,
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): PaginatedBookingsResponseDto {
        println("🔍 ========== GET MY BOOKINGS AS PROFESSIONAL REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/me/contractor")
        println("🔍 CONTRACTOR ID: $contractorId")
        println("🔍 ============================================================")

        return try {
            val response: PaginatedBookingsResponseDto = client.get("$BASE_PATH/me/contractor") {
                contentType(ContentType.Application.Json)
                parameter("contractorId", contractorId)
                status?.let { parameter("status", it) }
                parameter("limit", limit)
                parameter("offset", offset)
            }.body()

            println("🔍 ========== GET MY BOOKINGS AS PROFESSIONAL RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 BOOKINGS COUNT: ${response.data.size}")
            response.pagination?.let { pagination ->
                println("🔍 PAGINATION: total=${pagination.total}, hasMore=${pagination.hasMore}")
            }
            println("🔍 =============================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get My Bookings As Professional Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get My Bookings As Professional Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get My Bookings As Professional Failed: ${e.message}")
            throw e
        }
    }

    // ===========================================================
    // GET BOOKING DETAILS
    // ===========================================================

    /**
     * Get booking details by ID
     * GET /v1/bookings/{bookingId}
     */
    suspend fun getBookingDetails(
        bookingId: String,
        userId: String,
        professionalId: String? = null
    ): BaseBookingResponseDto {
        println("🔍 ========== GET BOOKING DETAILS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/$bookingId")
        println("🔍 BOOKING ID: $bookingId")
        println("🔍 ================================================")

        return try {
            val response: BaseBookingResponseDto = client.get("$BASE_PATH/$bookingId") {
                contentType(ContentType.Application.Json)
                parameter("userId", userId)
                professionalId?.let { parameter("professionalId", it) }
            }.body()

            println("🔍 ========== GET BOOKING DETAILS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            response.data?.let { booking ->
                println("🔍 BOOKING ID: ${booking.id}")
                println("🔍 STATUS: ${booking.status}")
            }
            println("🔍 =================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Booking Details Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Booking Details Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Booking Details Failed: ${e.message}")
            throw e
        }
    }

    // ===========================================================
    // BOOKING ACTIONS
    // ===========================================================

    /**
     * Accept a booking (Contractor action)
     * PATCH /v1/bookings/{bookingId}/accept
     */
    suspend fun acceptBooking(
        bookingId: String,
        contractorId: String
    ): BookingActionResponseDto {
        println("🔍 ========== ACCEPT BOOKING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/$bookingId/accept")
        println("🔍 BOOKING ID: $bookingId")
        println("🔍 ============================================")

        return try {
            val request = mapOf(
                "bookingId" to bookingId,
                "contractorId" to contractorId
            )

            val response: BookingActionResponseDto = client.patch("$BASE_PATH/$bookingId/accept") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== ACCEPT BOOKING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            response.data?.let { action ->
                println("🔍 BOOKING ID: ${action.id}")
                println("🔍 STATUS: ${action.status}")
            }
            println("🔍 ============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Accept Booking Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Accept Booking Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Accept Booking Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Decline a booking (Contractor action)
     * PATCH /v1/bookings/{bookingId}/decline
     */
    suspend fun declineBooking(
        bookingId: String,
        contractorId: String,
        reason: String? = null
    ): BookingActionResponseDto {
        println("🔍 ========== DECLINE BOOKING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/$bookingId/decline")
        println("🔍 BOOKING ID: $bookingId")
        println("🔍 =============================================")

        return try {
            val request = mutableMapOf(
                "bookingId" to bookingId,
                "contractorId" to contractorId
            )
            reason?.let { request["reason"] = it }

            val response: BookingActionResponseDto = client.patch("$BASE_PATH/$bookingId/decline") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== DECLINE BOOKING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 =============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Decline Booking Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Decline Booking Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Decline Booking Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Cancel a booking
     * PATCH /v1/bookings/{bookingId}/cancel
     */
    suspend fun cancelBooking(
        bookingId: String,
        userId: String,
        professionalId: String? = null,
        reason: String? = null
    ): BookingActionResponseDto {
        println("🔍 ========== CANCEL BOOKING REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/$bookingId/cancel")
        println("🔍 BOOKING ID: $bookingId")
        println("🔍 ============================================")

        return try {
            val request = mutableMapOf(
                "bookingId" to bookingId,
                "userId" to userId
            )
            professionalId?.let { request["professionalId"] = it }
            reason?.let { request["reason"] = it }

            val response: BookingActionResponseDto = client.patch("$BASE_PATH/$bookingId/cancel") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== CANCEL BOOKING RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 ============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Cancel Booking Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Cancel Booking Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Cancel Booking Failed: ${e.message}")
            throw e
        }
    }

    // ===========================================================
    // CONTRACTOR DASHBOARD
    // ===========================================================

    /**
     * Get upcoming bookings for contractor dashboard
     * GET /v1/bookings/contractor/upcoming
     */
    suspend fun getUpcomingBookingsForProfessional(
        contractorId: String,
        limit: Int = 10
    ): ListUpcomingBookingsResponseDto {
        println("🔍 ========== GET UPCOMING BOOKINGS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/contractor/upcoming")
        println("🔍 CONTRACTOR ID: $contractorId")
        println("🔍 ==================================================")

        return try {
            val response: ListUpcomingBookingsResponseDto = client.get("$BASE_PATH/contractor/upcoming") {
                contentType(ContentType.Application.Json)
                parameter("contractorId", contractorId)
                parameter("limit", limit)
            }.body()

            println("🔍 ========== GET UPCOMING BOOKINGS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 BOOKINGS COUNT: ${response.data.size}")
            println("🔍 ====================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Upcoming Bookings Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Upcoming Bookings Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Upcoming Bookings Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Get booking statistics for contractor dashboard
     * GET /v1/bookings/contractor/stats
     */
    suspend fun getProfessionalBookingStats(
        contractorId: String
    ): BaseBookingStatsResponseDto {
        println("🔍 ========== GET BOOKING STATS REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/$BASE_PATH/contractor/stats")
        println("🔍 CONTRACTOR ID: $contractorId")
        println("🔍 ==============================================")

        return try {
            val response: BaseBookingStatsResponseDto = client.get("$BASE_PATH/contractor/stats") {
                contentType(ContentType.Application.Json)
                parameter("contractorId", contractorId)
            }.body()

            println("🔍 ========== GET BOOKING STATS RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            response.data?.let { stats ->
                println("🔍 TOTAL: ${stats.total}, PENDING: ${stats.pending}, CONFIRMED: ${stats.confirmed}")
            }
            println("🔍 ===============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Get Booking Stats Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Get Booking Stats Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Get Booking Stats Failed: ${e.message}")
            throw e
        }
    }
}