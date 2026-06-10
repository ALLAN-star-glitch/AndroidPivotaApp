package com.example.pivota.dashboard.data.mapper

import com.example.pivota.dashboard.data.dto.ServiceOfferingsResponseDto
import com.example.pivota.dashboard.data.dto.ServiceOfferingDto
import com.example.pivota.dashboard.data.dto.DayAvailabilityDto
import com.example.pivota.dashboard.data.dto.PaginationInfoDto

import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PaginationInfo
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOfferingsResponse


import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ServiceOfferingMapper @Inject constructor() {

    fun toServiceOfferingsResponse(dto: ServiceOfferingsResponseDto): ServiceOfferingsResponse {
        return ServiceOfferingsResponse(
            success = dto.success,
            message = dto.message,
            code = dto.code,
            data = dto.data?.map { toServiceOffering(it) } ?: emptyList(),
            pagination = dto.pagination?.let { toPaginationInfo(it) }
        )
    }

    private fun toServiceOffering(dto: ServiceOfferingDto): ServiceOffering {
        return ServiceOffering(
            id = dto.id,
            externalId = dto.externalId,
            professionalName = dto.professionalName,
            skilledProfessionalId = dto.skilledProfessionalId,
            professionalAvatar = dto.professionalAvatar,
            isVerified = dto.isVerified,
            title = dto.title,
            description = dto.description,
            categoryId = dto.categoryId,
            categoryName = dto.categoryName,
            basePrice = dto.basePrice,
            priceUnit = dto.priceUnit,
            currency = dto.currency,
            coverageAreas = dto.coverageAreas,
            availability = dto.availability?.map { toDayAvailability(it) } ?: emptyList(),
            yearsExperience = dto.yearsExperience,
            hourlyRate = dto.hourlyRate,
            status = dto.status,
            averageRating = dto.averageRating,
            reviewCount = dto.reviewCount,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            // ========== NEW: Negotiable Pricing Fields ==========
            isNegotiable = dto.isNegotiable ?: true,
            minNegotiablePrice = dto.minNegotiablePrice,
            maxNegotiablePrice = dto.maxNegotiablePrice,
            // ========== NEW: Booking Fee Override Fields ==========
            useCustomBookingFee = dto.useCustomBookingFee ?: false,
            customBookingFeeEnabled = dto.customBookingFeeEnabled,
            customBookingFeeAmount = dto.customBookingFeeAmount,
            customBookingFeeCurrency = dto.customBookingFeeCurrency,
            customBookingFeeDescription = dto.customBookingFeeDescription,
            customBookingFeeRefundable = dto.customBookingFeeRefundable
        )
    }

    private fun toDayAvailability(dto: DayAvailabilityDto): DayAvailability {
        return DayAvailability(
            day = dto.day,
            open = dto.open,
            close = dto.close,
            isClosed = dto.isClosed
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


}