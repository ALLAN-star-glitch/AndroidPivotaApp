package com.example.pivota.dashboard.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pivota.R

enum class DashboardDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    EXPLORE(R.string.explore, Icons.Filled.Search, R.string.explore),
    POST(R.string.post, Icons.Filled.Add, R.string.post),
    MANAGE(R.string.manage, Icons.Filled.Build, R.string.manage),
    SETTINGS(R.string.settings, Icons.Filled.Settings, R.string.settings),
}
