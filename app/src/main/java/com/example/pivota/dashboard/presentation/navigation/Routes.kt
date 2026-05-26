package com.example.pivota.dashboard.presentation.navigation

import kotlinx.serialization.Serializable

// Top-level dashboard routes
@Serializable object Dashboard
@Serializable object Professionals
@Serializable object Connect
@Serializable object Profile

// Specific Posting Flow routes (Type-Safe)
@Serializable object PostJob
@Serializable object PostHousing
@Serializable object PostSupport
@Serializable object PostService

@Serializable object MyListings

@Serializable object HouseListings

@Serializable object JobListings

@Serializable
data object BookViewing

@Serializable
data object HouseDetails

@Serializable
data object AdminHouseDetails

@Serializable
object JobDetails

@Serializable
object JobApplicationForm // Optional - if you want an application form screen

@Serializable
object AdminJobDetails

// All Services Screen - Browse all COMPLIMENTARY categories
@Serializable
object AllServices

// Subcategories Screen - Shows subcategories for a selected parent category
@Serializable
data class Subcategories(
    val parentCategoryId: String,
    val parentCategoryName: String,
    val vertical: String
)

// Service Offerings Screen - Shows professionals offering a specific service
@Serializable
data class ServiceOfferings(
    val categoryId: String,
    val categoryName: String
)

// Category-filtered listing routes
@Serializable
data class HousingListingsWithCategory(
    val categoryId: String,
    val categoryName: String
)

@Serializable
data class JobListingsWithCategory(
    val categoryId: String,
    val categoryName: String
)

@Serializable
data class SupportListingsWithCategory(
    val categoryId: String,
    val categoryName: String
)

// Add more routes as needed