package com.example.pivota.dashboard.data.mapper

import com.example.pivota.dashboard.data.dto.*
import com.example.pivota.dashboard.domain.model.listings_models.professionals.*
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.text.SimpleDateFormat
import java.util.*

@Singleton
class BookingMapper @Inject constructor() {

    // ======================================================
    // RESPONSE MAPPINGS (DTO → Domain)
    // ======================================================

    fun toBooking(response: BaseBookingResponseDto): Booking? {
        return response.data?.let { toBooking(it) }
    }

    fun toBooking(dto: BookingResponseDto): Booking {
        return Booking(
            id = dto.id,
            externalId = dto.externalId,
            contractorId = dto.contractorId,
            clientId = dto.clientId,
            serviceId = dto.serviceId,
            service = dto.service?.let { toServiceOfferingInfo(it) },
            contractorName = dto.contractorName,
            serviceTitle = dto.serviceTitle,
            status = dto.status,
            serviceExecutionStatus = dto.serviceExecutionStatus,
            scheduledDate = dto.scheduledDate,
            locationCity = dto.locationCity,
            servicePrice = dto.servicePrice,
            servicePriceUnit = dto.servicePriceUnit,
            serviceDuration = dto.serviceDuration,
            currency = dto.currency,
            customerNotes = dto.customerNotes,
            bookingFeeAmount = dto.bookingFeeAmount,
            bookingFeeCurrency = dto.bookingFeeCurrency,
            bookingFeeRefundable = dto.bookingFeeRefundable,
            totalAmount = dto.totalAmount,
            confirmedAt = dto.confirmedAt,
            declinedAt = dto.declinedAt,
            cancelledAt = dto.cancelledAt,
            completedAt = dto.completedAt,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            clientInfo = dto.client?.let { toBookingClientInfo(it) },
            contractorInfo = dto.contractor?.let { toBookingContractorInfo(it) },
            serviceDetails = dto.serviceDetails?.let { toBookingServiceDetails(it) }
        )
    }

    fun toBookingList(dtos: List<BookingResponseDto>): List<Booking> {
        return dtos.map { toBooking(it) }
    }

    fun toPaginatedBookings(response: PaginatedBookingsResponseDto): PaginatedBookings {
        return PaginatedBookings(
            bookings = response.data.map { toBooking(it) },
            pagination = response.pagination?.let { toPaginationInfo(it) }
        )
    }

    // In BookingMapper.kt
    fun toBookingAction(response: BookingActionResponseDto): BookingAction? {
        return response.data?.let {
            BookingAction(
                id = it.id,
                status = it.status,
                updatedAt = it.updatedAt
            )
        }
    }

    fun toUpcomingBookings(response: ListUpcomingBookingsResponseDto): List<UpcomingBooking> {
        return response.data.map { toUpcomingBooking(it) }
    }

    fun toUpcomingBooking(dto: UpcomingBookingDto): UpcomingBooking {
        return UpcomingBooking(
            id = dto.id,
            serviceTitle = dto.serviceTitle,
            scheduledDate = dto.scheduledDate,
            locationCity = dto.locationCity,
            clientName = dto.clientName,
            clientPhone = dto.clientPhone,
            agreedPrice = dto.agreedPrice,
            currency = dto.currency
        )
    }

    fun toBookingStatistics(response: BaseBookingStatsResponseDto): BookingStatistics? {
        return response.data?.let {
            BookingStatistics(
                total = it.total,
                pending = it.pending,
                confirmed = it.confirmed,
                completed = it.completed,
                cancelled = it.cancelled,
                declined = it.declined,
                upcoming = it.upcoming,
                completedThisMonth = it.completedThisMonth
            )
        }
    }

    fun toBookingStatusList(response: BaseBookingStatusListResponseDto): List<BookingStatus> {
        return response.data?.statuses?.map { toBookingStatus(it) } ?: emptyList()
    }

    fun toBookingStatus(dto: BookingStatusDto): BookingStatus {
        return BookingStatus(
            value = dto.value,
            label = dto.label,
            description = dto.description,
            badgeVariant = dto.badgeVariant,
            order = dto.order
        )
    }

    // ======================================================
    // REQUEST MAPPINGS (Domain → DTO)
    // ======================================================

    fun toCreateBookingRequestDto(request: CreateBookingRequest): CreateBookingRequestDto {
        return CreateBookingRequestDto(
            serviceId = request.serviceId,
            contractorId = request.contractorId,
            scheduledDate = request.scheduledDate,
            locationCity = request.locationCity,
            customerNotes = request.customerNotes,
            durationHours = request.durationHours,
            durationDays = request.durationDays,
            durationWeeks = request.durationWeeks,
            durationMonths = request.durationMonths,
            proposedPrice = request.proposedPrice
        )
    }

    fun toAcceptBookingRequestDto(request: AcceptBookingRequest): AcceptBookingRequestDto {
        return AcceptBookingRequestDto(
            bookingId = request.bookingId,
            contractorId = request.contractorId
        )
    }

    fun toDeclineBookingRequestDto(request: DeclineBookingRequest): DeclineBookingRequestDto {
        return DeclineBookingRequestDto(
            bookingId = request.bookingId,
            contractorId = request.contractorId,
            reason = request.reason
        )
    }

    fun toCancelBookingRequestDto(request: CancelBookingRequest): CancelBookingRequestDto {
        return CancelBookingRequestDto(
            bookingId = request.bookingId,
            userId = request.userId,
            professionalId = request.professionalId,
            reason = request.reason
        )
    }

    fun toGetCustomerBookingsRequestDto(request: GetCustomerBookingsRequest): GetCustomerBookingsRequestDto {
        return GetCustomerBookingsRequestDto(
            clientId = request.clientId,
            status = request.status,
            limit = request.limit,
            offset = request.offset
        )
    }

    fun toGetContractorBookingsRequestDto(request: GetContractorBookingsRequest): GetContractorBookingsRequestDto {
        return GetContractorBookingsRequestDto(
            contractorId = request.contractorId,
            status = request.status,
            limit = request.limit,
            offset = request.offset
        )
    }

    fun toGetBookingDetailsRequestDto(request: GetBookingDetailsRequest): GetBookingDetailsRequestDto {
        return GetBookingDetailsRequestDto(
            bookingId = request.bookingId,
            userId = request.userId,
            professionalId = request.professionalId
        )
    }

    fun toGetUpcomingBookingsRequestDto(request: GetUpcomingBookingsRequest): GetUpcomingBookingsRequestDto {
        return GetUpcomingBookingsRequestDto(
            contractorId = request.contractorId,
            limit = request.limit
        )
    }

    fun toGetProfessionalStatsRequestDto(request: GetProfessionalStatsRequest): GetProfessionalStatsRequestDto {
        return GetProfessionalStatsRequestDto(
            contractorId = request.contractorId
        )
    }

    // ======================================================
    // HELPER MAPPING FUNCTIONS (Private)
    // ======================================================

    private fun toServiceOfferingInfo(dto: BookingServiceInfoDto): ServiceOffering {
        return ServiceOffering(
            id = dto.id,
            externalId = dto.id,
            skilledProfessionalId = dto.skilledProfessionalId ?: "",
            professionalName = "",
            professionalAvatar = null,
            isVerified = false,
            title = dto.title,
            description = "",
            categoryId = "",
            categoryName = "",
            basePrice = dto.basePrice,
            priceUnit = dto.priceUnit,
            currency = dto.currency,
            coverageAreas = emptyList(),
            availability = emptyList(),
            yearsExperience = null,
            hourlyRate = null,
            status = "",
            averageRating = 0.0,
            reviewCount = 0,
            createdAt = "",
            updatedAt = "",
            isNegotiable = false,
            minNegotiablePrice = null,
            maxNegotiablePrice = null,
            useCustomBookingFee = false,
            customBookingFeeEnabled = null,
            customBookingFeeAmount = null,
            customBookingFeeCurrency = null,
            customBookingFeeDescription = null,
            customBookingFeeRefundable = null,
        )
    }

    private fun toBookingClientInfo(dto: BookingClientInfoDto): BookingClientInfo {
        return BookingClientInfo(
            uuid = dto.uuid,
            name = dto.name,
            email = dto.email,
            phone = dto.phone
        )
    }

    private fun toBookingContractorInfo(dto: BookingContractorInfoDto): BookingContractorInfo {
        return BookingContractorInfo(
            uuid = dto.uuid,
            name = dto.name,
            profileImage = dto.profileImage,
            isVerified = dto.isVerified,
            rating = dto.rating,
            phone = dto.phone,
            email = dto.email
        )
    }

    private fun toBookingServiceDetails(dto: BookingServiceDetailsDto): BookingServiceDetails {
        return BookingServiceDetails(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            basePrice = dto.basePrice,
            priceUnit = dto.priceUnit,
            category = dto.category
        )
    }

    private fun toPaginationInfo(dto: PaginationInfoDto): PaginationInfo {
        return PaginationInfo(
            total = dto.total,
            limit = dto.limit,
            offset = dto.offset,
            hasMore = dto.hasMore
        )
    }

    // ======================================================
    // UTILITY FUNCTIONS FOR SCREEN INPUT
    // ======================================================

    fun createBookingRequestFromScreenInput(
        serviceOffering: ServiceOffering,
        contractorId: String,
        selectedDate: Date,
        durationValue: Int,
        durationType: DurationType,
        locationCity: String,
        customerNotes: String?,
        proposedPrice: Double?
    ): CreateBookingRequest {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val scheduledDateString = dateFormat.format(selectedDate)

        val durationHours: Int? = if (durationType == DurationType.HOURS) durationValue else null
        val durationDays: Int? = if (durationType == DurationType.DAYS) durationValue else null
        val durationWeeks: Int? = if (durationType == DurationType.WEEKS) durationValue else null
        val durationMonths: Int? = if (durationType == DurationType.MONTHS) durationValue else null

        return CreateBookingRequest(
            serviceId = serviceOffering.externalId,
            contractorId = contractorId,
            scheduledDate = scheduledDateString,
            locationCity = locationCity,
            durationHours = durationHours,
            durationDays = durationDays,
            durationWeeks = durationWeeks,
            durationMonths = durationMonths,
            customerNotes = customerNotes,
            proposedPrice = proposedPrice
        )
    }

    fun calculateTotalPrice(
        serviceOffering: ServiceOffering,
        durationValue: Int,
        proposedPrice: Double? = null
    ): Double {
        val basePricePerUnit = proposedPrice ?: serviceOffering.basePrice
        return basePricePerUnit * durationValue
    }

    fun calculateGrandTotal(
        servicePrice: Double,
        serviceOffering: ServiceOffering
    ): Double {
        val bookingFee = if (serviceOffering.useCustomBookingFee && serviceOffering.customBookingFeeEnabled == true) {
            serviceOffering.customBookingFeeAmount ?: 0.0
        } else 0.0
        return servicePrice + bookingFee
    }

    fun validateDuration(
        durationValue: Int,
        priceUnit: String
    ): DurationValidationResult {
        return when (priceUnit) {
            "PER_HOUR" -> {
                when {
                    durationValue <= 0 -> DurationValidationResult.Invalid("Duration must be greater than 0 hours")
                    durationValue > 24 -> DurationValidationResult.Invalid("Maximum duration is 24 hours")
                    else -> DurationValidationResult.Valid
                }
            }
            "PER_DAY" -> {
                when {
                    durationValue <= 0 -> DurationValidationResult.Invalid("Duration must be greater than 0 days")
                    durationValue > 30 -> DurationValidationResult.Invalid("Maximum duration is 30 days")
                    else -> DurationValidationResult.Valid
                }
            }
            "PER_WEEK" -> {
                when {
                    durationValue <= 0 -> DurationValidationResult.Invalid("Duration must be greater than 0 weeks")
                    durationValue > 52 -> DurationValidationResult.Invalid("Maximum duration is 52 weeks")
                    else -> DurationValidationResult.Valid
                }
            }
            "PER_MONTH" -> {
                when {
                    durationValue <= 0 -> DurationValidationResult.Invalid("Duration must be greater than 0 months")
                    durationValue > 12 -> DurationValidationResult.Invalid("Maximum duration is 12 months")
                    else -> DurationValidationResult.Valid
                }
            }
            "PER_SESSION", "FIXED" -> DurationValidationResult.Valid
            else -> DurationValidationResult.Valid
        }
    }

    fun validateProposedPrice(
        proposedPrice: Double,
        serviceOffering: ServiceOffering
    ): PriceValidationResult {
        return when {
            !serviceOffering.isNegotiable -> PriceValidationResult.Invalid("This service is not negotiable")
            serviceOffering.minNegotiablePrice != null && proposedPrice < serviceOffering.minNegotiablePrice ->
                PriceValidationResult.Invalid("Price too low. Minimum: ${serviceOffering.currency} ${formatPrice(serviceOffering.minNegotiablePrice)}")
            serviceOffering.maxNegotiablePrice != null && proposedPrice > serviceOffering.maxNegotiablePrice ->
                PriceValidationResult.Invalid("Price too high. Maximum: ${serviceOffering.currency} ${formatPrice(serviceOffering.maxNegotiablePrice)}")
            else -> PriceValidationResult.Valid
        }
    }

    private fun formatPrice(price: Double): String {
        return if (price == price.toLong().toDouble()) {
            price.toLong().toString()
        } else {
            String.format("%.2f", price)
        }
    }
}

// ======================================================
// SEALED CLASSES FOR VALIDATION RESULTS
// ======================================================

sealed class DurationValidationResult {
    object Valid : DurationValidationResult()
    data class Invalid(val message: String) : DurationValidationResult()
}

sealed class PriceValidationResult {
    object Valid : PriceValidationResult()
    data class Invalid(val message: String) : PriceValidationResult()
}