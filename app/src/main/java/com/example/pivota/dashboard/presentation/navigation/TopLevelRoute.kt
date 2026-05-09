package com.example.pivota.dashboard.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector


// Update your TopLevelRoute data class to support selected icons
data class TopLevelRoute(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val contentDescription: String,
    val requiresAuth: Boolean = false
)
