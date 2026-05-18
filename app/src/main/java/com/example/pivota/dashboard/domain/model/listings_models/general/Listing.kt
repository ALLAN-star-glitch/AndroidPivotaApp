package com.example.pivota.dashboard.domain.model.listings_models.general

import com.example.pivota.dashboard.domain.ListingFilter
import com.example.pivota.dashboard.domain.ListingType
import kotlinx.serialization.Serializable

@Serializable
data class Listing(
    val id: String,
    val title: String,
    val type: ListingType,      // Jobs / Housing / Services
    val status: ListingFilter,  // Active / Pending / Closed
    val description: String
)


@Serializable
data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val vertical: String,      // HOUSING, JOBS, SOCIAL_SUPPORT
    val type: String,           // MAIN or COMPLIMENTARY
    val hasSubcategories: Boolean,
    val description: String? = null,
    val parentId: String? = null,
    val subcategoriesCount: Int = 0,
    val jobPostsCount: Int = 0,
    val servicesCount: Int = 0,
    val supportCount: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// For discovery metadata (lightweight version)
@Serializable
data class DiscoveryCategory(
    val id: String,
    val name: String,
    val slug: String,
    val vertical: String,
    val type: String,
    val hasSubcategories: Boolean
)