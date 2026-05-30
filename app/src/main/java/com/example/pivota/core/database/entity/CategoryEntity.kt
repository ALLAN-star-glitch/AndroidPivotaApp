package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val slug: String,
    val vertical: String,
    val type: String,
    val hasSubcategories: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val cacheKey: String? = null  // Added: For tracking which query this category belongs to
)

@Entity(tableName = "discovery_categories")
data class DiscoveryCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val slug: String,
    val vertical: String,
    val type: String,
    val hasSubcategories: Boolean,
    val lastUpdated: Long = System.currentTimeMillis(),
    val cacheKey: String? = null  // Added: For tracking which query this discovery category belongs to
)

@Entity(tableName = "categories_cache_metadata")
data class CategoriesCacheMetadataEntity(
    @PrimaryKey
    val cacheKey: String,
    val lastUpdated: Long,
    val totalCount: Int,
    val etag: String? = null
)