package com.example.pivota.dashboard.presentation.composables

import kotlinx.serialization.Serializable

// Top-level dashboard routes
@Serializable object Dashboard
@Serializable object Providers
@Serializable object Discover
@Serializable object SmartMatch
@Serializable object Profile

// Specific Posting Flow routes (Type-Safe)
@Serializable object PostJob
@Serializable object PostHousing
@Serializable object PostSupport
@Serializable object PostService