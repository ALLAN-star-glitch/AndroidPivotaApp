package com.example.pivota.dashboard.presentation.composables

import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute(
    val route: Any,               // e.g., Explore, Post, Manage, Settings
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val requiresAuth: Boolean = false // mark guest-locked tabs
)
