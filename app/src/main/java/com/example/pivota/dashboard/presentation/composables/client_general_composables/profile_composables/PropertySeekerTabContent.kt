package com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pivota.dashboard.domain.model.profile_models.HousingSeekerProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection

@Composable
fun PropertySeekerTabContent(
    propertySeekerProfileData: HousingSeekerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditHousing: () -> Unit,
    onEditLocation: () -> Unit,
    onEditMoveIn: () -> Unit,
    onEditHousehold: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Housing Preferences
        ProfileSection(
            title = "Housing Preferences",
            action = {
                IconButton(onClick = onEditHousing) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Bed,
                label = "Bedrooms",
                subtitle = "${propertySeekerProfileData.minBedrooms} - ${propertySeekerProfileData.maxBedrooms}",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.AttachMoney,
                label = "Budget",
                subtitle = buildString {
                    if (propertySeekerProfileData.minBudget != null) {
                        append("KES ${propertySeekerProfileData.minBudget}")
                    }
                    if (propertySeekerProfileData.maxBudget != null) {
                        if (propertySeekerProfileData.minBudget != null) append(" - ")
                        append("KES ${propertySeekerProfileData.maxBudget}")
                    }
                },
                onClick = {},
                iconColor = primaryColor
            )
            if (propertySeekerProfileData.preferredTypes.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Category,
                    label = "Property Types",
                    subtitle = propertySeekerProfileData.preferredTypes.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Location Preferences
        ProfileSection(
            title = "Location Preferences",
            action = {
                IconButton(onClick = onEditLocation) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (propertySeekerProfileData.preferredCities.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.LocationCity,
                    label = "Cities",
                    subtitle = propertySeekerProfileData.preferredCities.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (propertySeekerProfileData.preferredNeighborhoods.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.LocationOn,
                    label = "Neighborhoods",
                    subtitle = propertySeekerProfileData.preferredNeighborhoods.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Move-in Details
        ProfileSection(
            title = "Move-in Details",
            action = {
                IconButton(onClick = onEditMoveIn) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!propertySeekerProfileData.moveInDate.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Move-in Date",
                    subtitle = propertySeekerProfileData.moveInDate,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!propertySeekerProfileData.leaseDuration.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Schedule,
                    label = "Lease Duration",
                    subtitle = propertySeekerProfileData.leaseDuration,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Household
        ProfileSection(
            title = "Household",
            action = {
                IconButton(onClick = onEditHousehold) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.People,
                label = "Household Size",
                subtitle = "${propertySeekerProfileData.householdSize}",
                onClick = {},
                iconColor = primaryColor
            )
            if (propertySeekerProfileData.hasPets != null) {
                ProfileItem(
                    icon = Icons.Default.Pets,
                    label = "Pets",
                    subtitle = if (propertySeekerProfileData.hasPets == true)
                        propertySeekerProfileData.petDetails ?: "Yes"
                    else
                        "No",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}