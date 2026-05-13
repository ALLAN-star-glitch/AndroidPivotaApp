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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
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
import com.example.pivota.dashboard.domain.model.profile_models.ProfessionalProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.SuccessGreen

@Composable
fun ServiceProviderTabContent(
    serviceProviderProfile: ProfessionalProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditServices: () -> Unit,
    onEditBeneficiaries: () -> Unit,
    onEditAreas: () -> Unit,
    onEditContact: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Service Provider Overview",
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
                Text(
                    text = serviceProviderProfile.displayTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Experience",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${serviceProviderProfile.yearsExperience ?: 0}+ years",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            "Rating",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = goldenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                String.format("%.1f", serviceProviderProfile.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                " (${serviceProviderProfile.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (serviceProviderProfile.isVerified) {
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
                                "Verified Provider",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Services Offered
        if (serviceProviderProfile.specialties.isNotEmpty()) {
            ProfileSection(
                title = "Services Offered",
                action = {
                    IconButton(onClick = onEditServices) {
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
                        items(serviceProviderProfile.specialties) { service ->
                            AssistChip(
                                onClick = {},
                                label = { Text(service) },
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
        if (serviceProviderProfile.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditAreas) {
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
                        items(serviceProviderProfile.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Business Details
        ProfileSection(
            title = "Business Details",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!serviceProviderProfile.licenseNumber.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "License Number",
                    subtitle = serviceProviderProfile.licenseNumber!!,
                    onClick = {},
                    iconColor = if (serviceProviderProfile.isVerified) SuccessGreen else primaryColor
                )
            }
            if (serviceProviderProfile.hourlyRate != null) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Hourly Rate",
                    subtitle = "KES ${serviceProviderProfile.hourlyRate}",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
        }

        // About
        if (!serviceProviderProfile.title.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditContact) {
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
                    Text(
                        text = serviceProviderProfile.title!!,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Contact Info
        ProfileSection(
            title = "Contact Information",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            // These fields don't exist in ProfessionalProfile - they would come from ServiceProviderProfile
            // For now, they are commented out or shown as "Not provided"
            ProfileItem(
                icon = Icons.Default.Email,
                label = "Email",
                subtitle = "Not provided",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Phone,
                label = "Phone",
                subtitle = "Not provided",
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Support Needs
        ProfileSection(
            title = "Support Needs",
            action = {
                IconButton(onClick = onEditContact) {
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
                icon = Icons.Default.Favorite,
                label = "Accepts Donations",
                subtitle = "Not specified",
                onClick = {},
                iconColor = Color(0xFFE91E63)
            )
            ProfileItem(
                icon = Icons.Default.People,
                label = "Needs Volunteers",
                subtitle = "Not specified",
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }
    }
}