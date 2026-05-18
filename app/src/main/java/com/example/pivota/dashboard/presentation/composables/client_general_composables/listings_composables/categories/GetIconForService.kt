package com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Plumbing
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SolarPower
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Map service name to appropriate icon
 */
@Composable
fun getIconForService(name: String, vertical: String): ImageVector {
    return when {
        // HOUSING services
        name.contains("Plumber", ignoreCase = true) -> Icons.Outlined.Plumbing
        name.contains("Electrician", ignoreCase = true) -> Icons.Outlined.Bolt
        name.contains("Clean", ignoreCase = true) -> Icons.Outlined.CleaningServices
        name.contains("Move", ignoreCase = true) -> Icons.Outlined.LocalShipping
        name.contains("Paint", ignoreCase = true) -> Icons.Outlined.FormatPaint
        name.contains("Security", ignoreCase = true) -> Icons.Outlined.Security
        name.contains("Repair", ignoreCase = true) -> Icons.Outlined.Build
        name.contains("AC", ignoreCase = true) -> Icons.Outlined.AcUnit
        name.contains("Solar", ignoreCase = true) -> Icons.Outlined.SolarPower

        // JOBS services
        name.contains("CV", ignoreCase = true) || name.contains("Resume", ignoreCase = true) -> Icons.Outlined.Description
        name.contains("Career", ignoreCase = true) -> Icons.Outlined.TrendingUp
        name.contains("Interview", ignoreCase = true) -> Icons.Outlined.RecordVoiceOver
        name.contains("Training", ignoreCase = true) -> Icons.Outlined.School
        name.contains("Recruitment", ignoreCase = true) -> Icons.Outlined.People

        // SOCIAL_SUPPORT services
        name.contains("Counsel", ignoreCase = true) -> Icons.Outlined.Psychology
        name.contains("Legal", ignoreCase = true) -> Icons.Outlined.Gavel
        name.contains("Health", ignoreCase = true) -> Icons.Outlined.HealthAndSafety
        name.contains("Therapy", ignoreCase = true) -> Icons.Outlined.Favorite
        name.contains("Nutrition", ignoreCase = true) -> Icons.Outlined.Restaurant
        name.contains("Physio", ignoreCase = true) -> Icons.Outlined.FitnessCenter

        // Default by vertical
        vertical == "HOUSING" -> Icons.Outlined.Home
        vertical == "JOBS" -> Icons.Outlined.Work
        vertical == "SOCIAL_SUPPORT" -> Icons.Outlined.VolunteerActivism
        else -> Icons.Outlined.Build
    }
}