package com.example.pivota.dashboard.presentation.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings

val topLevelRoutes = listOf(
    TopLevelRoute(
        "Explore", Explore, Icons.Filled.Search,
        contentDescription = "Explore"
    ),
    TopLevelRoute(
        "Post", Post, Icons.Filled.Add,
        contentDescription = "Explore"
    ),
    TopLevelRoute(
        "Manage", Manage, Icons.Filled.Build,
        contentDescription = "Explore"
    ),
    TopLevelRoute(
        "Settings", Settings, Icons.Filled.Settings,
        contentDescription = "Explore"
    )
)