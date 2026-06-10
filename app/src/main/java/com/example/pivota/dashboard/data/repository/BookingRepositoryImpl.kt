package com.example.pivota.dashboard.data.repository

import android.util.Log
import com.example.pivota.core.database.dao.BookingDao
import com.example.pivota.core.database.entity.BookingCacheMetadataEntity
import com.example.pivota.core.database.entity.BookingEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.core.utils.NetworkMonitor
import com.example.pivota.dashboard.data.mapper.BookingCacheMapper
import com.example.pivota.dashboard.data.mapper.BookingMapper
import com.example.pivota.dashboard.data.remote.BookingApiService
import com.example.pivota.dashboard.domain.model.listings_models.professionals.*
import com.example.pivota.dashboard.domain.repository.BookingRepository
import com.example.pivota.dashboard.domain.repository.CacheStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val apiService: BookingApiService,
    private val bookingDao: BookingDao,
    private val networkMapper: BookingMapper,
    private val cacheMapper: BookingCacheMapper,
    private val networkMonitor: NetworkMonitor
) : BookingRepository {

    companion object {
        private const val TAG = "BookingRepository"

        private const val CACHE_EXPIRY_FRESH_MS = 5 * 60 * 1000L
        private const val CACHE_EXPIRY_STALE_MS = 30 * 60 * 1000L
        private const val CACHE_EXPIRY_OFFLINE_MS = 24 * 60 * 60 * 1000L
    }

    // ===========================================================
    // CREATE BOOKING
    // ===========================================================

    override suspend fun createBooking(request: CreateBookingRequest): ApiResult<Booking> {
        Log.d(TAG, "createBooking: serviceId=${request.serviceId}, contractorId=${request.contractorId}")

        val result = safeApiCall {
            apiService.createBooking(networkMapper.toCreateBookingRequestDto(request))
        }

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data != null) {
                    val booking = networkMapper.toBooking(response.data)

                    CoroutineScope(Dispatchers.IO).launch {
                        val entity = cacheMapper.toEntity(booking)
                        bookingDao.insertBooking(entity)
                        bookingDao.deleteCustomerBookingsCache(booking.clientId)
                        bookingDao.deleteProfessionalBookingsCache(booking.contractorId)
                        Log.d(TAG, "Booking cached and lists invalidated: ${booking.id}")
                    }

                    ApiResult.Success(booking)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown(originalMessage = response.message),
                        technicalMessage = response.message ?: "Booking creation failed"
                    )
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "createBooking error: ${result.technicalMessage}")
                ApiResult.Error(
                    networkError = result.networkError,
                    technicalMessage = result.technicalMessage
                )
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    // ===========================================================
    // GET BOOKING DETAILS
    // ===========================================================

    override suspend fun getBookingDetails(
        bookingId: String,
        userId: String,
        professionalId: String?,
        forceRefresh: Boolean
    ): ApiResult<Booking> {
        Log.d(TAG, "getBookingDetails: bookingId=$bookingId, forceRefresh=$forceRefresh")

        val isNetworkAvailable = networkMonitor.isNetworkAvailable()

        if (!forceRefresh) {
            val cachedBooking = bookingDao.getBookingById(bookingId)
            if (cachedBooking != null) {
                val metadata = bookingDao.getBookingCacheMetadata(bookingId)
                val now = System.currentTimeMillis()
                val isCacheFresh = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS

                if (isCacheFresh || !isNetworkAvailable) {
                    Log.d(TAG, "Returning cached booking: $bookingId")
                    return ApiResult.Success(cacheMapper.toDomain(cachedBooking))
                }
            }
        }

        if (forceRefresh && !isNetworkAvailable) {
            bookingDao.getBookingById(bookingId)?.let {
                Log.d(TAG, "Force refresh offline, returning cached version")
                return ApiResult.Success(cacheMapper.toDomain(it))
            }
        }

        val result = safeApiCall {
            apiService.getBookingDetails(bookingId, userId, professionalId)
        }

        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data != null) {
                    val booking = networkMapper.toBooking(response.data)

                    CoroutineScope(Dispatchers.IO).launch {
                        val entity = cacheMapper.toEntity(booking)
                        bookingDao.insertBooking(entity)
                        bookingDao.upsertBookingCacheMetadata(
                            BookingCacheMetadataEntity(booking.id, System.currentTimeMillis(), 0)
                        )
                        Log.d(TAG, "Booking cached from network: ${booking.id}")
                    }

                    ApiResult.Success(booking)
                } else {
                    bookingDao.getBookingById(bookingId)?.let {
                        Log.d(TAG, "Empty network response, returning cached version")
                        ApiResult.Success(cacheMapper.toDomain(it))
                    } ?: ApiResult.Error(
                        networkError = NetworkError.Unknown(originalMessage = response.message),
                        technicalMessage = response.message ?: "Booking not found"
                    )
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "getBookingDetails error: ${result.technicalMessage}")
                bookingDao.getBookingById(bookingId)?.let {
                    ApiResult.Success(cacheMapper.toDomain(it))
                } ?: ApiResult.Error(result.networkError, result.technicalMessage)
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    override fun getBookingDetailsStream(
        bookingId: String,
        userId: String,
        professionalId: String?
    ): Flow<ApiResult<Booking>> {
        return bookingDao.getBookingByIdFlow(bookingId).map { entity ->
            if (entity != null) ApiResult.Success(cacheMapper.toDomain(entity)) else ApiResult.Loading
        }
    }

    // ===========================================================
    // GET MY BOOKINGS AS CUSTOMER
    // ===========================================================

    override suspend fun getMyBookingsAsCustomer(
        clientId: String,
        status: String?,
        limit: Int,
        offset: Int,
        forceRefresh: Boolean
    ): ApiResult<PaginatedBookings> {
        Log.d(TAG, "getMyBookingsAsCustomer: clientId=$clientId, status=$status")

        val cacheKey = getCustomerCacheKey(clientId, status)
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()
        val now = System.currentTimeMillis()

        if (!forceRefresh && offset == 0) {
            val metadata = bookingDao.getCustomerListCacheMetadata(cacheKey)
            val isCacheFresh = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS

            if (isCacheFresh) {
                val cached = bookingDao.getCustomerBookings(clientId)
                if (cached.isNotEmpty()) {
                    Log.d(TAG, "Returning fresh cached customer bookings: ${cached.size}")
                    return ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(cached), null))
                }
            }

            val isCacheStale = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_STALE_MS
            if (isCacheStale && isNetworkAvailable) {
                val cached = bookingDao.getCustomerBookings(clientId)
                if (cached.isNotEmpty()) {
                    Log.d(TAG, "Returning stale customer cache with background refresh")
                    triggerCustomerBookingsBackgroundRefresh(clientId, status)
                    return ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(cached), null))
                }
            }
        }

        if (isNetworkAvailable) {
            return fetchCustomerBookingsFromNetwork(clientId, status, limit, offset)
        }

        val cached = bookingDao.getCustomerBookings(clientId)
        return if (cached.isNotEmpty()) {
            Log.d(TAG, "Offline: returning cached customer bookings")
            ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(cached), null))
        } else {
            ApiResult.Error(
                networkError = NetworkError.Unknown(originalMessage = "No internet and no cached data"),
                technicalMessage = "No cached data available for customer: $clientId"
            )
        }
    }

    override fun getCustomerBookingsStream(
        clientId: String,
        status: String?
    ): Flow<ApiResult<PaginatedBookings>> {
        return bookingDao.getCustomerBookingsFlow(clientId).map { entities ->
            if (entities.isNotEmpty()) {
                ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(entities), null))
            } else {
                ApiResult.Loading
            }
        }
    }

    // ===========================================================
    // GET MY BOOKINGS AS PROFESSIONAL
    // ===========================================================

    override suspend fun getMyBookingsAsProfessional(
        contractorId: String,
        status: String?,
        limit: Int,
        offset: Int,
        forceRefresh: Boolean
    ): ApiResult<PaginatedBookings> {
        Log.d(TAG, "getMyBookingsAsProfessional: contractorId=$contractorId, status=$status")

        val cacheKey = getProfessionalCacheKey(contractorId, status)
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()
        val now = System.currentTimeMillis()

        if (!forceRefresh && offset == 0) {
            val metadata = bookingDao.getProfessionalListCacheMetadata(cacheKey)
            val isCacheFresh = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS

            if (isCacheFresh) {
                val cached = bookingDao.getProfessionalBookings(contractorId)
                if (cached.isNotEmpty()) {
                    Log.d(TAG, "Returning fresh cached professional bookings: ${cached.size}")
                    return ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(cached), null))
                }
            }

            val isCacheStale = metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_STALE_MS
            if (isCacheStale && isNetworkAvailable) {
                val cached = bookingDao.getProfessionalBookings(contractorId)
                if (cached.isNotEmpty()) {
                    Log.d(TAG, "Returning stale professional cache with background refresh")
                    triggerProfessionalBookingsBackgroundRefresh(contractorId, status)
                    return ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(cached), null))
                }
            }
        }

        if (isNetworkAvailable) {
            return fetchProfessionalBookingsFromNetwork(contractorId, status, limit, offset)
        }

        val cached = bookingDao.getProfessionalBookings(contractorId)
        return if (cached.isNotEmpty()) {
            Log.d(TAG, "Offline: returning cached professional bookings")
            ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(cached), null))
        } else {
            ApiResult.Error(
                networkError = NetworkError.Unknown(originalMessage = "No internet and no cached data"),
                technicalMessage = "No cached data available for professional: $contractorId"
            )
        }
    }

    override fun getProfessionalBookingsStream(
        contractorId: String,
        status: String?
    ): Flow<ApiResult<PaginatedBookings>> {
        return bookingDao.getProfessionalBookingsFlow(contractorId).map { entities ->
            if (entities.isNotEmpty()) {
                ApiResult.Success(PaginatedBookings(cacheMapper.toDomainList(entities), null))
            } else {
                ApiResult.Loading
            }
        }
    }

    // ===========================================================
    // BOOKING ACTIONS
    // ===========================================================

    override suspend fun acceptBooking(bookingId: String, contractorId: String): ApiResult<BookingAction> {
        Log.d(TAG, "acceptBooking: bookingId=$bookingId")
        val result = safeApiCall { apiService.acceptBooking(bookingId, contractorId) }
        return handleBookingAction(result, bookingId)
    }

    override suspend fun declineBooking(bookingId: String, contractorId: String, reason: String?): ApiResult<BookingAction> {
        Log.d(TAG, "declineBooking: bookingId=$bookingId")
        val result = safeApiCall { apiService.declineBooking(bookingId, contractorId, reason) }
        return handleBookingAction(result, bookingId)
    }

    override suspend fun cancelBooking(bookingId: String, userId: String, professionalId: String?, reason: String?): ApiResult<BookingAction> {
        Log.d(TAG, "cancelBooking: bookingId=$bookingId")
        val result = safeApiCall { apiService.cancelBooking(bookingId, userId, professionalId, reason) }
        return handleBookingAction(result, bookingId)
    }

    private suspend fun handleBookingAction(
        result: ApiResult<com.example.pivota.dashboard.data.dto.BookingActionResponseDto>,
        bookingId: String
    ): ApiResult<BookingAction> {
        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data != null) {
                    val action = networkMapper.toBookingAction(response)
                    CoroutineScope(Dispatchers.IO).launch {
                        bookingDao.deleteBooking(bookingId)
                        bookingDao.deleteAllCustomerListCache()
                        bookingDao.deleteAllProfessionalListCache()
                        Log.d(TAG, "Cache invalidated for booking: $bookingId")
                    }
                    action?.let { ApiResult.Success(it) }
                        ?: ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = "No action data"),
                            technicalMessage = "No action data in response"
                        )
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown(originalMessage = response.message),
                        technicalMessage = response.message ?: "Action failed"
                    )
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "Booking action error: ${result.technicalMessage}")
                ApiResult.Error(
                    networkError = result.networkError,
                    technicalMessage = result.technicalMessage
                )
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    // ===========================================================
    // CONTRACTOR DASHBOARD
    // ===========================================================

    override suspend fun getUpcomingBookingsForProfessional(
        contractorId: String,
        limit: Int,
        forceRefresh: Boolean
    ): ApiResult<List<UpcomingBooking>> {
        Log.d(TAG, "getUpcomingBookingsForProfessional: contractorId=$contractorId")

        val cacheKey = getUpcomingCacheKey(contractorId)
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()
        val now = System.currentTimeMillis()

        if (!forceRefresh) {
            val metadata = bookingDao.getUpcomingListCacheMetadata(cacheKey)
            if (metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS) {
                val cached = bookingDao.getUpcomingBookings(contractorId, limit)
                if (cached.isNotEmpty()) {
                    Log.d(TAG, "Returning fresh cached upcoming bookings: ${cached.size}")
                    return ApiResult.Success(cacheMapper.toUpcomingDomainList(cached))
                }
            }
        }

        if (isNetworkAvailable || forceRefresh) {
            val result = safeApiCall { apiService.getUpcomingBookingsForProfessional(contractorId, limit) }
            return when (result) {
                is ApiResult.Success -> {
                    val response = result.data
                    if (response.success) {
                        val bookings = networkMapper.toUpcomingBookings(response)
                        CoroutineScope(Dispatchers.IO).launch {
                            bookingDao.insertUpcomingBookings(cacheMapper.toUpcomingEntityList(bookings, contractorId))
                            bookingDao.upsertUpcomingListCacheMetadata(
                                BookingCacheMetadataEntity(cacheKey, System.currentTimeMillis(), bookings.size)
                            )
                            Log.d(TAG, "Cached ${bookings.size} upcoming bookings")
                        }
                        ApiResult.Success(bookings)
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = response.message),
                            technicalMessage = response.message ?: "Failed to fetch upcoming bookings"
                        )
                    }
                }
                is ApiResult.Error -> {
                    val cached = bookingDao.getUpcomingBookings(contractorId, limit)
                    if (cached.isNotEmpty()) {
                        Log.d(TAG, "Network error, returning cached upcoming bookings")
                        ApiResult.Success(cacheMapper.toUpcomingDomainList(cached))
                    } else {
                        ApiResult.Error(result.networkError, result.technicalMessage)
                    }
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }

        val cached = bookingDao.getUpcomingBookings(contractorId, limit)
        return if (cached.isNotEmpty()) {
            ApiResult.Success(cacheMapper.toUpcomingDomainList(cached))
        } else {
            ApiResult.Error(
                networkError = NetworkError.Unknown(originalMessage = "No data"),
                technicalMessage = "No upcoming bookings found"
            )
        }
    }

    override fun getUpcomingBookingsStream(contractorId: String, limit: Int): Flow<ApiResult<List<UpcomingBooking>>> {
        return bookingDao.getUpcomingBookingsFlow(contractorId).map { entities ->
            if (entities.isNotEmpty()) ApiResult.Success(cacheMapper.toUpcomingDomainList(entities)) else ApiResult.Loading
        }
    }

    override suspend fun getProfessionalBookingStats(contractorId: String, forceRefresh: Boolean): ApiResult<BookingStatistics> {
        Log.d(TAG, "getProfessionalBookingStats: contractorId=$contractorId")

        val cacheKey = getStatsCacheKey(contractorId)
        val isNetworkAvailable = networkMonitor.isNetworkAvailable()
        val now = System.currentTimeMillis()

        if (!forceRefresh) {
            val metadata = bookingDao.getStatsCacheMetadata(cacheKey)
            if (metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS) {
                bookingDao.getBookingStats(contractorId)?.let {
                    Log.d(TAG, "Returning fresh cached stats")
                    return ApiResult.Success(cacheMapper.toStatsDomain(it))
                }
            }
        }

        if (isNetworkAvailable || forceRefresh) {
            val result = safeApiCall { apiService.getProfessionalBookingStats(contractorId) }
            return when (result) {
                is ApiResult.Success -> {
                    val response = result.data
                    if (response.success && response.data != null) {
                        val stats = networkMapper.toBookingStatistics(response)
                        stats?.let {
                            CoroutineScope(Dispatchers.IO).launch {
                                bookingDao.insertBookingStats(cacheMapper.toStatsEntity(it, contractorId))
                                bookingDao.upsertStatsCacheMetadata(
                                    BookingCacheMetadataEntity(cacheKey, System.currentTimeMillis(), 0)
                                )
                                Log.d(TAG, "Cached booking stats for contractor: $contractorId")
                            }
                            ApiResult.Success(it)
                        } ?: ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = "No stats data"),
                            technicalMessage = "No stats data in response"
                        )
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = response.message),
                            technicalMessage = response.message ?: "Failed to fetch stats"
                        )
                    }
                }
                is ApiResult.Error -> {
                    bookingDao.getBookingStats(contractorId)?.let {
                        Log.d(TAG, "Network error, returning cached stats")
                        ApiResult.Success(cacheMapper.toStatsDomain(it))
                    } ?: ApiResult.Error(result.networkError, result.technicalMessage)
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }

        return bookingDao.getBookingStats(contractorId)?.let {
            ApiResult.Success(cacheMapper.toStatsDomain(it))
        } ?: ApiResult.Error(
            networkError = NetworkError.Unknown(originalMessage = "No data"),
            technicalMessage = "No statistics available"
        )
    }

    override fun getBookingStatsStream(contractorId: String): Flow<ApiResult<BookingStatistics>> {
        return bookingDao.getBookingStatsFlow(contractorId).map { entity ->
            if (entity != null) ApiResult.Success(cacheMapper.toStatsDomain(entity)) else ApiResult.Loading
        }
    }

    // ===========================================================
    // BOOKING STATUSES
    // ===========================================================

    override suspend fun getBookingStatuses(forceRefresh: Boolean): ApiResult<List<BookingStatus>> {
        Log.d(TAG, "getBookingStatuses: forceRefresh=$forceRefresh")

        val now = System.currentTimeMillis()

        if (!forceRefresh) {
            val metadata = bookingDao.getStatusesCacheMetadata()
            if (metadata != null && (now - metadata.lastUpdated) < CACHE_EXPIRY_FRESH_MS) {
                val cached = bookingDao.getBookingStatuses()
                if (cached.isNotEmpty()) {
                    Log.d(TAG, "Returning fresh cached statuses")
                    return ApiResult.Success(cacheMapper.toStatusDomainList(cached))
                }
            }
        }

        val result = safeApiCall { apiService.getBookingStatuses() }
        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data != null) {
                    val statuses = networkMapper.toBookingStatusList(response)
                    CoroutineScope(Dispatchers.IO).launch {
                        bookingDao.insertBookingStatuses(cacheMapper.toStatusEntityList(statuses))
                        bookingDao.upsertStatusesCacheMetadata(
                            BookingCacheMetadataEntity("booking_statuses", System.currentTimeMillis(), statuses.size)
                        )
                        Log.d(TAG, "Cached ${statuses.size} booking statuses")
                    }
                    ApiResult.Success(statuses)
                } else {
                    val cached = bookingDao.getBookingStatuses()
                    if (cached.isNotEmpty()) {
                        Log.d(TAG, "Empty response, returning cached statuses")
                        ApiResult.Success(cacheMapper.toStatusDomainList(cached))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(originalMessage = response.message),
                            technicalMessage = response.message ?: "Failed to fetch statuses"
                        )
                    }
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "getBookingStatuses error: ${result.technicalMessage}")
                val cached = bookingDao.getBookingStatuses()
                if (cached.isNotEmpty()) {
                    ApiResult.Success(cacheMapper.toStatusDomainList(cached))
                } else {
                    ApiResult.Error(result.networkError, result.technicalMessage)
                }
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    override fun getBookingStatusesStream(): Flow<ApiResult<List<BookingStatus>>> {
        return bookingDao.getBookingStatusesFlow().map { entities ->
            if (entities.isNotEmpty()) ApiResult.Success(cacheMapper.toStatusDomainList(entities)) else ApiResult.Loading
        }
    }

    // ===========================================================
    // CACHE MANAGEMENT
    // ===========================================================

    override suspend fun refreshCustomerBookings(clientId: String, status: String?) {
        Log.d(TAG, "refreshCustomerBookings: clientId=$clientId")
        triggerCustomerBookingsBackgroundRefresh(clientId, status)
    }

    override suspend fun refreshProfessionalBookings(contractorId: String, status: String?) {
        Log.d(TAG, "refreshProfessionalBookings: contractorId=$contractorId")
        triggerProfessionalBookingsBackgroundRefresh(contractorId, status)
    }

    override suspend fun getCustomerBookingsCacheStatus(clientId: String, status: String?): CacheStatus {
        val metadata = bookingDao.getCustomerListCacheMetadata(getCustomerCacheKey(clientId, status))
        return metadata?.let {
            val age = System.currentTimeMillis() - it.lastUpdated
            when {
                age < CACHE_EXPIRY_FRESH_MS -> CacheStatus.Fresh(age)
                age < CACHE_EXPIRY_STALE_MS -> CacheStatus.Stale(age)
                else -> CacheStatus.Expired(age)
            }
        } ?: CacheStatus.Empty
    }

    override suspend fun getProfessionalBookingsCacheStatus(contractorId: String, status: String?): CacheStatus {
        val metadata = bookingDao.getProfessionalListCacheMetadata(getProfessionalCacheKey(contractorId, status))
        return metadata?.let {
            val age = System.currentTimeMillis() - it.lastUpdated
            when {
                age < CACHE_EXPIRY_FRESH_MS -> CacheStatus.Fresh(age)
                age < CACHE_EXPIRY_STALE_MS -> CacheStatus.Stale(age)
                else -> CacheStatus.Expired(age)
            }
        } ?: CacheStatus.Empty
    }

    override suspend fun clearAllBookingCache() {
        Log.d(TAG, "clearAllBookingCache")
        bookingDao.clearAllBookings()
        bookingDao.deleteAllCustomerListCache()      // changed from clearAllCustomerListCache
        bookingDao.deleteAllProfessionalListCache()  // changed from clearAllProfessionalListCache
        bookingDao.deleteAllUpcomingListCache()      // changed from clearAllUpcomingListCache
        bookingDao.deleteAllStatsCache()             // changed from clearAllStatsCache
        bookingDao.deleteAllStatusesCache()          // changed from clearAllStatusesCache
        bookingDao.clearAllCacheMetadata()
    }

    override suspend fun clearCustomerBookingsCache(clientId: String) {
        Log.d(TAG, "clearCustomerBookingsCache: clientId=$clientId")
        bookingDao.deleteCustomerBookingsCache(clientId)
    }

    override suspend fun clearProfessionalBookingsCache(contractorId: String) {
        Log.d(TAG, "clearProfessionalBookingsCache: contractorId=$contractorId")
        bookingDao.deleteProfessionalBookingsCache(contractorId)
    }

    override suspend fun clearBookingCache(bookingId: String) {
        Log.d(TAG, "clearBookingCache: bookingId=$bookingId")
        bookingDao.deleteBooking(bookingId)
    }

    // ===========================================================
    // PRIVATE HELPERS
    // ===========================================================

    private fun getCustomerCacheKey(clientId: String, status: String?) = "customer:$clientId:${status ?: "all"}"
    private fun getProfessionalCacheKey(contractorId: String, status: String?) = "professional:$contractorId:${status ?: "all"}"
    private fun getUpcomingCacheKey(contractorId: String) = "upcoming:$contractorId"
    private fun getStatsCacheKey(contractorId: String) = "stats:$contractorId"

    private suspend fun fetchCustomerBookingsFromNetwork(
        clientId: String, status: String?, limit: Int, offset: Int
    ): ApiResult<PaginatedBookings> {
        Log.d(TAG, "fetchCustomerBookingsFromNetwork: clientId=$clientId, status=$status")

        val result = safeApiCall { apiService.getMyBookingsAsCustomer(clientId, status, limit, offset) }
        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data.isNotEmpty()) {
                    val paginated = networkMapper.toPaginatedBookings(response)
                    val bookings = paginated.bookings

                    CoroutineScope(Dispatchers.IO).launch {
                        bookingDao.insertBookings(cacheMapper.toEntityList(bookings))
                        if (offset == 0) {
                            bookingDao.upsertCustomerListCacheMetadata(
                                BookingCacheMetadataEntity(getCustomerCacheKey(clientId, status), System.currentTimeMillis(), bookings.size)
                            )
                        }
                        Log.d(TAG, "Cached ${bookings.size} customer bookings")
                    }

                    ApiResult.Success(paginated)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown(originalMessage = response.message),
                        technicalMessage = response.message ?: "Failed to fetch customer bookings"
                    )
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "fetchCustomerBookingsFromNetwork error: ${result.technicalMessage}")
                result
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    private suspend fun fetchProfessionalBookingsFromNetwork(
        contractorId: String, status: String?, limit: Int, offset: Int
    ): ApiResult<PaginatedBookings> {
        Log.d(TAG, "fetchProfessionalBookingsFromNetwork: contractorId=$contractorId, status=$status")

        val result = safeApiCall { apiService.getMyBookingsAsProfessional(contractorId, status, limit, offset) }
        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                if (response.success && response.data.isNotEmpty()) {
                    val paginated = networkMapper.toPaginatedBookings(response)
                    val bookings = paginated.bookings

                    CoroutineScope(Dispatchers.IO).launch {
                        bookingDao.insertBookings(cacheMapper.toEntityList(bookings))
                        if (offset == 0) {
                            bookingDao.upsertProfessionalListCacheMetadata(
                                BookingCacheMetadataEntity(getProfessionalCacheKey(contractorId, status), System.currentTimeMillis(), bookings.size)
                            )
                        }
                        Log.d(TAG, "Cached ${bookings.size} professional bookings")
                    }

                    ApiResult.Success(paginated)
                } else {
                    ApiResult.Error(
                        networkError = NetworkError.Unknown(originalMessage = response.message),
                        technicalMessage = response.message ?: "Failed to fetch professional bookings"
                    )
                }
            }
            is ApiResult.Error -> {
                Log.e(TAG, "fetchProfessionalBookingsFromNetwork error: ${result.technicalMessage}")
                result
            }
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    private fun triggerCustomerBookingsBackgroundRefresh(clientId: String, status: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fetchCustomerBookingsFromNetwork(clientId, status, 20, 0)
                Log.d(TAG, "Background refresh completed for customer: $clientId")
            } catch (e: Exception) {
                Log.e(TAG, "Background refresh failed for customer: $clientId", e)
            }
        }
    }

    private fun triggerProfessionalBookingsBackgroundRefresh(contractorId: String, status: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fetchProfessionalBookingsFromNetwork(contractorId, status, 20, 0)
                Log.d(TAG, "Background refresh completed for professional: $contractorId")
            } catch (e: Exception) {
                Log.e(TAG, "Background refresh failed for professional: $contractorId", e)
            }
        }
    }
}