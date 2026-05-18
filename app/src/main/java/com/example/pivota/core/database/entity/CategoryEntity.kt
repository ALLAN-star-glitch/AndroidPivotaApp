

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
    val updatedAt: String? = null
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
    val lastUpdated: Long = System.currentTimeMillis()
)