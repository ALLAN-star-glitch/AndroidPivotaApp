package com.example.pivota.dashboard.presentation.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Define your new routes
val topLevelRoutes = listOf(
    TopLevelRoute(
        route = Home,
        label = "Home",
        icon = Icons.Default.Home,
        contentDescription = "Home Feed",
        requiresAuth = false
    ),
    TopLevelRoute(
        route = Explore,
        label = "Explore",
        icon = Icons.Default.Search,
        contentDescription = "Explore Opportunities",
        requiresAuth = false
    ),
    TopLevelRoute(
        route = Post,
        label = "Post",
        icon = Icons.Default.AddCircle,
        contentDescription = "Post a Listing",
        requiresAuth = true
    ),
    TopLevelRoute(
        route = Providers,
        label = "Providers",
        icon = Icons.Default.Person,
        contentDescription = "Find Providers",
        requiresAuth = true
    ),
    TopLevelRoute(
        route = Profile,
        label = "Profile",
        icon = Icons.Default.AccountCircle,
        contentDescription = "User Profile",
        requiresAuth = true
    )
)
