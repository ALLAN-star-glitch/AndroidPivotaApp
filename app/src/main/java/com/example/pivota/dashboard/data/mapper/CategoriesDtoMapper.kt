package com.example.pivota.dashboard.data.mapper

import com.example.pivota.dashboard.data.dto.CategoryDto
import com.example.pivota.dashboard.data.dto.DiscoveryCategoryDto
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Mapper for converting Category DTOs to Domain Models
 */
@Singleton
class CategoriesDtoMapper @Inject constructor() {

    /**
     * Convert DiscoveryCategoryDto to DiscoveryCategory domain model
     * Used for lightweight category lists (e.g., Common Services section)
     */
    fun toDomain(discoveryCategoryDto: DiscoveryCategoryDto): DiscoveryCategory {
        return DiscoveryCategory(
            id = discoveryCategoryDto.id,
            name = discoveryCategoryDto.name,
            slug = discoveryCategoryDto.slug,
            vertical = discoveryCategoryDto.vertical,
            type = discoveryCategoryDto.type,
            hasSubcategories = discoveryCategoryDto.hasSubcategories
        )
    }

    /**
     * Convert list of DiscoveryCategoryDto to list of DiscoveryCategory domain models
     */
    fun toDiscoveryDomainList(discoveryCategoryDtos: List<DiscoveryCategoryDto>): List<DiscoveryCategory> {
        return discoveryCategoryDtos.map { toDomain(it) }
    }

    /**
     * Convert CategoryDto to Category domain model
     * Used for full category details
     */
    fun toDomain(categoryDto: CategoryDto): Category {
        return Category(
            id = categoryDto.id,
            name = categoryDto.name,
            slug = categoryDto.slug,
            vertical = categoryDto.vertical,
            type = categoryDto.type,
            hasSubcategories = categoryDto.hasSubcategories,
            description = categoryDto.description,
            parentId = categoryDto.parentId,
            subcategoriesCount = categoryDto.subcategoriesCount,
            jobPostsCount = categoryDto.jobPostsCount,
            servicesCount = categoryDto.servicesCount,
            supportCount = categoryDto.supportCount,
            createdAt = categoryDto.createdAt,
            updatedAt = categoryDto.updatedAt
        )
    }

    /**
     * Convert list of CategoryDto to list of Category domain models
     */
    fun toCategoryDomainList(categoryDtos: List<CategoryDto>): List<Category> {
        return categoryDtos.map { toDomain(it) }
    }
}