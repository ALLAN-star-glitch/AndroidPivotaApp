package com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.dashboard.domain.model.profile_models.PropertyOwnerProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.SuccessGreen

@Composable
fun PropertyOwnerTabContent(
    propertyOwnerProfile: PropertyOwnerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditProfessionalDetails: () -> Unit,
    onEditPreferences: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Property Owner Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Type",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (propertyOwnerProfile.isProfessional) "Professional" else "Individual Owner",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    if (propertyOwnerProfile.yearsInBusiness != null) {
                        Column {
                            Text(
                                "Years in Business",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${propertyOwnerProfile.yearsInBusiness}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (propertyOwnerProfile.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Owner",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // License & Company
        if (propertyOwnerProfile.isProfessional) {
            ProfileSection(
                title = "Professional Details",
                action = {
                    IconButton(onClick = onEditProfessionalDetails) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                if (!propertyOwnerProfile.licenseNumber.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Badge,
                        label = "License Number",
                        subtitle = propertyOwnerProfile.licenseNumber!!,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!propertyOwnerProfile.companyName.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Business,
                        label = "Company",
                        subtitle = propertyOwnerProfile.companyName!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Property Preferences - using preferredPropertyTypes (List<PropertyType>)
        if (propertyOwnerProfile.preferredPropertyTypes.isNotEmpty()) {
            ProfileSection(
                title = "Property Types",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(propertyOwnerProfile.preferredPropertyTypes) { propertyType ->
                            AssistChip(
                                onClick = {},
                                label = { Text(propertyType.name) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Service Areas
        if (propertyOwnerProfile.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(propertyOwnerProfile.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Optional: Show listing type info
        if (propertyOwnerProfile.isListingForRent || propertyOwnerProfile.isListingForSale) {
            ProfileSection(
                title = "Listing Information",
                action = {
                    IconButton(onClick = onEditPreferences) {
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
                    icon = Icons.Default.Home,
                    label = "Listing Type",
                    subtitle = propertyOwnerProfile.listingTypeLabel,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = true
                )
                if (propertyOwnerProfile.propertyCount != null && propertyOwnerProfile.propertyCount > 0) {
                    ProfileItem(
                        icon = Icons.Default.Store,
                        label = "Properties",
                        subtitle = "${propertyOwnerProfile.propertyCount} properties",
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }
    }
}