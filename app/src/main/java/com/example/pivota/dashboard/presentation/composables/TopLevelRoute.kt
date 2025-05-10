package com.example.pivota.dashboard.presentation.composables

import androidx.compose.ui.graphics.vector.ImageVector


data class TopLevelRoute<T : Any>(val label: String, val route: T, val icon: ImageVector, val contentDescription: String)