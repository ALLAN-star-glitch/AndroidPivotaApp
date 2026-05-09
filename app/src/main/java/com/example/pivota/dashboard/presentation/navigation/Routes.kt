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
data object  HouseDetails

@Serializable
data object AdminHouseDetails

@Serializable
object JobDetails

@Serializable
object JobApplicationForm // Optional - if you want an application form screen

@Serializable
object AdminJobDetails

// Add more routes as needed