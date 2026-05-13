package com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.dashboard.domain.model.profile_models.BeneficiaryProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.errorLight

@Composable
fun BeneficiaryTabContent(
    beneficiaryProfile: BeneficiaryProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditNeeds: () -> Unit,
    onEditHousehold: () -> Unit,
    onEditLocation: () -> Unit,
    onEditPrivacy: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Needs
        ProfileSection(
            title = "Needs",
            action = {
                IconButton(onClick = onEditNeeds) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (beneficiaryProfile.needs.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfile.needs) { need ->
                            AssistChip(
                                onClick = {},
                                label = { Text(need) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }

            if (beneficiaryProfile.urgentNeeds.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    Text(
                        "Urgent Needs",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = errorLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfile.urgentNeeds) { need ->
                            AssistChip(
                                onClick = {},
                                label = { Text(need) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = errorLight.copy(alpha = 0.1f),
                                    labelColor = errorLight
                                )
                            )
                        }
                    }
                }
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
            if (beneficiaryProfile.familySize != null) {
                ProfileItem(
                    icon = Icons.Default.People,
                    label = "Family Size",
                    subtitle = "${beneficiaryProfile.familySize}",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (beneficiaryProfile.dependents != null) {
                ProfileItem(
                    icon = Icons.Default.ChildCare,
                    label = "Dependents",
                    subtitle = "${beneficiaryProfile.dependents}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
            if (!beneficiaryProfile.householdComposition.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.FamilyRestroom,
                    label = "Household Composition",
                    subtitle = beneficiaryProfile.householdComposition!!,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Vulnerability Factors
        if (beneficiaryProfile.vulnerabilityFactors.isNotEmpty()) {
            ProfileSection(
                title = "Vulnerability Factors",
                action = {
                    IconButton(onClick = onEditNeeds) {
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
                        items(beneficiaryProfile.vulnerabilityFactors) { factor ->
                            AssistChip(
                                onClick = {},
                                label = { Text(factor) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = errorLight.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Location
        if (!beneficiaryProfile.city.isNullOrBlank() || !beneficiaryProfile.neighborhood.isNullOrBlank()) {
            ProfileSection(
                title = "Location",
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
                if (!beneficiaryProfile.city.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.LocationCity,
                        label = "City",
                        subtitle = beneficiaryProfile.city!!,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!beneficiaryProfile.neighborhood.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.LocationOn,
                        label = "Neighborhood",
                        subtitle = beneficiaryProfile.neighborhood!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
                if (!beneficiaryProfile.landmark.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Place,
                        label = "Landmark",
                        subtitle = beneficiaryProfile.landmark!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Privacy Settings
        ProfileSection(
            title = "Privacy Settings",
            action = {
                IconButton(onClick = onEditPrivacy) {
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
                icon = Icons.Default.PrivacyTip,
                label = "Anonymous Profile",
                subtitle = if (beneficiaryProfile.prefersAnonymity) "Yes" else "No",
                onClick = {},
                iconColor = primaryColor
            )
            if (beneficiaryProfile.languagePreferences.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Language,
                    label = "Language Preference",
                    subtitle = beneficiaryProfile.languagePreferences.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}