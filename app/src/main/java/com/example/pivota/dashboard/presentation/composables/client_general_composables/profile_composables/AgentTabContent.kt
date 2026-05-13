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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Percent
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
import com.example.pivota.dashboard.domain.model.profile_models.AgentProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.SuccessGreen

@Composable
fun AgentTabContent(
    agentProfile: AgentProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditSpecializations: () -> Unit,
    onEditLicense: () -> Unit,
    onEditServiceAreas: () -> Unit,
    onEditCommission: () -> Unit,
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
            title = "Agent Overview",
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
                    text = agentProfile.agentType.displayName,
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
                            "${agentProfile.yearsExperience ?: 0}+ years",
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
                                String.format("%.1f", agentProfile.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                " (${agentProfile.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column {
                        Text(
                            "Deals",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${agentProfile.completedDeals}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                }

                if (agentProfile.isVerified) {
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
                                "Verified Agent",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Specializations
        if (agentProfile.specializations.isNotEmpty()) {
            ProfileSection(
                title = "Specializations",
                action = {
                    IconButton(onClick = onEditSpecializations) {
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
                        items(agentProfile.specializations) { spec ->
                            AssistChip(
                                onClick = {},
                                label = { Text(spec) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // License & Credentials
        if (!agentProfile.licenseNumber.isNullOrBlank()) {
            ProfileSection(
                title = "License & Credentials",
                action = {
                    IconButton(onClick = onEditLicense) {
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
                    icon = Icons.Default.Badge,
                    label = "License Number",
                    subtitle = agentProfile.licenseNumber!!,
                    onClick = {},
                    iconColor = primaryColor
                )
                if (!agentProfile.licenseBody.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Business,
                        label = "Issuing Body",
                        subtitle = agentProfile.licenseBody!!,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!agentProfile.agencyName.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Apartment,
                        label = "Agency",
                        subtitle = agentProfile.agencyName!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Service Areas
        if (agentProfile.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditServiceAreas) {
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
                        items(agentProfile.serviceAreas) { area ->
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

        // Commission & Fees
        ProfileSection(
            title = "Commission & Fees",
            action = {
                IconButton(onClick = onEditCommission) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (agentProfile.commissionRate != null) {
                ProfileItem(
                    icon = Icons.Default.Percent,
                    label = "Commission Rate",
                    subtitle = "${agentProfile.commissionRate}%",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfile.feeStructure.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Fee Structure",
                    subtitle = agentProfile.feeStructure!!,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (agentProfile.minimumFee != null) {
                ProfileItem(
                    icon = Icons.Default.Payment,
                    label = "Minimum Fee",
                    subtitle = "KES ${agentProfile.minimumFee}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
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
            if (!agentProfile.contactEmail.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    subtitle = agentProfile.contactEmail!!,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfile.contactPhone.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    subtitle = agentProfile.contactPhone!!,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfile.website.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Language,
                    label = "Website",
                    subtitle = agentProfile.website!!,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // About
        if (!agentProfile.about.isNullOrBlank()) {
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
                        text = agentProfile.about!!,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}