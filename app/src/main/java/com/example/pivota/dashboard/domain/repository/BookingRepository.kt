package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for booking operations
 */
interface BookingRepository {

    // ===========================================================
    // BOOKING CRUD OPERATIONS
    // ===========================================================

    /**
     * Create a new booking
     * @param request The booking creation request
     * @return ApiResult containing the created booking
     */
    suspend fun createBooking(
        request: CreateBookingRequest
    ): ApiResult<Booking>

    /**
     * Get booking details by ID
     * @param bookingId The booking external ID
     * @param userId The user ID making the request
     * @param professionalId Optional professional ID for authorization
     * @param forceRefresh Whether to force refresh from network
     * @return ApiResult containing the booking details
     */
    suspend fun getBookingDetails(
        bookingId: String,
        userId: String,
        professionalId: String? = null,
        forceRefresh: Boolean = false
    ): ApiResult<Booking>

    /**
     * Get booking details as a Flow for real-time updates
     */
    fun getBookingDetailsStream(
        bookingId: String,
        userId: String,
        professionalId: String? = null
    ): Flow<ApiResult<Booking>>

    // ===========================================================
    // GET MY BOOKINGS
    // ===========================================================

    /**
     * Get my bookings as a customer
     * @param clientId The client ID
     * @param status Optional status filter
     * @param limit Results per page
     * @param offset Pagination offset
     * @param forceRefresh Whether to force refresh from network
     * @return ApiResult containing paginated bookings
     */
    suspend fun getMyBookingsAsCustomer(
        clientId: String,
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        forceRefresh: Boolean = false
    ): ApiResult<PaginatedBookings>

    /**
     * Get customer bookings as a Flow for real-time updates
     */
    fun getCustomerBookingsStream(
        clientId: String,
        status: String? = null
    ): Flow<ApiResult<PaginatedBookings>>

    /**
     * Get my bookings as a contractor/professional
     * @param contractorId The contractor ID
     * @param status Optional status filter
     * @param limit Results per page
     * @param offset Pagination offset
     * @param forceRefresh Whether to force refresh from network
     * @return ApiResult containing paginated bookings
     */
    suspend fun getMyBookingsAsProfessional(
        contractorId: String,
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        forceRefresh: Boolean = false
    ): ApiResult<PaginatedBookings>

    /**
     * Get professional bookings as a Flow for real-time updates
     */
    fun getProfessionalBookingsStream(
        contractorId: String,
        status: String? = null
    ): Flow<ApiResult<PaginatedBookings>>

    // ===========================================================
    // BOOKING ACTIONS
    // ===========================================================

    /**
     * Accept a booking (Contractor action)
     * @param bookingId The booking external ID
     * @param contractorId The contractor ID
     * @return ApiResult containing the booking action response
     */
    suspend fun acceptBooking(
        bookingId: String,
        contractorId: String
    ): ApiResult<BookingAction>

    /**
     * Decline a booking (Contractor action)
     * @param bookingId The booking external ID
     * @param contractorId The contractor ID
     * @param reason Optional reason for declining
     * @return ApiResult containing the booking action response
     */
    suspend fun declineBooking(
        bookingId: String,
        contractorId: String,
        reason: String? = null
    ): ApiResult<BookingAction>

    /**
     * Cancel a booking
     * @param bookingId The booking external ID
     * @param userId The user ID cancelling
     * @param professionalId Optional professional ID
     * @param reason Optional reason for cancellation
     * @return ApiResult containing the booking action response
     */
    suspend fun cancelBooking(
        bookingId: String,
        userId: String,
        professionalId: String? = null,
        reason: String? = null
    ): ApiResult<BookingAction>

    // ===========================================================
    // CONTRACTOR DASHBOARD
    // ===========================================================

    /**
     * Get upcoming bookings for contractor dashboard
     * @param contractorId The contractor ID
     * @param limit Maximum number of results
     * @param forceRefresh Whether to force refresh from network
     * @return ApiResult containing list of upcoming bookings
     */
    suspend fun getUpcomingBookingsForProfessional(
        contractorId: String,
        limit: Int = 10,
        forceRefresh: Boolean = false
    ): ApiResult<List<UpcomingBooking>>

    /**
     * Get upcoming bookings as a Flow for real-time updates
     */
    fun getUpcomingBookingsStream(
        contractorId: String,
        limit: Int = 10
    ): Flow<ApiResult<List<UpcomingBooking>>>

    /**
     * Get booking statistics for contractor dashboard
     * @param contractorId The contractor ID
     * @param forceRefresh Whether to force refresh from network
     * @return ApiResult containing booking statistics
     */
    suspend fun getProfessionalBookingStats(
        contractorId: String,
        forceRefresh: Boolean = false
    ): ApiResult<BookingStatistics>

    /**
     * Get booking stats as a Flow for real-time updates
     */
    fun getBookingStatsStream(
        contractorId: String
    ): Flow<ApiResult<BookingStatistics>>

    // ===========================================================
    // BOOKING STATUSES
    // ===========================================================

    /**
     * Get all booking statuses for dropdown/UI
     * @param forceRefresh Whether to force refresh from network
     * @return ApiResult containing list of booking statuses
     */
    suspend fun getBookingStatuses(
        forceRefresh: Boolean = false
    ): ApiResult<List<BookingStatus>>

    /**
     * Get booking statuses as a Flow for real-time updates
     */
    fun getBookingStatusesStream(): Flow<ApiResult<List<BookingStatus>>>

    // ===========================================================
    // CACHE MANAGEMENT
    // ===========================================================

    /**
     * Refresh bookings for a customer
     * @param clientId The client ID
     * @param status Optional status filter
     */
    suspend fun refreshCustomerBookings(
        clientId: String,
        status: String? = null
    )

    /**
     * Refresh bookings for a professional
     * @param contractorId The contractor ID
     * @param status Optional status filter
     */
    suspend fun refreshProfessionalBookings(
        contractorId: String,
        status: String? = null
    )

    /**
     * Get cache status for customer bookings
     */
    suspend fun getCustomerBookingsCacheStatus(
        clientId: String,
        status: String? = null
    ): CacheStatus

    /**
     * Get cache status for professional bookings
     */
    suspend fun getProfessionalBookingsCacheStatus(
        contractorId: String,
        status: String? = null
    ): CacheStatus

    /**
     * Clear all booking cache
     */
    suspend fun clearAllBookingCache()

    /**
     * Clear cache for specific customer
     */
    suspend fun clearCustomerBookingsCache(clientId: String)

    /**
     * Clear cache for specific professional
     */
    suspend fun clearProfessionalBookingsCache(contractorId: String)

    /**
     * Clear cache for a specific booking
     */
    suspend fun clearBookingCache(bookingId: String)
}