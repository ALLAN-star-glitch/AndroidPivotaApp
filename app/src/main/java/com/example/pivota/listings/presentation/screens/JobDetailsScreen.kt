package com.example.pivota.listings.presentation.screens.jobs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.ui.theme.PivotaConnectTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Job Listing UI Model representing a job opportunity on PivotaConnect
 */
data class JobListingUiModel(
    val id: String,
    val title: String,
    val companyName: String,
    val companyLogoUrl: String? = null,
    val companyRating: Double = 0.0,
    val reviewCount: Int = 0,
    val location: String,
    val exactLocation: String? = null, // For map
    val jobType: JobType,
    val salaryMin: Int,
    val salaryMax: Int,
    val salaryPeriod: SalaryPeriod,
    val currency: String = "KES",
    val description: String,
    val responsibilities: List<String> = emptyList(),
    val requirements: List<String>,
    val preferredQualifications: List<String> = emptyList(),
    val benefits: List<Benefit> = emptyList(),
    val postedDate: Date,
    val applicationDeadline: Date? = null,
    val numberOfPositions: Int = 1,
    val employerName: String,
    val employerAvatarUrl: String? = null,
    val employerVerified: Boolean = true,
    val isSaved: Boolean = false,
    val isVerified: Boolean = true,
    val aiMatchScore: Int? = null, // AI SmartMatch™ score
    val commuteDistance: String? = null, // e.g., "15 min"
    val tags: List<String> = emptyList(),
    val applicationUrl: String? = null,
    val hasQuickApply: Boolean = false
)

enum class JobType {
    FULL_TIME,
    PART_TIME,
    FREELANCE,
    CONTRACT,
    INTERNSHIP,
    CASUAL,
    REMOTE,
    HYBRID;

    fun displayName(): String = when (this) {
        FULL_TIME -> "Full-time"
        PART_TIME -> "Part-time"
        FREELANCE -> "Freelance"
        CONTRACT -> "Contract"
        INTERNSHIP -> "Internship"
        CASUAL -> "Casual"
        REMOTE -> "Remote"
        HYBRID -> "Hybrid"
    }
}

enum class SalaryPeriod {
    PER_HOUR,
    PER_DAY,
    PER_WEEK,
    PER_MONTH,
    PER_YEAR,
    PROJECT_BASED;

    fun displayName(): String = when (this) {
        PER_HOUR -> "per hour"
        PER_DAY -> "per day"
        PER_WEEK -> "per week"
        PER_MONTH -> "per month"
        PER_YEAR -> "per year"
        PROJECT_BASED -> "per project"
    }
}

data class Benefit(
    val icon: BenefitIcon,
    val name: String,
    val description: String? = null
)

enum class BenefitIcon {
    TRANSPORT,
    MEALS,
    HEALTH,
    TRAINING,
    PARKING,
    BONUS,
    INSURANCE,
    OTHER;

    fun toImageVector(): ImageVector = when (this) {
        TRANSPORT -> Icons.Outlined.DirectionsCar
        MEALS -> Icons.Outlined.Fastfood
        HEALTH -> Icons.Outlined.HealthAndSafety
        TRAINING -> Icons.Outlined.School
        PARKING -> Icons.Outlined.DirectionsCar
        BONUS -> Icons.Outlined.AttachMoney
        INSURANCE -> Icons.Outlined.HealthAndSafety
        OTHER -> Icons.Outlined.Work
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JobDetailsScreen(
    jobListing: JobListingUiModel,
    onNavigateBack: () -> Unit,
    onApplyClick: (JobListingUiModel) -> Unit,
    onMessageEmployerClick: (JobListingUiModel) -> Unit,
    onSaveToggle: (String, Boolean) -> Unit,
    onBookmarkClick: (JobListingUiModel) -> Unit = {}
) {
    // Add null check at the beginning
    if (jobListing == null) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }

    // State for saved/bookmark
    var isSaved by rememberSaveable { mutableStateOf(jobListing.isSaved) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Format salary
    val formattedSalary = formatSalary(
        min = jobListing.salaryMin,
        max = jobListing.salaryMax,
        currency = jobListing.currency,
        period = jobListing.salaryPeriod
    )

    // Calculate days since posted
    val daysAgo = getDaysAgo(jobListing.postedDate)

    // Format deadline if exists
    val deadlineText = jobListing.applicationDeadline?.let {
        formatDeadline(it)
    }

    // Check if deadline is approaching (within 3 days)
    val isDeadlineApproaching = jobListing.applicationDeadline?.let {
        isDateApproaching(it, 3)
    } ?: false

    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = jobListing.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSaved = !isSaved
                        onSaveToggle(jobListing.id, isSaved)
                        onBookmarkClick(jobListing)
                    }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) "Saved" else "Save",
                            tint = if (isSaved)
                                MaterialTheme.colorScheme.tertiary // Baobab Gold for active bookmark
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onMessageEmployerClick(jobListing)
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubble,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Message")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onApplyClick(jobListing)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Apply Now",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isWide) {
            // TWO PANE LAYOUT (Tablet/Desktop)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Left Pane - Company & Key Details
                JobDetailsLeftPane(
                    jobListing = jobListing,
                    formattedSalary = formattedSalary,
                    daysAgo = daysAgo,
                    deadlineText = deadlineText,
                    isDeadlineApproaching = isDeadlineApproaching,
                    isSaved = isSaved,
                    onSaveToggle = { onSaveToggle(jobListing.id, !isSaved) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                // Right Pane - Scrollable Details
                JobDetailsRightPane(
                    jobListing = jobListing,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        } else {
            // SINGLE PANE LAYOUT (Mobile)
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Company Header Card
                JobCompanyHeader(
                    jobListing = jobListing,
                    formattedSalary = formattedSalary,
                    daysAgo = daysAgo,
                    deadlineText = deadlineText,
                    isDeadlineApproaching = isDeadlineApproaching,
                    isSaved = isSaved,
                    onSaveToggle = { onSaveToggle(jobListing.id, !isSaved) }
                )

                // AI SmartMatch™ (if available)
                jobListing.aiMatchScore?.let { score ->
                    SmartMatchBanner(score = score)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Job Description Card
                JobDescriptionCard(
                    description = jobListing.description,
                    responsibilities = jobListing.responsibilities,
                    requirements = jobListing.requirements,
                    preferredQualifications = jobListing.preferredQualifications
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Benefits Card (if available)
                if (jobListing.benefits.isNotEmpty()) {
                    BenefitsCard(benefits = jobListing.benefits)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Additional Info Card
                AdditionalInfoCard(
                    postedDate = jobListing.postedDate,
                    deadline = jobListing.applicationDeadline,
                    numberOfPositions = jobListing.numberOfPositions,
                    location = jobListing.location,
                    exactLocation = jobListing.exactLocation,
                    commuteDistance = jobListing.commuteDistance,
                    tags = jobListing.tags
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Employer Card
                EmployerCard(
                    employerName = jobListing.employerName,
                    employerAvatarUrl = jobListing.employerAvatarUrl,
                    isVerified = jobListing.employerVerified,
                    companyName = jobListing.companyName,
                    companyRating = jobListing.companyRating,
                    reviewCount = jobListing.reviewCount
                )

                // Bottom spacer for scroll
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/* ───────── MOBILE COMPONENTS (Single Pane) ───────── */

@Composable
fun JobCompanyHeader(
    jobListing: JobListingUiModel,
    formattedSalary: String,
    daysAgo: String,
    deadlineText: String?,
    isDeadlineApproaching: Boolean,
    isSaved: Boolean,
    onSaveToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Company Logo and Save Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Company Logo
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (jobListing.companyLogoUrl != null) {
                        AsyncImage(
                            model = jobListing.companyLogoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Save Icon
                IconButton(onClick = onSaveToggle) {
                    Icon(
                        imageVector = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = if (isSaved)
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Job Title
            Text(
                text = jobListing.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary // African Sapphire
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Company Name and Rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = jobListing.companyName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (jobListing.companyRating > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary, // Baobab Gold
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", jobListing.companyRating),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                        if (jobListing.reviewCount > 0) {
                            Text(
                                text = "(${jobListing.reviewCount})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary, // Warm Terracotta
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = jobListing.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
                if (jobListing.commuteDistance != null) {
                    Text(
                        text = " • ${jobListing.commuteDistance}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Job Type Badge and Salary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Job Type Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 0.dp
                ) {
                    Text(
                        text = jobListing.jobType.displayName(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                // Salary
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.AttachMoney,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formattedSalary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Posted and Deadline Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Posted
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Posted $daysAgo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Deadline
                if (deadlineText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = if (isDeadlineApproaching)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Apply by $deadlineText",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDeadlineApproaching)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isDeadlineApproaching) FontWeight.Medium else FontWeight.Normal,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // Verified Badge
            if (jobListing.isVerified) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Verified Employer",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SmartMatchBanner(score: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Star, // Replace with AI icon when available
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "✨ AI SmartMatch™: $score% match with your profile",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun JobDescriptionCard(
    description: String,
    responsibilities: List<String>,
    requirements: List<String>,
    preferredQualifications: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // About the Job
            Text(
                text = "About the Job",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Responsibilities (if available)
            if (responsibilities.isNotEmpty()) {
                Text(
                    text = "Responsibilities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                responsibilities.forEach { responsibility ->
                    BulletPoint(text = responsibility)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Requirements
            Text(
                text = "Requirements / Skills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            requirements.forEach { requirement ->
                BulletPoint(text = requirement)
            }

            // Preferred Qualifications (if available)
            if (preferredQualifications.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Preferred Qualifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                preferredQualifications.forEach { qualification ->
                    BulletPoint(text = qualification, icon = Icons.Outlined.Star)
                }
            }
        }
    }
}

@Composable
fun BulletPoint(
    text: String,
    icon: ImageVector = Icons.Outlined.CheckCircle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary, // Warm Terracotta
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BenefitsCard(benefits: List<Benefit>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Benefits & Perks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                benefits.forEach { benefit ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                benefit.icon.toImageVector(),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = benefit.name,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Optional detailed benefits with descriptions
            benefits.filter { it.description != null }.takeIf { it.isNotEmpty() }?.let { detailedBenefits ->
                Spacer(modifier = Modifier.height(12.dp))
                detailedBenefits.forEach { benefit ->
                    benefit.description?.let { desc ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                benefit.icon.toImageVector(),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = benefit.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdditionalInfoCard(
    postedDate: Date,
    deadline: Date?,
    numberOfPositions: Int,
    location: String,
    exactLocation: String?,
    commuteDistance: String?,
    tags: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Posted Date
            InfoRow(
                icon = Icons.Outlined.AccessTime,
                label = "Posted",
                value = formatDate(postedDate)
            )

            // Deadline
            deadline?.let {
                InfoRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Apply by",
                    value = formatDate(it),
                    valueColor = if (isDateApproaching(it, 3))
                        MaterialTheme.colorScheme.error
                    else
                        null
                )
            }

            // Number of Positions
            InfoRow(
                icon = Icons.Outlined.Group,
                label = "Openings",
                value = "$numberOfPositions ${if (numberOfPositions == 1) "position" else "positions"}"
            )

            // Location with map option
            InfoRow(
                icon = Icons.Outlined.LocationOn,
                label = "Location",
                value = location
            )

            if (exactLocation != null) {
                // Mini map placeholder - in real implementation, use Google Maps composable
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Map view coming soon",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tags
            if (tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EmployerCard(
    employerName: String,
    employerAvatarUrl: String?,
    isVerified: Boolean,
    companyName: String,
    companyRating: Double,
    reviewCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                if (employerAvatarUrl != null) {
                    AsyncImage(
                        model = employerAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Employer Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = employerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isVerified) {
                        Icon(
                            Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Verified Employer",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    if (companyRating > 0) {
                        if (isVerified) {
                            Text(
                                " • ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                String.format("%.1f", companyRating),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 1.dp)
                            )
                            if (reviewCount > 0) {
                                Text(
                                    "(${reviewCount})",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 1.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Message Icon
            IconButton(onClick = { /* Handle message */ }) {
                Icon(
                    Icons.Outlined.ChatBubble,
                    contentDescription = "Message",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/* ───────── TWO PANE COMPONENTS ───────── */

@Composable
private fun JobDetailsLeftPane(
    jobListing: JobListingUiModel,
    formattedSalary: String,
    daysAgo: String,
    deadlineText: String?,
    isDeadlineApproaching: Boolean,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Company Logo Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Company Logo
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (jobListing.companyLogoUrl != null) {
                        AsyncImage(
                            model = jobListing.companyLogoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Company Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = jobListing.companyName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        if (jobListing.companyRating > 0) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = String.format("%.1f", jobListing.companyRating),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            if (jobListing.reviewCount > 0) {
                                Text(
                                    text = "(${jobListing.reviewCount} reviews)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Save Button
                IconButton(onClick = onSaveToggle) {
                    Icon(
                        imageVector = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = if (isSaved)
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Job Title and Key Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = jobListing.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = jobListing.location,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    if (jobListing.commuteDistance != null) {
                        Text(
                            text = " • ${jobListing.commuteDistance}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Job Type
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = jobListing.jobType.displayName(),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Salary
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.AttachMoney,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = formattedSalary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // AI SmartMatch™ (if available)
        jobListing.aiMatchScore?.let { score ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Star, // Replace with AI icon
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI SmartMatch™",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$score% match with your profile",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Quick Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Outlined.AccessTime,
                    value = daysAgo,
                    label = "Posted"
                )
                StatItem(
                    icon = Icons.Outlined.Group,
                    value = jobListing.numberOfPositions.toString(),
                    label = if (jobListing.numberOfPositions == 1) "Opening" else "Openings"
                )
                if (deadlineText != null) {
                    StatItem(
                        icon = Icons.Outlined.CalendarMonth,
                        value = deadlineText,
                        label = "Deadline",
                        valueColor = if (isDeadlineApproaching)
                            MaterialTheme.colorScheme.error
                        else
                            null
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    valueColor: Color? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun JobDetailsRightPane(
    jobListing: JobListingUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Description Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About the Job",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = jobListing.description,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }
        }

        // Responsibilities Card (if available)
        if (jobListing.responsibilities.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Responsibilities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    jobListing.responsibilities.forEach { responsibility ->
                        BulletPoint(text = responsibility)
                    }
                }
            }
        }

        // Requirements Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Requirements / Skills",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                jobListing.requirements.forEach { requirement ->
                    BulletPoint(text = requirement)
                }
            }
        }

        // Benefits Card (if available)
        if (jobListing.benefits.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Benefits & Perks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        jobListing.benefits.forEach { benefit ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        benefit.icon.toImageVector(),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        benefit.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Location Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Location Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = jobListing.location,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (jobListing.exactLocation != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Exact location available upon application",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Employer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    if (jobListing.employerAvatarUrl != null) {
                        AsyncImage(
                            model = jobListing.employerAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = jobListing.employerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (jobListing.employerVerified) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Verified,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp),
                                contentDescription = null
                            )
                            Text(
                                "Verified Employer",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { /* Handle message */ }
                ) {
                    Icon(
                        Icons.Outlined.ChatBubble,
                        contentDescription = "Message",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Spacer at bottom
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/* ───────── UTILITY FUNCTIONS ───────── */

private fun formatSalary(
    min: Int,
    max: Int,
    currency: String,
    period: SalaryPeriod
): String {
    val formatter = NumberFormat.getInstance(Locale.US)
    return if (min == max) {
        "$currency ${formatter.format(min)} ${period.displayName()}"
    } else {
        "$currency ${formatter.format(min)} – ${formatter.format(max)} ${period.displayName()}"
    }
}

private fun getDaysAgo(date: Date): String {
    val diff = System.currentTimeMillis() - date.time
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        days < 1 -> "Today"
        days == 1L -> "Yesterday"
        days < 7 -> "$days days ago"
        days < 14 -> "1 week ago"
        days < 30 -> "${days / 7} weeks ago"
        days < 60 -> "1 month ago"
        else -> "${days / 30} months ago"
    }
}

private fun formatDate(date: Date): String {
    val format = SimpleDateFormat("MMM d, yyyy", Locale.US)
    return format.format(date)
}

private fun formatDeadline(date: Date): String {
    val format = SimpleDateFormat("MMM d", Locale.US)
    return format.format(date)
}

private fun isDateApproaching(date: Date, daysThreshold: Int): Boolean {
    val diff = date.time - System.currentTimeMillis()
    val daysLeft = TimeUnit.MILLISECONDS.toDays(diff)
    return daysLeft <= daysThreshold && daysLeft > 0
}

/* ───────── BUTTON DEFAULTS (Helper) ───────── */

object ButtonDefaults {
    @Composable
    fun outlinedButtonColors(
        contentColor: Color
    ) = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
        contentColor = contentColor
    )

    @Composable
    fun buttonColors(
        containerColor: Color
    ) = androidx.compose.material3.ButtonDefaults.buttonColors(
        containerColor = containerColor
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun JobDetailsPreview() {
    PivotaConnectTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            JobDetailsScreen(
                jobListing = createSampleJobListing(),
                onNavigateBack = {},
                onApplyClick = {},
                onMessageEmployerClick = {},
                onSaveToggle = { _, _ -> },
                onBookmarkClick = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 840)
@Composable
fun JobDetailsTabletPreview() {
    PivotaConnectTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            JobDetailsScreen(
                jobListing = createSampleJobListing(),
                onNavigateBack = {},
                onApplyClick = {},
                onMessageEmployerClick = {},
                onSaveToggle = { _, _ -> },
                onBookmarkClick = {}
            )
        }
    }
}

private fun createSampleJobListing(): JobListingUiModel {
    // Create a date from 3 days ago
    val postedDate = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3))

    // Create a deadline date 10 days from now
    val deadlineDate = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10))

    return JobListingUiModel(
        id = "job-123",
        title = "Senior Software Engineer",
        companyName = "Tech Innovations Africa",
        companyLogoUrl = null,
        companyRating = 4.8,
        reviewCount = 124,
        location = "Nairobi, Kenya",
        exactLocation = "Westlands, Nairobi",
        jobType = JobType.FULL_TIME,
        salaryMin = 250000,
        salaryMax = 350000,
        salaryPeriod = SalaryPeriod.PER_MONTH,
        currency = "KES",
        description = "We are looking for a talented Senior Software Engineer to join our growing team in Nairobi. You will be responsible for developing and maintaining high-quality mobile and web applications that serve users across Africa. The ideal candidate has strong problem-solving skills, experience with modern frameworks, and a passion for creating impact through technology.",
        responsibilities = listOf(
            "Design and implement scalable software solutions",
            "Collaborate with cross-functional teams to define and ship new features",
            "Mentor junior developers and conduct code reviews",
            "Optimize applications for maximum speed and scalability",
            "Troubleshoot and debug production issues"
        ),
        requirements = listOf(
            "Bachelor's degree in Computer Science or related field",
            "5+ years of experience in software development",
            "Strong knowledge of Kotlin/Java for Android or Swift for iOS",
            "Experience with RESTful APIs and cloud services (AWS/Azure)",
            "Excellent communication and teamwork skills"
        ),
        preferredQualifications = listOf(
            "Master's degree in Computer Science",
            "Experience with AI/ML technologies",
            "Knowledge of African fintech or e-commerce domains",
            "Contributions to open source projects"
        ),
        benefits = listOf(
            Benefit(BenefitIcon.HEALTH, "Comprehensive Health Insurance", "Includes dental and optical coverage for you and your family"),
            Benefit(BenefitIcon.TRANSPORT, "Transport Allowance", "Monthly stipend for commute or fuel"),
            Benefit(BenefitIcon.MEALS, "Meal Vouchers", "Daily meal allowance"),
            Benefit(BenefitIcon.TRAINING, "Learning & Development", "Annual budget for courses and conferences"),
            Benefit(BenefitIcon.BONUS, "Performance Bonus", "Annual bonus based on company and individual performance"),
            Benefit(BenefitIcon.INSURANCE, "Life Insurance", "Coverage of 3x annual salary")
        ),
        postedDate = postedDate,
        applicationDeadline = deadlineDate,
        numberOfPositions = 3,
        employerName = "Sarah Omondi",
        employerAvatarUrl = null,
        employerVerified = true,
        isSaved = false,
        isVerified = true,
        aiMatchScore = 92,
        commuteDistance = "25 min",
        tags = listOf("Remote-Friendly", "Tech", "Senior Level", "Fintech", "Hybrid"),
        applicationUrl = null,
        hasQuickApply = true
    )
}

// Alternative preview with different job type for variety
@Preview(showBackground = true)
@Composable
fun JobDetailsPartTimePreview() {
    PivotaConnectTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            JobDetailsScreen(
                jobListing = createPartTimeJobListing(),
                onNavigateBack = {},
                onApplyClick = {},
                onMessageEmployerClick = {},
                onSaveToggle = { _, _ -> },
                onBookmarkClick = {}
            )
        }
    }
}

private fun createPartTimeJobListing(): JobListingUiModel {
    val postedDate = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))
    val deadlineDate = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5))

    return JobListingUiModel(
        id = "job-456",
        title = "Customer Service Representative (Weekends)",
        companyName = "M-Kopa Solar",
        companyLogoUrl = null,
        companyRating = 4.3,
        reviewCount = 89,
        location = "Mombasa, Kenya",
        exactLocation = "Nyali, Mombasa",
        jobType = JobType.PART_TIME,
        salaryMin = 1500,
        salaryMax = 2000,
        salaryPeriod = SalaryPeriod.PER_DAY,
        currency = "KES",
        description = "Join our customer service team to assist customers with their solar energy solutions. You'll handle inquiries, troubleshoot issues, and ensure customer satisfaction during weekend shifts.",
        responsibilities = listOf(
            "Respond to customer inquiries via phone and chat",
            "Troubleshoot basic technical issues",
            "Document customer interactions in CRM",
            "Escalate complex issues to senior team"
        ),
        requirements = listOf(
            "High school diploma or equivalent",
            "6+ months customer service experience",
            "Excellent communication skills in English and Swahili",
            "Basic computer skills",
            "Available to work weekends"
        ),
        preferredQualifications = listOf(
            "Experience in call center environment",
            "Knowledge of solar energy basics",
            "CRM software experience"
        ),
        benefits = listOf(
            Benefit(BenefitIcon.TRANSPORT, "Transport allowance"),
            Benefit(BenefitIcon.MEALS, "Meals provided during shift"),
            Benefit(BenefitIcon.TRAINING, "On-the-job training")
        ),
        postedDate = postedDate,
        applicationDeadline = deadlineDate,
        numberOfPositions = 5,
        employerName = "James Mwangi",
        employerAvatarUrl = null,
        employerVerified = true,
        isSaved = true, // Preview with saved state
        isVerified = true,
        aiMatchScore = null, // No AI match for this one
        commuteDistance = "15 min",
        tags = listOf("Weekend", "Entry Level", "Customer Service", "Mombasa"),
        applicationUrl = null,
        hasQuickApply = true
    )
}