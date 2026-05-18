
package com.example.pivota.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ======================================================
// CATEGORIES DTOs (Match backend response)
// ======================================================

@Serializable
data class DiscoveryMetadataResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: Int,
    @SerialName("data") val data: List<DiscoveryCategoryDto>? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class DiscoveryCategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("slug") val slug: String,
    @SerialName("vertical") val vertical: String,
    @SerialName("type") val type: String,
    @SerialName("hasSubcategories") val hasSubcategories: Boolean
)

@Serializable
data class CategoriesResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: Int,
    @SerialName("data") val data: List<CategoryDto>? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("slug") val slug: String,
    @SerialName("vertical") val vertical: String,
    @SerialName("type") val type: String,
    @SerialName("description") val description: String? = null,
    @SerialName("parentId") val parentId: String? = null,
    @SerialName("hasSubcategories") val hasSubcategories: Boolean,
    @SerialName("hasParent") val hasParent: Boolean,
    @SerialName("subcategoriesCount") val subcategoriesCount: Int,
    @SerialName("jobPostsCount") val jobPostsCount: Int,
    @SerialName("servicesCount") val servicesCount: Int,
    @SerialName("supportCount") val supportCount: Int,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("subcategories") val subcategories: List<CategoryDto>? = null
)