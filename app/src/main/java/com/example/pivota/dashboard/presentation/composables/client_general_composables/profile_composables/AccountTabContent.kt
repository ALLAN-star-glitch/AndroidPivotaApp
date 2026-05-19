package com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.dashboard.domain.model.profile_models.AccountStatus
import com.example.pivota.dashboard.domain.model.profile_models.CompletionLevel
import com.example.pivota.dashboard.domain.model.profile_models.IndividualProfile
import com.example.pivota.dashboard.domain.model.profile_models.OrganizationProfile
import com.example.pivota.dashboard.domain.model.profile_models.ProfileAccount
import com.example.pivota.dashboard.domain.model.profile_models.ProfileCompletion
import com.example.pivota.dashboard.domain.model.profile_models.ProfileUser
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.errorLight

@Composable
fun AccountTabContent(
    account: ProfileAccount,
    user: ProfileUser,
    individualProfile: IndividualProfile?,
    organizationProfile: OrganizationProfile?,
    completion: ProfileCompletion,
    primaryColor: Color,
    goldenAccent: Color,
    purpleAccent: Color,
    warningColor: Color,
    successColor: Color,
    colorScheme: ColorScheme,
    onNavigateToSettings: () -> Unit,
    onNavigateToTeamManagement: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToBillingHistory: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onSignOut: () -> Unit,
    onEditPersonalInfo: () -> Unit,
    onEditVerification: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Account Overview
        ProfileSection(
            title = "Account Overview",
            action = {
                IconButton(onClick = onEditPersonalInfo) {
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
                icon = Icons.Default.AccountCircle,
                label = "Account Code",
                subtitle = account.code,
                onClick = {},
                iconColor = primaryColor,
                showDivider = true
            )
            ProfileItem(
                icon = Icons.Default.Category,
                label = "Account Type",
                subtitle = account.type.name,
                onClick = {},
                iconColor = primaryColor,
                showDivider = true
            )
            ProfileItem(
                icon = Icons.Default.Info,
                label = "Status",
                subtitle = account.status.name,
                onClick = {},
                iconColor = when (account.status) {
                    AccountStatus.ACTIVE -> successColor
                    AccountStatus.PENDING_PAYMENT -> warningColor
                    else -> errorLight
                },
                showDivider = false
            )
            ProfileItem(
                icon = Icons.Default.CalendarToday,
                label = "Member Since",
                subtitle = account.createdAt.take(10),
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Profile Completion
        ProfileSection(
            title = "Profile Completion",
            action = {
                IconButton(onClick = onEditPersonalInfo) {
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
                LinearProgressIndicator(
                    progress = completion.overallCompletion / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = primaryColor,
                    trackColor = colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${completion.overallCompletion}% Complete",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    text = when (completion.completionLevel) {
                        CompletionLevel.INCOMPLETE -> "Getting started"
                        CompletionLevel.BASIC -> "Basic profile"
                        CompletionLevel.PARTIAL -> "Almost there"
                        CompletionLevel.COMPLETE -> "Complete profile"
                    },
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Personal Information
        ProfileSection(
            title = "Personal Information",
            action = {
                IconButton(onClick = onEditPersonalInfo) {
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
                icon = Icons.Default.Person,
                label = "Full Name",
                subtitle = user.displayName,
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Email,
                label = "Email",
                subtitle = user.email,
                onClick = {},
                iconColor = primaryColor
            )
            user.phoneNumber?.let {
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            individualProfile?.dateOfBirth?.let {
                ProfileItem(
                    icon = Icons.Default.Cake,
                    label = "Date of Birth",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            individualProfile?.gender?.let {
                ProfileItem(
                    icon = Icons.Default.Wc,
                    label = "Gender",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            individualProfile?.nationalId?.let {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "National ID",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        if (!individualProfile?.bio.isNullOrBlank()) {
            ProfileSection(
                title = "About Me",
                action = {
                    IconButton(onClick = onEditPersonalInfo) {
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
                        text = individualProfile?.bio ?: "",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Organization Profile Section (if user is part of an organization)
        organizationProfile?.let { orgData ->
            ProfileSection(
                title = "Organization Information",
                action = {
                    IconButton(onClick = onEditPersonalInfo) {
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
                    icon = Icons.Default.Business,
                    label = "Organization Name",
                    subtitle = orgData.name,
                    onClick = {},
                    iconColor = primaryColor
                )
                orgData.officialEmail?.let {
                    ProfileItem(
                        icon = Icons.Default.Email,
                        label = "Official Email",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.officialPhone?.let {
                    ProfileItem(
                        icon = Icons.Default.Phone,
                        label = "Official Phone",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.registrationNumber?.let {
                    ProfileItem(
                        icon = Icons.Default.Description,
                        label = "Registration No",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.kraPin?.let {
                    ProfileItem(
                        icon = Icons.Default.Receipt,
                        label = "KRA PIN",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.type?.let {
                    ProfileItem(
                        icon = Icons.Default.Category,
                        label = "Organization Type",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.website?.let {
                    ProfileItem(
                        icon = Icons.Default.Language,
                        label = "Website",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.physicalAddress?.let {
                    ProfileItem(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }

            if (!orgData.about.isNullOrBlank()) {
                ProfileSection(
                    title = "About",
                    action = {
                        IconButton(onClick = onEditPersonalInfo) {
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
                            text = orgData.about ?: "",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }

            // Team Snapshot (count only)
            if (orgData.members.isNotEmpty() || orgData.pendingInvitations.isNotEmpty()) {
                ProfileSection(
                    title = "Team",
                    action = {
                        Row {
                            IconButton(onClick = { /* Edit team */ }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            TextButton(onClick = onNavigateToTeamManagement) {
                                Text("Manage", color = primaryColor)
                            }
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        if (orgData.members.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(primaryColor.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Group,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Team Members",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurface
                                    )
                                    Text(
                                        "${orgData.members.size} members",
                                        fontSize = 13.sp,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = primaryColor.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        "View",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        color = primaryColor
                                    )
                                }
                            }
                        }

                        if (orgData.pendingInvitations.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(warningColor.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = warningColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Pending Invitations",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurface
                                    )
                                    Text(
                                        "${orgData.pendingInvitations.size} awaiting response",
                                        fontSize = 13.sp,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Preferences & Settings
        ProfileSection(
            title = "Preferences & Settings",
            action = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                subtitle = "Email, Push, SMS",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Language,
                label = "Language",
                subtitle = "English",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Palette,
                label = "Theme",
                subtitle = "System default",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Lock,
                label = "Privacy & Security",
                subtitle = "2FA enabled",
                onClick = onNavigateToSettings,
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Subscription & Billing
        ProfileSection(
            title = "Subscription & Billing",
            action = {
                IconButton(onClick = onNavigateToSubscription) {
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
                icon = Icons.Default.Payment,
                label = "Subscription Plan",
                subtitle = "Pro Plan • 1,500 KES/month",
                onClick = onNavigateToSubscription,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.CreditCard,
                label = "Payment Methods",
                subtitle = "M-Pesa • 2 cards",
                onClick = onNavigateToPaymentMethods,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.History,
                label = "Billing History",
                subtitle = "View past invoices",
                onClick = onNavigateToBillingHistory,
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Support & Info
        ProfileSection(title = "Support") {
            ProfileItem(
                icon = Icons.Default.HelpOutline,
                label = "Help Center",
                subtitle = "FAQs, Guides, Tutorials",
                onClick = onNavigateToHelpCenter,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Info,
                label = "About PivotaConnect",
                subtitle = "Version 1.0.0 • MVP1",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Description,
                label = "Terms of Service",
                subtitle = "Read our terms",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.PrivacyTip,
                label = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Sign Out
        ProfileSection(title = "Account") {
            ProfileItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Sign Out",
                subtitle = "",
                onClick = onSignOut,
                iconColor = errorLight,
                textColor = errorLight,
                showDivider = false
            )
        }
    }
}