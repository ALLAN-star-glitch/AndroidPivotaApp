package com.example.pivota.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ======================================================
// RESPONSE DTOS (Based on NestJS DTOs)
// ======================================================

/**
 * Base response wrapper for single booking
 * Matches BaseResponseDto<BookingResponseDto> in NestJS
 */
@Serializable
data class BaseBookingResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: BookingResponseDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

/**
 * Main Booking Response DTO - Matches NestJS BookingResponseDto
 */
@Serializable
data class BookingResponseDto(
    @SerialName("id") val id: String,
    @SerialName("externalId") val externalId: String,
    @SerialName("contractorId") val contractorId: String,
    @SerialName("clientId") val clientId: String,
    @SerialName("serviceId") val serviceId: String? = null,
    @SerialName("service") val service: BookingServiceInfoDto? = null,
    @SerialName("contractorName") val contractorName: String? = null,
    @SerialName("serviceTitle") val serviceTitle: String? = null,
    @SerialName("status") val status: String,
    @SerialName("serviceExecutionStatus") val serviceExecutionStatus: String? = null,
    @SerialName("scheduledDate") val scheduledDate: String? = null,
    @SerialName("locationCity") val locationCity: String? = null,
    @SerialName("servicePrice") val servicePrice: Double? = null,
    @SerialName("servicePriceUnit") val servicePriceUnit: String? = null,
    @SerialName("serviceDuration") val serviceDuration: Int? = null,
    @SerialName("currency") val currency: String,
    @SerialName("customerNotes") val customerNotes: String? = null,
    @SerialName("bookingFeeAmount") val bookingFeeAmount: Double? = null,
    @SerialName("bookingFeeCurrency") val bookingFeeCurrency: String? = null,
    @SerialName("bookingFeeRefundable") val bookingFeeRefundable: Boolean? = null,
    @SerialName("totalAmount") val totalAmount: Double? = null,
    @SerialName("confirmedAt") val confirmedAt: String? = null,
    @SerialName("declinedAt") val declinedAt: String? = null,
    @SerialName("cancelledAt") val cancelledAt: String? = null,
    @SerialName("completedAt") val completedAt: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    // Enriched fields
    @SerialName("client") val client: BookingClientInfoDto? = null,
    @SerialName("contractor") val contractor: BookingContractorInfoDto? = null,
    @SerialName("serviceDetails") val serviceDetails: BookingServiceDetailsDto? = null
)

/**
 * Paginated response for booking lists
 * Matches PaginatedBookingsResponseDto in NestJS
 */
@Serializable
data class PaginatedBookingsResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<BookingResponseDto> = emptyList(),
    @SerialName("pagination") val pagination: PaginationInfoDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

/**
 * Response for booking actions (accept/decline/cancel)
 * Matches BookingActionResponseDto in NestJS
 */
@Serializable
data class BookingActionResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: BookingActionDataDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class BookingActionDataDto(
    @SerialName("id") val id: String,
    @SerialName("status") val status: String,
    @SerialName("updatedAt") val updatedAt: String
)

/**
 * Upcoming bookings response
 * Matches ListUpcomingBookingsResponseDto in NestJS
 */
@Serializable
data class ListUpcomingBookingsResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<UpcomingBookingDto> = emptyList(),
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class UpcomingBookingDto(
    @SerialName("id") val id: String,
    @SerialName("serviceTitle") val serviceTitle: String? = null,
    @SerialName("scheduledDate") val scheduledDate: String? = null,
    @SerialName("locationCity") val locationCity: String? = null,
    @SerialName("clientName") val clientName: String? = null,
    @SerialName("clientPhone") val clientPhone: String? = null,
    @SerialName("agreedPrice") val agreedPrice: Double? = null,
    @SerialName("currency") val currency: String
)

/**
 * Booking statistics response
 * Matches BaseBookingStatsResponseDto in NestJS
 */
@Serializable
data class BaseBookingStatsResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: BookingStatisticsDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class BookingStatisticsDto(
    @SerialName("total") val total: Int,
    @SerialName("pending") val pending: Int,
    @SerialName("confirmed") val confirmed: Int,
    @SerialName("completed") val completed: Int,
    @SerialName("cancelled") val cancelled: Int,
    @SerialName("declined") val declined: Int,
    @SerialName("upcoming") val upcoming: Int,
    @SerialName("completedThisMonth") val completedThisMonth: Int
)

/**
 * Booking status list response
 * Matches BaseBookingStatusListResponseDto in NestJS
 */
@Serializable
data class BaseBookingStatusListResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: BookingStatusListDataDto? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class BookingStatusListDataDto(
    @SerialName("statuses") val statuses: List<BookingStatusDto>
)

@Serializable
data class BookingStatusDto(
    @SerialName("value") val value: String,
    @SerialName("label") val label: String,
    @SerialName("description") val description: String,
    @SerialName("badgeVariant") val badgeVariant: String,
    @SerialName("order") val order: Int
)

// ======================================================
// ENRICHED INFO DTOS
// ======================================================

@Serializable
data class BookingServiceInfoDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("basePrice") val basePrice: Double,
    @SerialName("priceUnit") val priceUnit: String,
    @SerialName("currency") val currency: String,
    @SerialName("skilledProfessionalId") val skilledProfessionalId: String? = null  // Make optional

)

@Serializable
data class BookingClientInfoDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String
)

@Serializable
data class BookingContractorInfoDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("isVerified") val isVerified: Boolean,
    @SerialName("rating") val rating: Double,
    @SerialName("phone") val phone: String,
    @SerialName("email") val email: String
)

@Serializable
data class BookingServiceDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("basePrice") val basePrice: Double,
    @SerialName("priceUnit") val priceUnit: String,
    @SerialName("category") val category: String? = null
)

// ======================================================
// REQUEST DTOS (Based on NestJS DTOs)
// ======================================================

/**
 * Create booking request - Matches NestJS CreateBookingRequestDto
 * Note: clientId is included but will be validated/overridden by backend from JWT
 */
@Serializable
data class CreateBookingRequestDto(
    @SerialName("serviceId") val serviceId: String,
    @SerialName("contractorId") val contractorId: String,
    @SerialName("scheduledDate") val scheduledDate: String,
    @SerialName("locationCity") val locationCity: String? = null,
    @SerialName("customerNotes") val customerNotes: String? = null,
    @SerialName("durationHours") val durationHours: Int? = null,
    @SerialName("durationDays") val durationDays: Int? = null,
    @SerialName("durationWeeks") val durationWeeks: Int? = null,
    @SerialName("durationMonths") val durationMonths: Int? = null,
    @SerialName("proposedPrice") val proposedPrice: Double? = null
    // Note: clientId is NOT in request body - it comes from JWT
    // Note: isPlatformAdmin is NOT in request body - set by backend
)

/**
 * Accept booking request - Matches NestJS AcceptBookingRequestDto
 */
@Serializable
data class AcceptBookingRequestDto(
    @SerialName("bookingId") val bookingId: String,
    @SerialName("contractorId") val contractorId: String
)

/**
 * Decline booking request - Matches NestJS DeclineBookingRequestDto
 */
@Serializable
data class DeclineBookingRequestDto(
    @SerialName("bookingId") val bookingId: String,
    @SerialName("contractorId") val contractorId: String,
    @SerialName("reason") val reason: String? = null
)

/**
 * Cancel booking request - Matches NestJS CancelBookingRequestDto
 */
@Serializable
data class CancelBookingRequestDto(
    @SerialName("bookingId") val bookingId: String,
    @SerialName("userId") val userId: String,
    @SerialName("professionalId") val professionalId: String? = null,
    @SerialName("reason") val reason: String? = null
)

/**
 * Get customer bookings request - Matches NestJS GetCustomerBookingsRequestDto
 */
@Serializable
data class GetCustomerBookingsRequestDto(
    @SerialName("clientId") val clientId: String,
    @SerialName("status") val status: String? = null,
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0
)

/**
 * Get contractor bookings request - Matches NestJS GetContractorBookingsRequestDto
 */
@Serializable
data class GetContractorBookingsRequestDto(
    @SerialName("contractorId") val contractorId: String,
    @SerialName("status") val status: String? = null,
    @SerialName("limit") val limit: Int = 20,
    @SerialName("offset") val offset: Int = 0
)

/**
 * Get booking details request - Matches NestJS GetBookingDetailsRequestDto
 */
@Serializable
data class GetBookingDetailsRequestDto(
    @SerialName("bookingId") val bookingId: String,
    @SerialName("userId") val userId: String,
    @SerialName("professionalId") val professionalId: String? = null
)

/**
 * Get upcoming bookings request - Matches NestJS GetUpcomingBookingsRequestDto
 */
@Serializable
data class GetUpcomingBookingsRequestDto(
    @SerialName("contractorId") val contractorId: String,
    @SerialName("limit") val limit: Int = 10
)

/**
 * Get professional stats request - Matches NestJS GetProfessionalStatsRequestDto
 */
@Serializable
data class GetProfessionalStatsRequestDto(
    @SerialName("contractorId") val contractorId: String
)
