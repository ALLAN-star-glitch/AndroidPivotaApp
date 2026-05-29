package com.example.pivota.dashboard.data.mapper

import com.example.pivota.dashboard.data.dto.PricingUnitOptionDto
import com.example.pivota.dashboard.data.dto.PricingUnitsByCategoryDataDto
import com.example.pivota.dashboard.data.dto.PricingUnitsByCategoryResponseDto
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitOption
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitsByCategory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class PricingUnitsMapper @Inject constructor() {

    fun toDomain(dto: PricingUnitsByCategoryResponseDto): PricingUnitsByCategory? {
        if (!dto.success || dto.data == null) {
            return null
        }
        return toDomain(dto.data)
    }

    fun toDomain(dto: PricingUnitsByCategoryDataDto): PricingUnitsByCategory {
        return PricingUnitsByCategory(
            categoryId = dto.categoryId,
            categoryName = dto.categoryName,
            vertical = dto.vertical,
            allowedUnits = dto.allowedUnits.map { toDomain(it) }
        )
    }

    fun toDomain(dto: PricingUnitOptionDto): PricingUnitOption {
        return PricingUnitOption(
            unit = dto.unit,
            label = dto.label,
            description = dto.description,
            minPrice = dto.minPrice,
            maxPrice = dto.maxPrice,
            experienceRequired = dto.experienceRequired,
            notesRequired = dto.notesRequired,
            currency = dto.currency
        )
    }
}