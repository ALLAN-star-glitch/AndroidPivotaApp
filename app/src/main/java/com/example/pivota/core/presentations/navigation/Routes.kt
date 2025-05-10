package com.example.pivota.core.presentations.navigation

import kotlinx.serialization.Serializable

// Entry routes
@Serializable object Welcome
@Serializable object Register
@Serializable object Login

// Nested graph route
@Serializable object MainFlow

// Routes inside nested graph
@Serializable object Preference
@Serializable object Dashboard


