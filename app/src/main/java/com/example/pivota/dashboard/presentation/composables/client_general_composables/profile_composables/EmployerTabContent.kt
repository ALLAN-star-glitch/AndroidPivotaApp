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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
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
import com.example.pivota.dashboard.domain.model.profile_models.EmployerProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.SuccessGreen

@Composable
fun EmployerTabContent(
    employerProfile: EmployerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditDescription: () -> Unit,
    onEditPreferences: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Company Overview
        ProfileSection(
            title = "Company Overview",
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
                    text = employerProfile.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!employerProfile.industry.isNullOrBlank()) {
                        Column {
                            Text(
                                "Industry",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                employerProfile.industry!!,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    if (!employerProfile.companySize.isNullOrBlank()) {
                        Column {
                            Text(
                                "Size",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                employerProfile.companySize!!,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    if (employerProfile.foundedYear != null) {
                        Column {
                            Text(
                                "Founded",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${employerProfile.foundedYear}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (employerProfile.isVerified) {
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
                                "Verified Employer",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Description
        if (!employerProfile.description.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditDescription) {
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
                        text = employerProfile.description!!,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Hiring Preferences
        if (employerProfile.preferredSkills.isNotEmpty()) {
            ProfileSection(
                title = "Preferred Skills",
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
                        items(employerProfile.preferredSkills) { skill ->
                            AssistChip(
                                onClick = {},
                                label = { Text(skill) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Remote Policy
        if (!employerProfile.remotePolicy.isNullOrBlank()) {
            ProfileSection(
                title = "Work Policy",
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
                    icon = Icons.Default.Work,
                    label = "Remote Policy",
                    subtitle = employerProfile.remotePolicy!!,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}