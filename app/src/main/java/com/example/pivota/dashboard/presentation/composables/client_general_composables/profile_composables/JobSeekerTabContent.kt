import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.rounded.Download
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
import com.example.pivota.dashboard.domain.model.profile_models.JobSeekerProfile
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileItem
import com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens.ProfileSection
import com.example.pivota.ui.theme.SuccessGreen

@Composable
fun JobSeekerTabContent(
    jobSeekerProfileData: JobSeekerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditBranding: () -> Unit,
    onEditSkills: () -> Unit,
    onEditIndustries: () -> Unit,
    onEditPreferences: () -> Unit,
    onEditWorkAuth: () -> Unit,
    onEditCV: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Professional Branding
        ProfileSection(
            title = "Professional Profile",
            action = {
                IconButton(onClick = onEditBranding) {
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
                if (!jobSeekerProfileData.headline.isNullOrBlank()) {
                    Text(
                        text = jobSeekerProfileData.headline,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (jobSeekerProfileData.isActivelySeeking)
                            SuccessGreen.copy(alpha = 0.1f)
                        else
                            colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Accessibility,
                                contentDescription = null,
                                tint = if (jobSeekerProfileData.isActivelySeeking) SuccessGreen else colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (jobSeekerProfileData.isActivelySeeking) "Actively Seeking" else "Open to Opportunities",
                                fontSize = 12.sp,
                                color = if (jobSeekerProfileData.isActivelySeeking) SuccessGreen else colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Skills
        if (jobSeekerProfileData.skills.isNotEmpty()) {
            ProfileSection(
                title = "Skills",
                action = {
                    IconButton(onClick = onEditSkills) {
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
                        items(jobSeekerProfileData.skills) { skill ->
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

        // Industries
        if (jobSeekerProfileData.industries.isNotEmpty()) {
            ProfileSection(
                title = "Industries",
                action = {
                    IconButton(onClick = onEditIndustries) {
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
                        items(jobSeekerProfileData.industries) { industry ->
                            AssistChip(
                                onClick = {},
                                label = { Text(industry) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Job Preferences
        ProfileSection(
            title = "Job Preferences",
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
            if (jobSeekerProfileData.jobTypes.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Work,
                    label = "Job Types",
                    subtitle = jobSeekerProfileData.jobTypes.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.seniorityLevel.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.TrendingUp,
                    label = "Seniority Level",
                    subtitle = jobSeekerProfileData.seniorityLevel,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.noticePeriod.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Schedule,
                    label = "Notice Period",
                    subtitle = jobSeekerProfileData.noticePeriod,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (jobSeekerProfileData.expectedSalary != null) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Expected Salary",
                    subtitle = "KES ${jobSeekerProfileData.expectedSalary}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Work Authorization
        if (jobSeekerProfileData.workAuthorization.isNotEmpty()) {
            ProfileSection(
                title = "Work Authorization",
                action = {
                    IconButton(onClick = onEditWorkAuth) {
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
                        items(jobSeekerProfileData.workAuthorization) { auth ->
                            AssistChip(
                                onClick = {},
                                label = { Text(auth) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // CV & Links
        ProfileSection(
            title = "CV & Professional Links",
            action = {
                IconButton(onClick = onEditCV) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!jobSeekerProfileData.cvUrl.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "CV/Resume",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        if (jobSeekerProfileData.cvLastUpdated != null) {
                            Text(
                                "Updated ${jobSeekerProfileData.cvLastUpdated}",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Rounded.Download,
                        contentDescription = null,
                        tint = primaryColor
                    )
                }
            }

            if (!jobSeekerProfileData.linkedInUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Info,
                    label = "LinkedIn",
                    subtitle = "View Profile",
                    onClick = {},
                    iconColor = Color(0xFF0077B5)
                )
            }
            if (!jobSeekerProfileData.portfolioUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Link,
                    label = "Portfolio",
                    subtitle = "View Website",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.githubUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Code,
                    label = "GitHub",
                    subtitle = "View Profile",
                    onClick = {},
                    iconColor = Color.Black,
                    showDivider = false
                )
            }
        }
    }
}