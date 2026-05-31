package com.example.pivota.dashboard.data.mapper

import com.example.pivota.core.database.entity.CategoryEntity
import com.example.pivota.core.database.entity.DiscoveryCategoryEntity
import com.example.pivota.dashboard.data.dto.CategoryDto
import com.example.pivota.dashboard.data.dto.DiscoveryCategoryDto
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Mapper for converting Category DTOs to Domain Models and Entities
 */
@Singleton
class CategoriesDtoMapper @Inject constructor() {

    // ==================== DISCOVERY CATEGORY MAPPINGS ====================

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
     * Convert DiscoveryCategoryDto to DiscoveryCategoryEntity for Room caching
     * @param dto The DTO to convert
     * @param cacheKey The cache key for this query
     */
    fun toDiscoveryEntity(dto: DiscoveryCategoryDto, cacheKey: String): DiscoveryCategoryEntity {
        return DiscoveryCategoryEntity(
            id = dto.id,
            name = dto.name,
            slug = dto.slug,
            vertical = dto.vertical,
            type = dto.type,
            hasSubcategories = dto.hasSubcategories,
            lastUpdated = System.currentTimeMillis(),
            cacheKey = cacheKey
        )
    }

    /**
     * Convert list of DiscoveryCategoryDto to list of DiscoveryCategoryEntity
     */
    fun toDiscoveryEntityList(dtos: List<DiscoveryCategoryDto>, cacheKey: String): List<DiscoveryCategoryEntity> {
        return dtos.map { toDiscoveryEntity(it, cacheKey) }
    }

    /**
     * Convert DiscoveryCategoryEntity to DiscoveryCategory domain model
     */
    fun toDomainFromEntity(entity: DiscoveryCategoryEntity): DiscoveryCategory {
        return DiscoveryCategory(
            id = entity.id,
            name = entity.name,
            slug = entity.slug,
            vertical = entity.vertical,
            type = entity.type,
            hasSubcategories = entity.hasSubcategories
        )
    }

    /**
     * Convert list of DiscoveryCategoryEntity to list of DiscoveryCategory domain models
     */
    fun toDiscoveryDomainListFromEntities(entities: List<DiscoveryCategoryEntity>): List<DiscoveryCategory> {
        return entities.map { toDomainFromEntity(it) }
    }

    // ==================== FULL CATEGORY MAPPINGS ====================

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
            updatedAt = categoryDto.updatedAt,
            subcategories = categoryDto.subcategories?.map { toDomain(it) }
        )
    }

    /**
     * Convert list of CategoryDto to list of Category domain models
     */
    fun toCategoryDomainList(categoryDtos: List<CategoryDto>): List<Category> {
        return categoryDtos.map { toDomain(it) }
    }

    /**
     * Convert CategoryDto to CategoryEntity for Room caching
     * @param dto The DTO to convert
     * @param cacheKey The cache key for this query
     */
    fun toCategoryEntity(dto: CategoryDto, cacheKey: String): CategoryEntity {
        return CategoryEntity(
            id = dto.id,
            name = dto.name,
            slug = dto.slug,
            vertical = dto.vertical,
            type = dto.type,
            parentId = dto.parentId,
            hasSubcategories = dto.hasSubcategories,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            cacheKey = cacheKey
        )
    }


    /**
     * Convert list of CategoryDto to list of CategoryEntity (flattening nested subcategories)
     * This recursively flattens the category hierarchy into a flat list for Room storage
     */
    fun toCategoryEntityList(dtos: List<CategoryDto>, cacheKey: String): List<CategoryEntity> {
        val entities = mutableListOf<CategoryEntity>()
        var totalNestedCount = 0

        fun flatten(category: CategoryDto) {
            entities.add(toCategoryEntity(category, cacheKey))
            val nestedCount = category.subcategories?.size ?: 0
            if (nestedCount > 0) {
                println("📋 Category '${category.name}' has $nestedCount subcategories")
                totalNestedCount += nestedCount
            }
            category.subcategories?.forEach { subcategory ->
                flatten(subcategory)
            }
        }

        dtos.forEach { category ->
            flatten(category)
        }

        println("📋 Total entities created: ${entities.size}")
        println("📋 Total nested subcategories found: $totalNestedCount")

        return entities
    }


    /**
     * Convert single CategoryEntity to Category domain model (without children)
     * For single category lookups where hierarchy is not needed
     */
    fun toCategoryDomainFromEntity(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            slug = entity.slug,
            vertical = entity.vertical,
            type = entity.type,
            hasSubcategories = entity.hasSubcategories,
            description = null,
            parentId = entity.parentId,
            subcategoriesCount = 0,
            jobPostsCount = 0,
            servicesCount = 0,
            supportCount = 0,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            subcategories = null
        )
    }

    /**
     * Convert list of CategoryEntity to list of Category domain models with proper hierarchy
     * This reconstructs the parent-child relationships from the flat list
     */
    fun toCategoryDomainListFromEntities(entities: List<CategoryEntity>): List<Category> {
        if (entities.isEmpty()) return emptyList()

        println("🔧 Building category hierarchy from ${entities.size} entities")

        val rootEntities = entities.filter { it.parentId == null }
        println("🔧 Root entities found: ${rootEntities.size}")

        fun buildCategory(entity: CategoryEntity): Category {
            val children = entities.filter { it.parentId == entity.id }
            if (children.isNotEmpty()) {
                println("🔧 Category '${entity.name}' has ${children.size} children")
            }
            return Category(
                id = entity.id,
                name = entity.name,
                slug = entity.slug,
                vertical = entity.vertical,
                type = entity.type,
                hasSubcategories = entity.hasSubcategories,
                description = null,
                parentId = entity.parentId,
                subcategoriesCount = children.size,
                jobPostsCount = 0,
                servicesCount = 0,
                supportCount = 0,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                subcategories = children.map { buildCategory(it) }
            )
        }

        val result = rootEntities.map { buildCategory(it) }
        println("🔧 Built ${result.size} root categories with their subcategories")

        // Print sample of Architects
        result.find { it.name == "Architects" }?.let { arch ->
            println("🔧 Architects has ${arch.subcategoriesCount} subcategories: ${arch.subcategories?.map { it.name }}")
        }

        return result
    }

    // ==================== SINGLE CATEGORY MAPPINGS ====================

    /**
     * Convert CategoryDto to CategoryEntity for single category caching
     */
    fun toCategoryEntityFromDetail(dto: CategoryDto): CategoryEntity {
        return CategoryEntity(
            id = dto.id,
            name = dto.name,
            slug = dto.slug,
            vertical = dto.vertical,
            type = dto.type,
            hasSubcategories = dto.hasSubcategories,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            cacheKey = "category_by_id_${dto.id}"
        )
    }
}