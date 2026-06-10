package com.example.pivota.dashboard.data.mapper

import com.example.pivota.core.database.entity.*
import com.example.pivota.dashboard.domain.model.listings_models.professionals.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingCacheMapper @Inject constructor(
    private val moshi: Moshi
) {

    companion object {
        private val STRING_LIST_TYPE = Types.newParameterizedType(List::class.java, String::class.java)
        private val DAY_AVAILABILITY_LIST_TYPE = Types.newParameterizedType(List::class.java, DayAvailability::class.java)
    }

    // ===========================================================
    // BOOKING MAPPING
    // ===========================================================

    fun toEntity(booking: Booking): BookingEntity {
        return BookingEntity(
            id = booking.id,
            externalId = booking.externalId,
            contractorId = booking.contractorId,
            clientId = booking.clientId,
            serviceId = booking.serviceId,
            contractorName = booking.contractorName,
            serviceTitle = booking.serviceTitle,
            status = booking.status,
            serviceExecutionStatus = booking.serviceExecutionStatus,
            scheduledDate = booking.scheduledDate,
            locationCity = booking.locationCity,
            servicePrice = booking.servicePrice,
            servicePriceUnit = booking.servicePriceUnit,
            serviceDuration = booking.serviceDuration,
            currency = booking.currency,
            customerNotes = booking.customerNotes,
            bookingFeeAmount = booking.bookingFeeAmount,
            bookingFeeCurrency = booking.bookingFeeCurrency,
            bookingFeeRefundable = booking.bookingFeeRefundable,
            totalAmount = booking.totalAmount,
            confirmedAt = booking.confirmedAt,
            declinedAt = booking.declinedAt,
            cancelledAt = booking.cancelledAt,
            completedAt = booking.completedAt,
            createdAt = booking.createdAt,
            updatedAt = booking.updatedAt,
            lastUpdated = System.currentTimeMillis(),
            categoryId = booking.service?.categoryId
        )
    }

    fun toEntityList(bookings: List<Booking>): List<BookingEntity> {
        return bookings.map { toEntity(it) }
    }

    fun toDomain(entity: BookingEntity): Booking {
        return Booking(
            id = entity.id,
            externalId = entity.externalId,
            contractorId = entity.contractorId,
            clientId = entity.clientId,
            serviceId = entity.serviceId,
            service = null, // Service details fetched separately if needed
            contractorName = entity.contractorName,
            serviceTitle = entity.serviceTitle,
            status = entity.status,
            serviceExecutionStatus = entity.serviceExecutionStatus,
            scheduledDate = entity.scheduledDate,
            locationCity = entity.locationCity,
            servicePrice = entity.servicePrice,
            servicePriceUnit = entity.servicePriceUnit,
            serviceDuration = entity.serviceDuration,
            currency = entity.currency,
            customerNotes = entity.customerNotes,
            bookingFeeAmount = entity.bookingFeeAmount,
            bookingFeeCurrency = entity.bookingFeeCurrency,
            bookingFeeRefundable = entity.bookingFeeRefundable,
            totalAmount = entity.totalAmount,
            confirmedAt = entity.confirmedAt,
            declinedAt = entity.declinedAt,
            cancelledAt = entity.cancelledAt,
            completedAt = entity.completedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            clientInfo = null, // Not cached - fetch fresh when needed
            contractorInfo = null, // Not cached - fetch fresh when needed
            serviceDetails = null // Not cached - fetch fresh when needed
        )
    }

    fun toDomainList(entities: List<BookingEntity>): List<Booking> {
        return entities.map { toDomain(it) }
    }

    // ===========================================================
    // UPCOMING BOOKING MAPPING
    // ===========================================================

    fun toUpcomingEntity(booking: UpcomingBooking, contractorId: String): UpcomingBookingEntity {
        return UpcomingBookingEntity(
            id = booking.id,
            contractorId = contractorId,
            serviceTitle = booking.serviceTitle,
            scheduledDate = booking.scheduledDate,
            locationCity = booking.locationCity,
            clientName = booking.clientName,
            clientPhone = booking.clientPhone,
            agreedPrice = booking.agreedPrice,
            currency = booking.currency,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun toUpcomingEntityList(bookings: List<UpcomingBooking>, contractorId: String): List<UpcomingBookingEntity> {
        return bookings.map { toUpcomingEntity(it, contractorId) }
    }

    fun toUpcomingDomain(entity: UpcomingBookingEntity): UpcomingBooking {
        return UpcomingBooking(
            id = entity.id,
            serviceTitle = entity.serviceTitle,
            scheduledDate = entity.scheduledDate,
            locationCity = entity.locationCity,
            clientName = entity.clientName,
            clientPhone = entity.clientPhone,
            agreedPrice = entity.agreedPrice,
            currency = entity.currency
        )
    }

    fun toUpcomingDomainList(entities: List<UpcomingBookingEntity>): List<UpcomingBooking> {
        return entities.map { toUpcomingDomain(it) }
    }

    // ===========================================================
    // BOOKING STATISTICS MAPPING
    // ===========================================================

    fun toStatsEntity(stats: BookingStatistics, contractorId: String): BookingStatsEntity {
        return BookingStatsEntity(
            contractorId = contractorId,
            total = stats.total,
            pending = stats.pending,
            confirmed = stats.confirmed,
            completed = stats.completed,
            cancelled = stats.cancelled,
            declined = stats.declined,
            upcoming = stats.upcoming,
            completedThisMonth = stats.completedThisMonth,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun toStatsDomain(entity: BookingStatsEntity): BookingStatistics {
        return BookingStatistics(
            total = entity.total,
            pending = entity.pending,
            confirmed = entity.confirmed,
            completed = entity.completed,
            cancelled = entity.cancelled,
            declined = entity.declined,
            upcoming = entity.upcoming,
            completedThisMonth = entity.completedThisMonth
        )
    }

    // ===========================================================
    // BOOKING STATUS MAPPING
    // ===========================================================

    fun toStatusEntity(status: BookingStatus): BookingStatusEntity {
        return BookingStatusEntity(
            value = status.value,
            label = status.label,
            description = status.description,
            badgeVariant = status.badgeVariant,
            order = status.order,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun toStatusEntityList(statuses: List<BookingStatus>): List<BookingStatusEntity> {
        return statuses.map { toStatusEntity(it) }
    }

    fun toStatusDomain(entity: BookingStatusEntity): BookingStatus {
        return BookingStatus(
            value = entity.value,
            label = entity.label,
            description = entity.description,
            badgeVariant = entity.badgeVariant,
            order = entity.order
        )
    }

    fun toStatusDomainList(entities: List<BookingStatusEntity>): List<BookingStatus> {
        return entities.map { toStatusDomain(it) }
    }
}