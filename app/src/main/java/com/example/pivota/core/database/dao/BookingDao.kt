package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pivota.core.database.entity.BookingCacheMetadataEntity
import com.example.pivota.core.database.entity.BookingEntity
import com.example.pivota.core.database.entity.BookingStatsEntity
import com.example.pivota.core.database.entity.BookingStatusEntity
import com.example.pivota.core.database.entity.UpcomingBookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    // ===========================================================
    // BOOKING CRUD OPERATIONS
    // ===========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Query("SELECT * FROM bookings WHERE id = :bookingId OR externalId = :bookingId")
    suspend fun getBookingById(bookingId: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE id = :bookingId OR externalId = :bookingId")
    fun getBookingByIdFlow(bookingId: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE clientId = :clientId ORDER BY scheduledDate DESC")
    suspend fun getCustomerBookings(clientId: String): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE clientId = :clientId ORDER BY scheduledDate DESC")
    fun getCustomerBookingsFlow(clientId: String): Flow<List<BookingEntity>>

    @Query("""
        SELECT * FROM bookings 
        WHERE clientId = :clientId 
        AND (:status IS NULL OR status = :status)
        ORDER BY scheduledDate DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getCustomerBookingsPaginated(
        clientId: String,
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<BookingEntity>

    @Query("""
        SELECT COUNT(*) FROM bookings 
        WHERE clientId = :clientId 
        AND (:status IS NULL OR status = :status)
    """)
    suspend fun getCustomerBookingsCount(
        clientId: String,
        status: String? = null
    ): Int

    @Query("SELECT * FROM bookings WHERE contractorId = :contractorId ORDER BY createdAt DESC")
    suspend fun getProfessionalBookings(contractorId: String): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE contractorId = :contractorId ORDER BY createdAt DESC")
    fun getProfessionalBookingsFlow(contractorId: String): Flow<List<BookingEntity>>

    @Query("""
        SELECT * FROM bookings 
        WHERE contractorId = :contractorId 
        AND (:status IS NULL OR status = :status)
        ORDER BY createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getProfessionalBookingsPaginated(
        contractorId: String,
        status: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<BookingEntity>

    @Query("""
        SELECT COUNT(*) FROM bookings 
        WHERE contractorId = :contractorId 
        AND (:status IS NULL OR status = :status)
    """)
    suspend fun getProfessionalBookingsCount(
        contractorId: String,
        status: String? = null
    ): Int

    @Query("DELETE FROM bookings WHERE id = :bookingId OR externalId = :bookingId")
    suspend fun deleteBooking(bookingId: String)

    @Query("DELETE FROM bookings WHERE clientId = :clientId")
    suspend fun deleteCustomerBookingsCache(clientId: String)

    @Query("DELETE FROM bookings WHERE contractorId = :contractorId")
    suspend fun deleteProfessionalBookingsCache(contractorId: String)

    @Query("DELETE FROM bookings")
    suspend fun clearAllBookings()

    // ===========================================================
// UPCOMING BOOKINGS
// ===========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingBookings(bookings: List<UpcomingBookingEntity>)

    @Query("""
    SELECT * FROM upcoming_bookings 
    WHERE contractorId = :contractorId 
    ORDER BY scheduledDate ASC 
    LIMIT :limit
""")
    suspend fun getUpcomingBookings(
        contractorId: String,
        limit: Int = 10
    ): List<UpcomingBookingEntity>

    @Query("SELECT * FROM upcoming_bookings WHERE contractorId = :contractorId ORDER BY scheduledDate ASC")
    fun getUpcomingBookingsFlow(
        contractorId: String  // Removed the unused limit parameter
    ): Flow<List<UpcomingBookingEntity>>

    @Query("DELETE FROM upcoming_bookings WHERE contractorId = :contractorId")
    suspend fun deleteUpcomingBookings(contractorId: String)

    @Query("DELETE FROM upcoming_bookings")
    suspend fun clearAllUpcomingBookings()
    // ===========================================================
    // BOOKING STATISTICS
    // ===========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookingStats(stats: BookingStatsEntity)

    @Query("SELECT * FROM booking_stats WHERE contractorId = :contractorId")
    suspend fun getBookingStats(contractorId: String): BookingStatsEntity?

    @Query("SELECT * FROM booking_stats WHERE contractorId = :contractorId")
    fun getBookingStatsFlow(contractorId: String): Flow<BookingStatsEntity?>

    @Query("DELETE FROM booking_stats WHERE contractorId = :contractorId")
    suspend fun deleteBookingStats(contractorId: String)

    @Query("DELETE FROM booking_stats")
    suspend fun clearAllBookingStats()

    // ===========================================================
    // BOOKING STATUSES
    // ===========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookingStatuses(statuses: List<BookingStatusEntity>)

    @Query("SELECT * FROM booking_statuses ORDER BY `order` ASC")
    suspend fun getBookingStatuses(): List<BookingStatusEntity>

    @Query("SELECT * FROM booking_statuses ORDER BY `order` ASC")
    fun getBookingStatusesFlow(): Flow<List<BookingStatusEntity>>

    @Query("SELECT * FROM booking_statuses WHERE value = :value")
    suspend fun getBookingStatusByValue(value: String): BookingStatusEntity?

    @Query("DELETE FROM booking_statuses")
    suspend fun clearAllBookingStatuses()

    // ===========================================================
    // CACHE METADATA
    // ===========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomerListCacheMetadata(metadata: BookingCacheMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfessionalListCacheMetadata(metadata: BookingCacheMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUpcomingListCacheMetadata(metadata: BookingCacheMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStatsCacheMetadata(metadata: BookingCacheMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStatusesCacheMetadata(metadata: BookingCacheMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookingCacheMetadata(metadata: BookingCacheMetadataEntity)

    @Query("SELECT * FROM booking_cache_metadata WHERE bookingId = :cacheKey")
    suspend fun getCustomerListCacheMetadata(cacheKey: String): BookingCacheMetadataEntity?

    @Query("SELECT * FROM booking_cache_metadata WHERE bookingId = :cacheKey")
    suspend fun getProfessionalListCacheMetadata(cacheKey: String): BookingCacheMetadataEntity?

    @Query("SELECT * FROM booking_cache_metadata WHERE bookingId = :cacheKey")
    suspend fun getUpcomingListCacheMetadata(cacheKey: String): BookingCacheMetadataEntity?

    @Query("SELECT * FROM booking_cache_metadata WHERE bookingId = :cacheKey")
    suspend fun getStatsCacheMetadata(cacheKey: String): BookingCacheMetadataEntity?

    @Query("SELECT * FROM booking_cache_metadata WHERE bookingId = 'booking_statuses'")
    suspend fun getStatusesCacheMetadata(): BookingCacheMetadataEntity?

    @Query("SELECT * FROM booking_cache_metadata WHERE bookingId = :bookingId")
    suspend fun getBookingCacheMetadata(bookingId: String): BookingCacheMetadataEntity?

    // ===========================================================
    // CACHE CLEARING METHODS
    // ===========================================================

    @Query("DELETE FROM booking_cache_metadata WHERE bookingId LIKE 'customer:%'")
    suspend fun deleteAllCustomerListCache()

    @Query("DELETE FROM booking_cache_metadata WHERE bookingId LIKE 'professional:%'")
    suspend fun deleteAllProfessionalListCache()

    @Query("DELETE FROM booking_cache_metadata WHERE bookingId LIKE 'upcoming:%'")
    suspend fun deleteAllUpcomingListCache()

    @Query("DELETE FROM booking_cache_metadata WHERE bookingId LIKE 'stats:%'")
    suspend fun deleteAllStatsCache()

    @Query("DELETE FROM booking_cache_metadata WHERE bookingId = 'booking_statuses'")
    suspend fun deleteAllStatusesCache()

    @Query("DELETE FROM booking_cache_metadata")
    suspend fun clearAllCacheMetadata()

    @Query("DELETE FROM booking_cache_metadata WHERE bookingId = :cacheKey")
    suspend fun deleteCacheMetadata(cacheKey: String)

    // ===========================================================
    // CLEANUP OPERATIONS
    // ===========================================================

    @Query("DELETE FROM bookings WHERE updatedAt < :timestamp")
    suspend fun deleteStaleBookings(timestamp: Long)

    @Query("DELETE FROM upcoming_bookings WHERE scheduledDate < :timestamp")
    suspend fun deleteStaleUpcomingBookings(timestamp: Long)

    @Query("DELETE FROM booking_cache_metadata WHERE lastUpdated < :timestamp")
    suspend fun deleteStaleCacheMetadata(timestamp: Long)

    // ===========================================================
    // COUNT OPERATIONS
    // ===========================================================

    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun getTotalBookingsCount(): Int

    @Query("SELECT COUNT(*) FROM bookings WHERE status = :status")
    suspend fun getBookingsCountByStatus(status: String): Int

    @Query("SELECT COUNT(*) FROM bookings WHERE clientId = :clientId")
    suspend fun getCustomerBookingsTotalCount(clientId: String): Int

    @Query("SELECT COUNT(*) FROM bookings WHERE contractorId = :contractorId")
    suspend fun getProfessionalBookingsTotalCount(contractorId: String): Int
}